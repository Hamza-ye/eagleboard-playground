package com.mass3d.api.user;

import java.io.Serializable;
import java.util.Objects;

public class UserSettingId implements Serializable {
  User user;
  String name;

  public UserSettingId(){}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserSettingId accountId = (UserSettingId) o;
    return user.equals(accountId.user) &&
        name.equals(accountId.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, name);
  }
}
