package com.mass3d.user;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.mass3d.common.AuditLogUtil;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.commons.filter.FilterUtils;
import com.mass3d.feedback.ErrorCode;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.dataset.DataSet;
import com.mass3d.system.filter.UserAuthorityGroupCanIssueFilter;
import com.mass3d.system.util.DateUtils;
import com.mass3d.period.PeriodType;
import com.mass3d.security.PasswordManager;
import com.mass3d.setting.SettingKey;
import com.mass3d.setting.SystemSettingManager;
import com.mass3d.todotask.TodoTask;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.user.UserService")
public class DefaultUserService
    implements UserService {

  private static final Log log = LogFactory.getLog(DefaultUserService.class);

  private static final int EXPIRY_THRESHOLD = 14;

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private UserStore userStore;
  private UserGroupService userGroupService;
  private UserCredentialsStore userCredentialsStore;
  private UserAuthorityGroupStore userAuthorityGroupStore;
  private CurrentUserService currentUserService;
  private SystemSettingManager systemSettingManager;
  private PasswordManager passwordManager;

  public DefaultUserService( UserStore userStore, UserGroupService userGroupService,
      UserCredentialsStore userCredentialsStore, UserAuthorityGroupStore userAuthorityGroupStore,
      CurrentUserService currentUserService, SystemSettingManager systemSettingManager,
      @Lazy PasswordManager passwordManager)
  {
    checkNotNull( userStore );
    checkNotNull( userGroupService );
    checkNotNull( userCredentialsStore );
    checkNotNull( userAuthorityGroupStore );
    checkNotNull( systemSettingManager );
    checkNotNull( passwordManager );
//    checkNotNull( sessionRegistry );

    this.userStore = userStore;
    this.userGroupService = userGroupService;
    this.userCredentialsStore = userCredentialsStore;
    this.userAuthorityGroupStore = userAuthorityGroupStore;
    this.currentUserService = currentUserService;
    this.systemSettingManager = systemSettingManager;
    this.passwordManager = passwordManager;
//    this.sessionRegistry = sessionRegistry;
  }

  @Autowired
  public void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  @Autowired
  public void setUserGroupService(UserGroupService userGroupService) {
    this.userGroupService = userGroupService;
  }

  @Autowired
  public void setUserCredentialsStore(UserCredentialsStore userCredentialsStore) {
    this.userCredentialsStore = userCredentialsStore;
  }

  @Autowired
  public void setUserAuthorityGroupStore(UserAuthorityGroupStore userAuthorityGroupStore) {
    this.userAuthorityGroupStore = userAuthorityGroupStore;
  }

  @Autowired
  public void setCurrentUserService(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

//  public void setSystemSettingManager(SystemSettingManager systemSettingManager) {
//    this.systemSettingManager = systemSettingManager;
//  }

  @Autowired
  public void setPasswordManager(PasswordManager passwordManager) {
    this.passwordManager = passwordManager;
  }

  // -------------------------------------------------------------------------
  // UserService implementation
  // -------------------------------------------------------------------------

  // -------------------------------------------------------------------------
  // User
  // -------------------------------------------------------------------------

  @Override
  @Transactional
  public Long addUser(User user) {
    AuditLogUtil.infoWrapper(log, currentUserService.getCurrentUsername(), user,
        AuditLogUtil.ACTION_CREATE);

    userStore.save(user);

    return user.getId();
  }

  @Override
  @Transactional
  public void updateUser(User user) {
    userStore.update(user);

    AuditLogUtil.infoWrapper(log, currentUserService.getCurrentUsername(), user,
        AuditLogUtil.ACTION_UPDATE);
  }

  @Override
  @Transactional
  public void deleteUser(User user) {
    AuditLogUtil.infoWrapper(log, currentUserService.getCurrentUsername(), user,
        AuditLogUtil.ACTION_DELETE);

    userStore.delete(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    return userStore.getAll();
  }

  @Override
  @Transactional(readOnly = true)
  public User getUser(Long userId) {
    return userStore.get(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public User getUser(String uid) {
    return userStore.getByUid(uid);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getUsers(Collection<String> uids) {
    return userStore.getByUid(uids);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsersBetweenByName(String name, int first, int max) {
    UserQueryParams params = new UserQueryParams();
    params.setQuery(name);
    params.setFirst(first);
    params.setMax(max);

    return userStore.getUsers(params);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getUsers(UserQueryParams params) {
    return getUsers(params, null);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getUsers(UserQueryParams params, @Nullable List<String> orders) {
    handleUserQueryParams(params);

    if (!validateUserQueryParams(params)) {
      return Lists.newArrayList();
    }

    return userStore.getUsers(params, orders);
  }

  @Override
  @Transactional(readOnly = true)
  public int getUserCount(UserQueryParams params) {
    handleUserQueryParams(params);

    if (!validateUserQueryParams(params)) {
      return 0;
    }

    return userStore.getUserCount(params);
  }

  @Override
  @Transactional(readOnly = true)
  public int getUserCount() {
    return userStore.getUserCount();
  }

  private void handleUserQueryParams(UserQueryParams params) {
    boolean canGrantOwnRoles = (Boolean) systemSettingManager
        .getSystemSetting(SettingKey.CAN_GRANT_OWN_USER_AUTHORITY_GROUPS);
    params.setDisjointRoles(!canGrantOwnRoles);

    if (!params.hasUser()) {
      params.setUser(currentUserService.getCurrentUser());
    }

    if (params.hasUser() && params.getUser().isSuper()) {
      params.setCanManage(false);
      params.setAuthSubset(false);
      params.setDisjointRoles(false);
    }

    if (params.getInactiveMonths() != null) {
      Calendar cal = PeriodType.createCalendarInstance();
      cal.add(Calendar.MONTH, (params.getInactiveMonths() * -1));
      params.setInactiveSince(cal.getTime());
    }
    // Todo Eagle commented out in handleUserQueryParams()
//        if ( params.isUserOrgUnits() && params.hasUser() )
//        {
//            params.setTodoTasks( Lists.newArrayList( params.getUser().getTodoTasks() ) );
//        }
  }

  private boolean validateUserQueryParams(UserQueryParams params) {
    if (params.isCanManage() && (params.getUser() == null || !params.getUser()
        .hasManagedGroups())) {
      log.warn("Cannot get managed users as user does not have any managed groups");
      return false;
    }

    if (params.isAuthSubset() && (params.getUser() == null || !params.getUser().getUserCredentials()
        .hasAuthorities())) {
      log.warn("Cannot get users with authority subset as user does not have any authorities");
      return false;
    }

    if (params.isDisjointRoles() && (params.getUser() == null || !params.getUser()
        .getUserCredentials().hasUserAuthorityGroups())) {
      log.warn("Cannot get users with disjoint roles as user does not have any user roles");
      return false;
    }

    return true;
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getUsersByPhoneNumber(String phoneNumber) {
    UserQueryParams params = new UserQueryParams();
    params.setPhoneNumber(phoneNumber);
    return getUsers(params);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isLastSuperUser(UserCredentials userCredentials) {
    if (!userCredentials.isSuper()) {
      return false; // Cannot be last if not super user
    }

    Collection<UserCredentials> users = userCredentialsStore.getAll();

    for (UserCredentials user : users) {
      if (user.isSuper() && !user.equals(userCredentials)) {
        return false;
      }
    }

    return true;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isLastSuperRole(UserAuthorityGroup userAuthorityGroup) {
    Collection<UserAuthorityGroup> groups = userAuthorityGroupStore.getAll();

    for (UserAuthorityGroup group : groups) {
      if (group.isSuper() && group.getId() != userAuthorityGroup.getId()) {
        return false;
      }
    }

    return true;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean canAddOrUpdateUser(Collection<String> userGroups) {
    return canAddOrUpdateUser(userGroups, currentUserService.getCurrentUser());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean canAddOrUpdateUser(Collection<String> userGroups, User currentUser) {
    if (currentUser == null) {
      return false;
    }

    boolean canAdd = currentUser.getUserCredentials().isAuthorized(UserGroup.AUTH_USER_ADD);

    if (canAdd) {
      return true;
    }

    boolean canAddInGroup = currentUser.getUserCredentials()
        .isAuthorized(UserGroup.AUTH_USER_ADD_IN_GROUP);

    if (!canAddInGroup) {
      return false;
    }

    boolean canManageAnyGroup = false;

    for (String uid : userGroups) {
      UserGroup userGroup = userGroupService.getUserGroup(uid);

      if (currentUser.canManage(userGroup)) {
        canManageAnyGroup = true;
        break;
      }
    }

    return canManageAnyGroup;
  }

  // -------------------------------------------------------------------------
  // UserAuthorityGroup
  // -------------------------------------------------------------------------

  @Override
  @Transactional
  public Long addUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup) {
    userAuthorityGroupStore.save(userAuthorityGroup);
    return userAuthorityGroup.getId();
  }

  @Override
  @Transactional
  public void updateUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup) {
    userAuthorityGroupStore.update(userAuthorityGroup);
  }

  @Override
  @Transactional
  public void deleteUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup) {
    userAuthorityGroupStore.delete(userAuthorityGroup);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAuthorityGroup> getAllUserAuthorityGroups() {
    return userAuthorityGroupStore.getAll();
  }

  @Override
  @Transactional(readOnly = true)
  public UserAuthorityGroup getUserAuthorityGroup(Long id) {
    return userAuthorityGroupStore.get(id);
  }

  @Override
  @Transactional(readOnly = true)
  public UserAuthorityGroup getUserAuthorityGroup(String uid) {
    return userAuthorityGroupStore.getByUid(uid);
  }

  @Override
  @Transactional(readOnly = true)
  public UserAuthorityGroup getUserAuthorityGroupByName(String name) {
    return userAuthorityGroupStore.getByName(name);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAuthorityGroup> getUserRolesByUid(Collection<String> uids) {
    return userAuthorityGroupStore.getByUid(uids);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAuthorityGroup> getUserRolesBetween(int first, int max) {
    return userAuthorityGroupStore.getAllOrderedName(first, max);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAuthorityGroup> getUserRolesBetweenByName(String name, int first, int max) {
    return userAuthorityGroupStore.getAllLikeName(name, first, max);
  }

//    @Override
//    @Transactional(readOnly = true)
//    public int countDataSetUserAuthorityGroups( DataSet dataSet )
//    {
//        return userAuthorityGroupStore.countDataSetUserAuthorityGroups( dataSet );
//    }

  // Todo Eagle added function countFieldSetUserAuthorityGroups( FieldSet fieldSet )
  @Override
  @Transactional(readOnly = true)
  public int countFieldSetUserAuthorityGroups(DataSet fieldSet) {
    return userAuthorityGroupStore.countFieldSetUserAuthorityGroups(fieldSet);
  }

  // Todo Eagle added function countTodoTaskUserAuthorityGroups( TodoTask todoTask )
  @Override
  @Transactional(readOnly = true)
  public int countTodoTaskUserAuthorityGroups(TodoTask todoTask) {
    return userAuthorityGroupStore.countTodoTaskUserAuthorityGroups(todoTask);
  }

  @Override
  @Transactional(readOnly = true)
  public void canIssueFilter(Collection<UserAuthorityGroup> userRoles) {
    User user = currentUserService.getCurrentUser();

    boolean canGrantOwnUserAuthorityGroups = (Boolean) systemSettingManager
        .getSystemSetting(SettingKey.CAN_GRANT_OWN_USER_AUTHORITY_GROUPS);

    FilterUtils.filter(userRoles,
        new UserAuthorityGroupCanIssueFilter(user, canGrantOwnUserAuthorityGroups));
  }

  // -------------------------------------------------------------------------
  // UserCredentials
  // -------------------------------------------------------------------------

  @Override
  @Transactional
  public Long addUserCredentials(UserCredentials userCredentials) {
    userCredentialsStore.save(userCredentials);
    return userCredentials.getId();
  }

  @Override
  @Transactional
  public void updateUserCredentials(UserCredentials userCredentials) {
    userCredentialsStore.update(userCredentials);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserCredentials> getAllUserCredentials() {
    return userCredentialsStore.getAll();
  }

  @Override
  @Transactional
  public void encodeAndSetPassword(User user, String rawPassword) {
    encodeAndSetPassword(user.getUserCredentials(), rawPassword);
  }

  @Override
  @Transactional
  public void encodeAndSetPassword(UserCredentials userCredentials, String rawPassword) {
    if (StringUtils.isEmpty(rawPassword) && !userCredentials.isExternalAuth()) {
      return; // Leave unchanged if internal authentication and no password supplied
    }

    if (userCredentials.isExternalAuth()) {
      userCredentials.setPassword(UserService.PW_NO_INTERNAL_LOGIN);

      return; // Set unusable, not-encoded password if external authentication
    }

    boolean isNewPassword = StringUtils.isBlank(userCredentials.getPassword()) ||
        !passwordManager.matches(rawPassword, userCredentials.getPassword());

    if (isNewPassword) {
      userCredentials.setPasswordLastUpdated(new Date());
    }

    // Encode and set password

    userCredentials.setPassword(passwordManager.encode(rawPassword));
    userCredentials.getPreviousPasswords().add(passwordManager.encode(rawPassword));
  }

  @Override
  @Transactional(readOnly = true)
  public UserCredentials getUserCredentialsByUsername(String username) {
    return userCredentialsStore.getUserCredentialsByUsername(username);
  }

  @Override
  @Transactional(readOnly = true)
  public UserCredentials getUserCredentialsByOpenId(String openId) {
    return userCredentialsStore.getUserCredentialsByOpenId(openId);
  }

  @Override
  @Transactional(readOnly = true)
  public UserCredentials getUserCredentialsByLdapId(String ldapId) {
    return userCredentialsStore.getUserCredentialsByLdapId(ldapId);
  }

  @Override
  @Transactional
  public void setLastLogin(String username) {
    UserCredentials credentials = getUserCredentialsByUsername(username);

    if (credentials != null) {
      credentials.setLastLogin(new Date());
      updateUserCredentials(credentials);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public int getActiveUsersCount(int days) {
    Calendar cal = PeriodType.createCalendarInstance();
    cal.add(Calendar.DAY_OF_YEAR, (days * -1));

    return getActiveUsersCount(cal.getTime());
  }

  @Override
  @Transactional(readOnly = true)
  public int getActiveUsersCount(Date since) {
    UserQueryParams params = new UserQueryParams();
    params.setLastLogin(since);

    return getUserCount(params);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean credentialsNonExpired(UserCredentials credentials) {
    int credentialsExpires = systemSettingManager.credentialsExpires();

    if (credentialsExpires == 0) {
      return true;
    }

    if (credentials == null || credentials.getPasswordLastUpdated() == null) {
      return true;
    }

    int months = DateUtils.monthsBetween(credentials.getPasswordLastUpdated(), new Date());

    return months < credentialsExpires;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ErrorReport> validateUser(User user, User currentUser) {
    List<ErrorReport> errors = new ArrayList<>();

    if (currentUser == null || currentUser.getUserCredentials() == null || user == null
        || user.getUserCredentials() == null) {
      return errors;
    }

    // Validate user role

    boolean canGrantOwnUserAuthorityGroups = (Boolean) systemSettingManager
        .getSystemSetting(SettingKey.CAN_GRANT_OWN_USER_AUTHORITY_GROUPS);

    List<UserAuthorityGroup> roles = userAuthorityGroupStore.getByUid(
        user.getUserCredentials().getUserAuthorityGroups().stream()
            .map(BaseIdentifiableObject::getUid).collect(Collectors.toList()));

    roles.forEach(ur ->
    {
      if (!currentUser.getUserCredentials().canIssueUserRole(ur, canGrantOwnUserAuthorityGroups)) {
        errors.add(
            new ErrorReport(UserAuthorityGroup.class, ErrorCode.E3003, currentUser.getUsername(),
                ur.getName()));
      }
    });

    // Validate user group

    boolean canAdd = currentUser.getUserCredentials().isAuthorized(UserGroup.AUTH_USER_ADD);

    if (canAdd) {
      return errors;
    }

    boolean canAddInGroup = currentUser.getUserCredentials()
        .isAuthorized(UserGroup.AUTH_USER_ADD_IN_GROUP);

    if (!canAddInGroup) {
      errors.add(new ErrorReport(UserGroup.class, ErrorCode.E3004, currentUser));
      return errors;
    }

    user.getGroups().forEach(ug ->
    {
      if (!(currentUser.canManage(ug) || userGroupService.canAddOrRemoveMember(ug.getUid()))) {
        errors.add(new ErrorReport(UserGroup.class, ErrorCode.E3005, currentUser, ug));
      }
    });

    return errors;
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getExpiringUsers() {
    int daysBeforePasswordChangeRequired =
        (Integer) systemSettingManager.getSystemSetting(SettingKey.CREDENTIALS_EXPIRES) * 30;

    Date daysPassed = new DateTime(new Date())
        .minusDays(daysBeforePasswordChangeRequired - EXPIRY_THRESHOLD).toDate();

    UserQueryParams userQueryParams = new UserQueryParams()
        .setDisabled(false)
        .setPasswordLastUpdated(daysPassed);

    return userStore.getExpiringUsers(userQueryParams);
  }

  public void set2FA(User user, Boolean twoFa) {
    user.getUserCredentials().setTwoFA(twoFa);

    updateUser(user);
  }
}
