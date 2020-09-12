package com.mass3d.api.user;

public interface CurrentUserStore {

  User getUser(long id);

  UserCredentials getUserCredentialsByUsername(String username);
}
