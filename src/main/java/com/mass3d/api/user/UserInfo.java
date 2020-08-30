package com.mass3d.api.user;

import java.util.HashSet;
import java.util.Set;

public class UserInfo {

  private Long id;

  private String username;

  private Set<String> authorities = new HashSet<>();

  protected UserInfo() {
  }

  public UserInfo(Long id, String username, Set<String> authorities) {
    this.id = id;
    this.username = username;
    this.authorities = authorities;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public static UserInfo fromUser(User user) {
    if (user == null) {
      return null;
    }

    UserCredentials credentials = user.getUserCredentials();

    return new UserInfo(credentials.getId(), credentials.getUsername(),
        credentials.getAllAuthorities());
  }

  public boolean isSuper() {
    return authorities.contains(UserAuthorityGroup.AUTHORITY_ALL);
  }

  // -------------------------------------------------------------------------
  // Get methods
  // -------------------------------------------------------------------------

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public Set<String> getAuthorities() {
    return authorities;
  }
}
