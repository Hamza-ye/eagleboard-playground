package com.mass3d.user;

public interface CurrentUserStore {

  User getUser(long id);

  UserCredentials getUserCredentialsByUsername(String username);
}
