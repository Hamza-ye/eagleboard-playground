package com.mass3d.user;

import java.util.Set;

/**
 * This interface defined methods for getting access to the currently logged in user and clearing
 * the logged in state. If no user is logged in or the auto access admin is active, all user access
 * methods will return null.
 *
 */
public interface CurrentUserService {

  String ID = CurrentUserService.class.getName();

  /**
   * @return the username of the currently logged in user. If no user is logged in or the auto
   * access admin is active, null is returned.
   */
  String getCurrentUsername();

  /**
   * @return the set of authorities granted to the currently logged in user. If no current user
   * exists, an empty set is returned.
   */
  Set<String> getCurrentUserAuthorities();

  /**
   * @return the currently logged in user. If no user is logged in or the auto access admin is
   * active, null is returned.
   */
  User getCurrentUser();

  /**
   * @return the user info for the currently logged in user. If no user is logged in or the auto
   * access admin is active, null is returned.
   */
  UserInfo getCurrentUserInfo();

  /**
   * @return the data capture organisation units of the current user, empty set
   *          if no current user.
   */
//    Set<OrganisationUnit> getCurrentUserOrganisationUnits();

  /**
   * @return true if the current logged in user has the ALL privileges set, false otherwise.
   */
  boolean currentUserIsSuper();

  /**
   * Clears the current logged in state, which means that the currently logged in user is logged
   * out.
   */
  void clearCurrentUser();

  /**
   * Indicates whether the current user has been granted the given authority.
   */
  boolean currentUserIsAuthorized(String auth);
}
