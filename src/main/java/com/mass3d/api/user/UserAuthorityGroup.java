package com.mass3d.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;
import com.mass3d.api.common.BaseIdentifiableObject;
import com.mass3d.api.common.MetadataObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "userroleid"))
public class UserAuthorityGroup
    extends BaseIdentifiableObject implements MetadataObject {

  public static final String AUTHORITY_ALL = "ALL";

  public static final String[] CRITICAL_AUTHS = {"ALL", "F_SCHEDULING_ADMIN", "F_SYSTEM_SETTING",
      "F_SQLVIEW_PUBLIC_ADD", "F_SQLVIEW_PRIVATE_ADD", "F_SQLVIEW_DELETE",
      "F_USERROLE_PUBLIC_ADD", "F_USERROLE_PRIVATE_ADD", "F_USERROLE_DELETE"};

  /**
   * Required and unique.
   */
  private String description;

  @ElementCollection
  @CollectionTable(name="userroleauthorities", joinColumns=@JoinColumn(name="userroleid"))
  @Column(name="authority")
  private Set<String> authorities = new HashSet<>();

  @ManyToMany
  @JoinTable(name = "userrolemembers",
      joinColumns = @JoinColumn(name = "userroleid", referencedColumnName = "userroleid"),
      inverseJoinColumns = @JoinColumn(name = "userid", referencedColumnName = "userid")
  )
  private Set<UserCredentials> members = new HashSet<>();

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public UserAuthorityGroup() {

  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void addUserCredentials(UserCredentials userCredentials) {
    members.add(userCredentials);
    userCredentials.getUserAuthorityGroups().add(this);
  }

  public void removeUserCredentials(UserCredentials userCredentials) {
    members.remove(userCredentials);
    userCredentials.getUserAuthorityGroups().remove(this);
  }

  public boolean isSuper() {
    return authorities != null && authorities.contains(AUTHORITY_ALL);
  }

  public boolean hasCriticalAuthorities() {
    return authorities != null && CollectionUtils
        .containsAny(authorities, Sets.newHashSet(CRITICAL_AUTHS));
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty
  public Set<String> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<String> authorities) {
    this.authorities = authorities;
  }

  public Set<UserCredentials> getMembers() {
    return members;
  }

  public void setMembers(Set<UserCredentials> members) {
    this.members = members;
  }

  @JsonProperty
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  public List<User> getUsers() {
    List<User> users = new ArrayList<>();

    for (UserCredentials userCredentials : members) {
      if (userCredentials.getUserInfo() != null) {
        users.add(userCredentials.getUserInfo());
      }
    }

    return users;
  }
}