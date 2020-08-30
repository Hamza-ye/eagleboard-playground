package com.mass3d.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mass3d.api.common.BaseIdentifiableObject;
import com.mass3d.api.common.MetadataObject;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "usergroupid"))
public class UserGroup
    extends BaseIdentifiableObject implements MetadataObject {

  public static final String AUTH_USER_ADD = "F_USER_ADD";
  public static final String AUTH_USER_DELETE = "F_USER_DELETE";
  public static final String AUTH_USER_VIEW = "F_USER_VIEW";
  public static final String AUTH_USER_ADD_IN_GROUP = "F_USER_ADD_WITHIN_MANAGED_GROUP";
  public static final String AUTH_ADD_MEMBERS_TO_READ_ONLY_USER_GROUPS = "F_USER_GROUPS_READ_ONLY_ADD_MEMBERS";

  /**
   * Set of related users
   */
  @ManyToMany
  @JoinTable(name = "usergroupmembers",
      joinColumns = @JoinColumn(name = "usergroupid", referencedColumnName = "usergroupid"),
      inverseJoinColumns = @JoinColumn(name = "userid", referencedColumnName = "userid")
  )
  private Set<User> members = new HashSet<>();

  /**
   * User groups (if any) that members of this user group can manage the members within.
   */
  @ManyToMany
  @JoinTable(name = "usergroupmanaged",
      joinColumns = @JoinColumn(name = "managedbygroupid", referencedColumnName = "usergroupid"),
      inverseJoinColumns = @JoinColumn(name = "managedgroupid", referencedColumnName = "usergroupid")
  )
  private Set<UserGroup> managedGroups = new HashSet<>();

  /**
   * User groups (if any) whose members can manage the members of this user group.
   */
  @ManyToMany
  @JoinTable(name = "usergroupmanaged",
      joinColumns = @JoinColumn(name = "managedgroupid", referencedColumnName = "usergroupid"),
      inverseJoinColumns = @JoinColumn(name = "managedbygroupid", referencedColumnName = "usergroupid")
  )
  private Set<UserGroup> managedByGroups = new HashSet<>();

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public UserGroup() {

  }

  public UserGroup(String name) {
    this.name = name;
  }

  public UserGroup(String name, Set<User> members) {
    this(name);
    this.members = members;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void addUser(User user) {
    members.add(user);
    user.getGroups().add(this);
  }

  public void removeUser(User user) {
    members.remove(user);
    user.getGroups().remove(this);
  }

  public void updateUsers(Set<User> updates) {
    new HashSet<>(members).stream()
        .filter(user -> !updates.contains(user))
        .forEach(this::removeUser);

    updates.forEach(this::addUser);
  }

  public void addManagedGroup(UserGroup group) {
    managedGroups.add(group);
    group.getManagedByGroups().add(this);
  }

  public void removeManagedGroup(UserGroup group) {
    managedGroups.remove(group);
    group.getManagedByGroups().remove(this);
  }

  public void updateManagedGroups(Set<UserGroup> updates) {
    new HashSet<>(managedGroups).stream()
        .filter(group -> !updates.contains(group))
        .forEach(this::removeManagedGroup);

    updates.forEach(this::addManagedGroup);
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @Override
  @JsonIgnore
  public User getUser() {
    return user;
  }

  @Override
  @JsonIgnore
  public void setUser(User user) {
    this.user = user;
  }

  @JsonProperty("users")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  public Set<User> getMembers() {
    return members;
  }

  public void setMembers(Set<User> members) {
    this.members = members;
  }

  @JsonProperty("managedGroups")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  public Set<UserGroup> getManagedGroups() {
    return managedGroups;
  }

  public void setManagedGroups(Set<UserGroup> managedGroups) {
    this.managedGroups = managedGroups;
  }

  @JsonProperty("managedByGroups")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  public Set<UserGroup> getManagedByGroups() {
    return managedByGroups;
  }

  public void setManagedByGroups(Set<UserGroup> managedByGroups) {
    this.managedByGroups = managedByGroups;
  }
}
