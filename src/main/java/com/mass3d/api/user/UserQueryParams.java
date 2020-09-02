package com.mass3d.api.user;

import com.google.common.base.MoreObjects;
import java.util.Date;

public class UserQueryParams {

  private String query;

  private String phoneNumber;

  private User user;

  private boolean canManage;

  private boolean authSubset;

  private boolean disjointRoles;

  private Date lastLogin;

  private Date inactiveSince;

  private Date passwordLastUpdated;

  private Integer inactiveMonths;

  private boolean selfRegistered;

  private boolean isNot2FA;

  private Integer first;

  private Integer max;

  private boolean userOrgUnits;

  private boolean includeOrgUnitChildren;

  private Boolean disabled;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public UserQueryParams() {
  }

  public UserQueryParams(User user) {
    this.user = user;
  }

  // -------------------------------------------------------------------------
  // toString
  // -------------------------------------------------------------------------

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("query", query)
        .add("phone number", phoneNumber)
        .add("user", user != null ? user.getUsername() : null)
        .add("can manage", canManage)
        .add("auth subset", authSubset)
        .add("disjoint roles", disjointRoles)
        .add("last login", lastLogin)
        .add("inactive since", inactiveSince)
        .add("passwordLastUpdated", passwordLastUpdated)
        .add("inactive months", inactiveMonths)
        .add("self registered", selfRegistered)
        .add("isNot2FA", isNot2FA)
        .add("first", first)
        .add("max", max)
        .add("includeOrgUnitChildren", includeOrgUnitChildren)
        .add("disabled", disabled).toString();
  }

  // -------------------------------------------------------------------------
  // Builder
  // -------------------------------------------------------------------------

//    public UserQueryParams addOrganisationUnit( OrganisationUnit unit )
//    {
//        this.organisationUnits.add( unit );
//        return this;
//    }

  public boolean hasUser() {
    return user != null;
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  public String getQuery() {
    return query;
  }

  public UserQueryParams setQuery(String query) {
    this.query = query;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public UserQueryParams setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public User getUser() {
    return user;
  }

  public UserQueryParams setUser(User user) {
    this.user = user;
    return this;
  }

  public boolean isCanManage() {
    return canManage;
  }

  public UserQueryParams setCanManage(boolean canManage) {
    this.canManage = canManage;
    return this;
  }

  public boolean isAuthSubset() {
    return authSubset;
  }

  public UserQueryParams setAuthSubset(boolean authSubset) {
    this.authSubset = authSubset;
    return this;
  }

  public boolean isDisjointRoles() {
    return disjointRoles;
  }

  public UserQueryParams setDisjointRoles(boolean disjointRoles) {
    this.disjointRoles = disjointRoles;
    return this;
  }

  public Date getLastLogin() {
    return lastLogin;
  }

  public UserQueryParams setLastLogin(Date lastLogin) {
    this.lastLogin = lastLogin;
    return this;
  }

  public Date getInactiveSince() {
    return inactiveSince;
  }

  public UserQueryParams setInactiveSince(Date inactiveSince) {
    this.inactiveSince = inactiveSince;
    return this;
  }

  public Integer getInactiveMonths() {
    return inactiveMonths;
  }

  public UserQueryParams setInactiveMonths(Integer inactiveMonths) {
    this.inactiveMonths = inactiveMonths;
    return this;
  }

  public boolean isSelfRegistered() {
    return selfRegistered;
  }

  public UserQueryParams setSelfRegistered(boolean selfRegistered) {
    this.selfRegistered = selfRegistered;
    return this;
  }

  public boolean isNot2FA() {
    return isNot2FA;
  }

  public UserQueryParams setNot2FA(boolean isNot2FA) {
    this.isNot2FA = isNot2FA;
    return this;
  }

  public Integer getFirst() {
    return first;
  }

  public UserQueryParams setFirst(Integer first) {
    this.first = first;
    return this;
  }

  public Integer getMax() {
    return max;
  }

  public UserQueryParams setMax(Integer max) {
    this.max = max;
    return this;
  }

  public boolean isUserOrgUnits() {
    return userOrgUnits;
  }

  public UserQueryParams setUserOrgUnits(boolean userOrgUnits) {
    this.userOrgUnits = userOrgUnits;
    return this;
  }

  public boolean isIncludeOrgUnitChildren() {
    return includeOrgUnitChildren;
  }

  public UserQueryParams setIncludeOrgUnitChildren(boolean includeOrgUnitChildren) {
    this.includeOrgUnitChildren = includeOrgUnitChildren;
    return this;
  }

  public Date getPasswordLastUpdated() {
    return passwordLastUpdated;
  }

  public UserQueryParams setPasswordLastUpdated(Date passwordLastUpdated) {
    this.passwordLastUpdated = passwordLastUpdated;
    return this;
  }

  public Boolean getDisabled() {
    return disabled;
  }

  public UserQueryParams setDisabled(Boolean disabled) {
    this.disabled = disabled;
    return this;
  }
}
