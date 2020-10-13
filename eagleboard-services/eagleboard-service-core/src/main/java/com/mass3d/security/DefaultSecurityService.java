package com.mass3d.security;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.cache.Cache;
import com.mass3d.cache.CacheProvider;
import com.mass3d.common.CodeGenerator;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.i18n.I18n;
import com.mass3d.i18n.I18nManager;
import com.mass3d.i18n.locale.LocaleManager;
import com.mass3d.message.MessageSender;
import com.mass3d.period.Cal;
import com.mass3d.security.acl.AclService;
import com.mass3d.setting.SettingKey;
import com.mass3d.setting.SystemSettingManager;
import com.mass3d.system.util.JacksonUtils;
import com.mass3d.system.util.ValidationUtils;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserService;
import com.mass3d.user.UserSettingKey;
import com.mass3d.user.UserSettingService;
import com.mass3d.util.ObjectUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service( "com.mass3d.security.SecurityService" )
public class DefaultSecurityService
    implements SecurityService {

  private static final Log log = LogFactory.getLog(DefaultSecurityService.class);

  private static final String RESTORE_PATH = "/dhis-web-commons/security/";
  private static final Pattern INVITE_USERNAME_PATTERN = Pattern
      .compile("^invite\\-(.+?)\\-(\\w{11})$");
  private static final String TBD_NAME = "(TBD)";

  private static final String DEFAULT_APPLICATION_TITLE = "DHIS 2";

  private static final int INVITED_USER_PASSWORD_LENGTH = 40;

  private static final int RESTORE_TOKEN_LENGTH = 50;
  private static final int LOGIN_MAX_FAILED_ATTEMPTS = 5;
  private static final int LOGIN_LOCKOUT_MINS = 15;

  private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

  private Cache<Integer> userFailedLoginAttemptCache;

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  @Autowired
  private CurrentUserService currentUserService;

  @Autowired
  private UserSettingService userSettingService;

  @Autowired
  private AclService aclService;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private CacheProvider cacheProvider;

  private final PasswordManager passwordManager;
  private final MessageSender emailMessageSender;
  private final UserService userService;
  private final SystemSettingManager systemSettingManager;
  private final I18nManager i18nManager;

  public DefaultSecurityService(
      @Lazy PasswordManager passwordManager,
      MessageSender emailMessageSender,
      UserService userService,
      SystemSettingManager systemSettingManager,
      I18nManager i18nManager )
  {
    checkNotNull( passwordManager );
    checkNotNull( emailMessageSender );
    checkNotNull( userService );
    checkNotNull( systemSettingManager );
    checkNotNull( i18nManager );

    this.passwordManager = passwordManager;
    this.emailMessageSender = emailMessageSender;
    this.userService = userService;
    this.systemSettingManager = systemSettingManager;
    this.i18nManager = i18nManager;
  }
//  public DefaultSecurityService(
//      CurrentUserService currentUserService,
//      UserSettingService userSettingService,
//      AclService aclService,
//      RestTemplate restTemplate,
//      CacheProvider cacheProvider,
//      @Lazy PasswordManager passwordManager,
//      MessageSender emailMessageSender,
//      UserService userService,
//      SystemSettingManager systemSettingManager,
//      I18nManager i18nManager )
//  {
//    checkNotNull( currentUserService );
//    checkNotNull( userSettingService );
//    checkNotNull( aclService );
//    checkNotNull( restTemplate );
//    checkNotNull( cacheProvider );
//    checkNotNull( passwordManager );
//    checkNotNull( emailMessageSender );
//    checkNotNull( userService );
//    checkNotNull( systemSettingManager );
//    checkNotNull( i18nManager );
//
//    this.currentUserService = currentUserService;
//    this.userSettingService = userSettingService;
//    this.aclService = aclService;
//    this.restTemplate = restTemplate;
//    this.cacheProvider = cacheProvider;
//    this.passwordManager = passwordManager;
//    this.emailMessageSender = emailMessageSender;
//    this.userService = userService;
//    this.systemSettingManager = systemSettingManager;
//    this.i18nManager = i18nManager;
//  }
//  public void setPasswordManager(PasswordManager passwordManager) {
//    this.passwordManager = passwordManager;
//  }
//
//  public void setEmailMessageSender(MessageSender emailMessageSender) {
//    this.emailMessageSender = emailMessageSender;
//  }
//
//  public void setUserService(UserService userService) {
//    this.userService = userService;
//  }
//
//  public void setSystemSettingManager(SystemSettingManager systemSettingManager) {
//    this.systemSettingManager = systemSettingManager;
//  }
//
//  public void setI18nManager(I18nManager i18nManager) {
//    this.i18nManager = i18nManager;
//  }

  // -------------------------------------------------------------------------
  // Initialization
  // -------------------------------------------------------------------------


  @PostConstruct
  public void init() {
    this.userFailedLoginAttemptCache = cacheProvider.newCacheBuilder(Integer.class)
        .forRegion("userFailedLoginAttempt").expireAfterWrite(LOGIN_LOCKOUT_MINS, TimeUnit.MINUTES)
        .withDefaultValue(0).build();

  }

  // -------------------------------------------------------------------------
  // SecurityService implementation
  // -------------------------------------------------------------------------

  @Override
  public void registerFailedLogin(String username) {
    if (!isBlockFailedLogins() || username == null) {
      return;
    }

    Integer attempts = userFailedLoginAttemptCache.get(username).orElse(0);

    attempts++;

    userFailedLoginAttemptCache.put(username, attempts);
  }

  @Override
  public void registerSuccessfulLogin(String username) {
    if (!isBlockFailedLogins() || username == null) {
      return;
    }

    userFailedLoginAttemptCache.invalidate(username);
  }

  @Override
  public boolean isLocked(String username) {
    if (!isBlockFailedLogins() || username == null) {
      return false;
    }

    return userFailedLoginAttemptCache.get(username).orElse(0) > LOGIN_MAX_FAILED_ATTEMPTS;
  }

  private boolean isBlockFailedLogins() {
    return (Boolean) systemSettingManager.getSystemSetting(SettingKey.LOCK_MULTIPLE_FAILED_LOGINS);
  }

  @Override
  public boolean prepareUserForInvite(User user) {
    if (user == null || user.getUserCredentials() == null) {
      return false;
    }

    if (user.getUsername() == null || user.getUsername().isEmpty()) {
      String username = "invite-" + user.getEmail() + "-" + CodeGenerator.generateUid();

      user.getUserCredentials().setUsername(username);
    }

    String rawPassword = CodeGenerator.generateCode(INVITED_USER_PASSWORD_LENGTH);

    user.setSurname(StringUtils.isEmpty(user.getSurname()) ? TBD_NAME : user.getSurname());
    user.setFirstName(StringUtils.isEmpty(user.getFirstName()) ? TBD_NAME : user.getFirstName());
    user.getUserCredentials().setInvitation(true);
    userService.encodeAndSetPassword(user, rawPassword);

    return true;
  }

  @Override
  public String validateRestore(UserCredentials credentials) {
    if (!emailMessageSender.isConfigured()) {
      log.warn("Could not send restore/invite message as email is not configured");
      return "email_not_configured_for_system";
    }

    if (credentials == null || credentials.getUserInfo() == null) {
      log.warn("Could not send restore/invite message as user is null: " + credentials);
      return "no_user_credentials";
    }

    if (credentials.getUserInfo().getEmail() == null || !ValidationUtils
        .emailIsValid(credentials.getUserInfo().getEmail())) {
      log.warn("Could not send restore/invite message as user has no email or email is invalid");
      return "user_does_not_have_valid_email";
    }

    return null;
  }

  @Override
  public String validateInvite(UserCredentials credentials) {
    if (credentials == null) {
      log.warn("Could not send invite message as user does not exist");
      return "no_user_credentials";
    }

    if (credentials.getUsername() != null
        && userService.getUserCredentialsByUsername(credentials.getUsername()) != null) {
      log.warn("Could not send invite message as username is already taken: " + credentials);
      return "username_taken";
    }

    return validateRestore(credentials);
  }

  @Override
  public boolean sendRestoreMessage(UserCredentials credentials, String rootPath,
      RestoreOptions restoreOptions) {
    if (credentials == null || restoreOptions == null) {
      return false;
    }

    if (validateRestore(credentials) != null) {
      return false;
    }

    RestoreType restoreType = restoreOptions.getRestoreType();

    String applicationTitle = (String) systemSettingManager
        .getSystemSetting(SettingKey.APPLICATION_TITLE);

    if (applicationTitle == null || applicationTitle.isEmpty()) {
      applicationTitle = DEFAULT_APPLICATION_TITLE;
    }

    String[] result = initRestore(credentials, restoreOptions);

    Set<User> users = new HashSet<>();
    users.add(credentials.getUserInfo());

    Map<String, Object> vars = new HashMap<>();
    vars.put("applicationTitle", applicationTitle);
    vars.put("restorePath", rootPath + RESTORE_PATH + restoreType.getAction());
    vars.put("token", result[0]);
    vars.put("username", credentials.getUsername());
    vars.put("welcomeMessage", credentials.getUserInfo().getWelcomeMessage());

    User user = credentials.getUserInfo();
    Locale locale = (Locale) userSettingService.getUserSetting(UserSettingKey.UI_LOCALE, user);
    locale = ObjectUtils.firstNonNull(locale, LocaleManager.DEFAULT_LOCALE);

    I18n i18n = i18nManager.getI18n(locale);
    vars.put("i18n", i18n);

    rootPath = rootPath.replace("http://", "").replace("https://", "");

    // -------------------------------------------------------------------------
    // Render emails
    // -------------------------------------------------------------------------

//    VelocityManager vm = new VelocityManager();
//
//    String text1 = vm.render(vars, restoreType.getEmailTemplate() + "1");
//
//    String subject1 = i18n.getString(restoreType.getEmailSubject()) + " " + rootPath;
//
//    // -------------------------------------------------------------------------
//    // Send emails
//    // -------------------------------------------------------------------------
//
//    emailMessageSender.sendMessage(subject1, text1, null, null, users, true);

    return true;
  }

  @Override
  public String[] initRestore(UserCredentials credentials, RestoreOptions restoreOptions) {
    String token =
        restoreOptions.getTokenPrefix() + CodeGenerator.generateCode(RESTORE_TOKEN_LENGTH);

    String hashedToken = passwordManager.encode(token);

    RestoreType restoreType = restoreOptions.getRestoreType();

    Date expiry = new Cal().now()
        .add(restoreType.getExpiryIntervalType(), restoreType.getExpiryIntervalCount()).time();

    credentials.setRestoreToken(hashedToken);
    credentials.setRestoreExpiry(expiry);

    userService.updateUserCredentials(credentials);

    return new String[]{token};
  }

  @Override
  public RestoreOptions getRestoreOptions(String token) {
    return RestoreOptions.getRestoreOptions(token);
  }

  @Override
  public boolean restore(UserCredentials credentials, String token, String newPassword,
      RestoreType restoreType) {
    if (credentials == null || token == null || newPassword == null
        || !canRestore(credentials, token, restoreType)) {
      return false;
    }

    credentials.setRestoreToken(null);
    credentials.setRestoreExpiry(null);
    credentials.setInvitation(false);

    userService.encodeAndSetPassword(credentials, newPassword);
    userService.updateUserCredentials(credentials);

    return true;
  }

  @Override
  public boolean canRestore(UserCredentials credentials, String token, RestoreType restoreType) {
    String logPrefix =
        "Restore user: " + credentials.getUid() + ", username: " + credentials.getUsername() + " ";

    String errorMessage = verifyRestore(credentials, token, restoreType);

    if (errorMessage != null) {
      log.info(logPrefix + "Failed to verify restore: " + errorMessage);
      return false;
    }

    log.info(logPrefix + " success");
    return true;
  }

  /**
   * Verifies all parameters needed for account restore and checks validity of the user supplied
   * token and code. If the restore cannot be verified a descriptive error string is returned.
   *
   * @param credentials the user credentials.
   * @param token the user supplied token.
   * @param restoreType the restore type.
   * @return null if restore is valid, a descriptive error string otherwise.
   */
  private String verifyRestore(UserCredentials credentials, String token, RestoreType restoreType) {
    String errorMessage = credentials.isRestorable();

    if (errorMessage != null) {
      return errorMessage;
    }

    errorMessage = verifyToken(credentials, token, restoreType);

    if (errorMessage != null) {
      return errorMessage;
    }

    Date currentTime = new DateTime().toDate();
    Date restoreExpiry = credentials.getRestoreExpiry();

    if (currentTime.after(restoreExpiry)) {
      return "date_is_after_expiry";
    }

    return null; // Success;
  }

  /**
   * Verify the token given for a user invite or password restore operation.
   * <p/>
   * If error, returns one of the following strings:
   * <p/>
   * <ul>
   * <li>credentials_parameter_is_null</li>
   * <li>token_parameter_is_null</li>
   * <li>restore_type_parameter_is_null</li>
   * <li>cannot_parse_restore_options</li>
   * <li>wrong_prefix_for_restore_type</li>
   * <li>could_not_verify_token</li>
   * <li>restore_token_does_not_match_supplied_token</li>
   * </ul>
   *
   * @param credentials the user credentials.
   * @param token the token.
   * @param restoreType type of restore operation.
   * @return null if success, otherwise error string.
   */
  @Override
  public String verifyToken(UserCredentials credentials, String token, RestoreType restoreType) {
    if (credentials == null) {
      return "credentials_parameter_is_null";
    }

    if (token == null) {
      return "token_parameter_is_null";
    }

    if (restoreType == null) {
      return "restore_type_parameter_is_null";
    }

    RestoreOptions restoreOptions = RestoreOptions.getRestoreOptions(token);

    if (restoreOptions == null) {
      return "cannot_parse_restore_options";
    }

    if (restoreType != restoreOptions.getRestoreType()) {
      return "wrong_prefix_for_restore_type";
    }

    String restoreToken = credentials.getRestoreToken();

    if (restoreToken == null) {
      return "could_not_verify_token";
    }

    boolean validToken = passwordManager.matches(token, restoreToken);

    return validToken ? null : "restore_token_does_not_match_supplied_token";
  }

  @Override
  public boolean isInviteUsername(String username) {
    if (username == null || username.isEmpty()) {
      return true;
    }

    return INVITE_USERNAME_PATTERN.matcher(username).matches();
  }

  @Override
  public boolean canCreatePublic(IdentifiableObject identifiableObject) {
    return !aclService.isShareable(identifiableObject.getClass())
        || aclService
        .canMakePublic(currentUserService.getCurrentUser(), identifiableObject.getClass());
  }

  @Override
  public boolean canCreatePublic(String type) {
    Class<? extends IdentifiableObject> klass = aclService.classForType(type);

    return !aclService.isShareable(klass)
        || aclService.canMakePublic(currentUserService.getCurrentUser(), klass);
  }

  @Override
  public boolean canCreatePrivate(IdentifiableObject identifiableObject) {
    return !aclService.isShareable(identifiableObject.getClass())
        || aclService
        .canMakePrivate(currentUserService.getCurrentUser(), identifiableObject.getClass());
  }

  @Override
  public boolean canView(String type) {
    boolean requireAddToView = (Boolean) systemSettingManager
        .getSystemSetting(SettingKey.REQUIRE_ADD_TO_VIEW);

    return !requireAddToView || (canCreatePrivate(type) || canCreatePublic(type));
  }

  @Override
  public boolean canCreatePrivate(String type) {
    Class<? extends IdentifiableObject> klass = aclService.classForType(type);

    return !aclService.isShareable(klass)
        || aclService.canMakePrivate(currentUserService.getCurrentUser(), klass);
  }

  @Override
  public boolean canRead(IdentifiableObject identifiableObject) {
    return !aclService.isSupported(identifiableObject.getClass())
        || aclService.canRead(currentUserService.getCurrentUser(), identifiableObject);
  }

  @Override
  public boolean canWrite(IdentifiableObject identifiableObject) {
    return !aclService.isSupported(identifiableObject.getClass())
        || aclService.canWrite(currentUserService.getCurrentUser(), identifiableObject);
  }

  @Override
  public boolean canUpdate(IdentifiableObject identifiableObject) {
    return !aclService.isSupported(identifiableObject.getClass())
        || aclService.canUpdate(currentUserService.getCurrentUser(), identifiableObject);
  }

  @Override
  public boolean canDelete(IdentifiableObject identifiableObject) {
    return !aclService.isSupported(identifiableObject.getClass())
        || aclService.canDelete(currentUserService.getCurrentUser(), identifiableObject);
  }

  @Override
  public boolean canManage(IdentifiableObject identifiableObject) {
    return !aclService.isShareable(identifiableObject.getClass())
        || aclService.canManage(currentUserService.getCurrentUser(), identifiableObject);
  }

  @Override
  public boolean hasAnyAuthority(String... authorities) {
    User user = currentUserService.getCurrentUser();

    if (user != null && user.getUserCredentials() != null) {
      UserCredentials userCredentials = user.getUserCredentials();

      for (String authority : authorities) {
        if (userCredentials.isAuthorized(authority)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public RecaptchaResponse verifyRecaptcha(String key, String remoteIp)
      throws IOException {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    params
        .add("secret", (String) systemSettingManager.getSystemSetting(SettingKey.RECAPTCHA_SECRET));
    params.add("response", key);
    params.add("remoteip", remoteIp);

    String result = restTemplate.postForObject(RECAPTCHA_VERIFY_URL, params, String.class);

    log.info("Recaptcha result: " + result);

    return JacksonUtils.fromJson(result, RecaptchaResponse.class);
  }

  @Override
  public boolean canDataWrite(IdentifiableObject identifiableObject) {
    return !aclService.isSupported(identifiableObject.getClass())
        || aclService.canDataWrite(currentUserService.getCurrentUser(), identifiableObject);
  }

  @Override
  public boolean canDataRead(IdentifiableObject identifiableObject) {
    return !aclService.isSupported(identifiableObject.getClass())
        || aclService.canDataRead(currentUserService.getCurrentUser(), identifiableObject);
  }
}