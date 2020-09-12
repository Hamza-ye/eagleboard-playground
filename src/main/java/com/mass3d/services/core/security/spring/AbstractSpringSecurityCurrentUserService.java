package com.mass3d.services.core.security.spring;

import com.google.api.client.util.Sets;
import com.mass3d.api.user.CurrentUserService;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AbstractSpringSecurityCurrentUserService
    implements CurrentUserService {

  @Override
  public String getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()
        || authentication.getPrincipal() == null) {
      return null;
    }

    // Principal being a string implies anonymous authentication

    if (authentication.getPrincipal() instanceof String) {
      String principal = (String) authentication.getPrincipal();

      if (principal.compareTo("anonymousUser") != 0) {
        return null;
      }

      return principal;
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    return userDetails.getUsername();
  }

  @Override
  public Set<String> getCurrentUserAuthorities() {
    UserDetails userDetails = getCurrentUserDetails();

    if (userDetails == null) {
      return Sets.newHashSet();
    }

    return userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }

  /**
   * Returns the current UserDetails, or null of there is no current user or if principal is not of
   * type UserDetails.
   */
  protected UserDetails getCurrentUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() ||
        authentication.getPrincipal() == null || !(authentication
        .getPrincipal() instanceof UserDetails)) {
      return null;
    }

    return (UserDetails) authentication.getPrincipal();
  }

  @Override
  public void clearCurrentUser() {
    SecurityContextHolder.clearContext();
  }
}
