package com.mass3d.services.core.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mass3d.api.user.CurrentUserStore;
import com.mass3d.api.user.User;
import com.mass3d.api.user.UserCredentials;
import com.mass3d.api.user.UserInfo;
import com.mass3d.services.core.security.spring.AbstractSpringSecurityCurrentUserService;
import com.mass3d.support.commons.util.SystemUtils;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving information about the currently authenticated user.
 * <p>
 * Note that most methods are transactional, except for retrieving current UserInfo.
 *
 */
@Service
public class DefaultCurrentUserService
    extends AbstractSpringSecurityCurrentUserService {

  /**
   * Cache for user IDs. Key is username. Disabled during test phase. Take care not to cache user
   * info which might change during runtime.
   */
  private static final Cache<String, Long> USERNAME_ID_CACHE = Caffeine.newBuilder()
      .expireAfterAccess(1, TimeUnit.HOURS)
      .initialCapacity(200)
      .maximumSize(SystemUtils.isTestRun() ? 0 : 4000)
      .build();

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  @Autowired
  private CurrentUserStore currentUserStore;

  // -------------------------------------------------------------------------
  // CurrentUserService implementation
  // -------------------------------------------------------------------------

  @Override
  @Transactional(readOnly = true)
  public User getCurrentUser() {
    String username = getCurrentUsername();

    if (username == null) {
      return null;
    }

    Long userId = USERNAME_ID_CACHE.get(username, this::getUserId);

    if (userId == null) {
      return null;
    }

    return currentUserStore.getUser(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public UserInfo getCurrentUserInfo() {
    UserDetails userDetails = getCurrentUserDetails();

    if (userDetails == null) {
      return null;
    }

    Long userId = USERNAME_ID_CACHE.get(userDetails.getUsername(), un -> getUserId(un));

    if (userId == null) {
      return null;
    }

    Set<String> authorities = userDetails.getAuthorities()
        .stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());

    return new UserInfo(userId, userDetails.getUsername(), authorities);
  }

  private Long getUserId(String username) {
    UserCredentials credentials = currentUserStore.getUserCredentialsByUsername(username);

    return credentials != null ? credentials.getId() : null;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean currentUserIsSuper() {
    User user = getCurrentUser();

    return user != null && user.isSuper();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean currentUserIsAuthorized(String auth) {
    User user = getCurrentUser();

    return user != null && user.getUserCredentials().isAuthorized(auth);
  }
}
