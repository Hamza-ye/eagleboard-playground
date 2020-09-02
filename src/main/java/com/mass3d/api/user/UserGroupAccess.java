package com.mass3d.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import com.mass3d.api.common.DxfNamespaces;
import com.mass3d.api.common.EmbeddedObject;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "usergroupaccess")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "userGroupAccess", namespace = DxfNamespaces.DXF_2_0)
public class UserGroupAccess
    implements Serializable, EmbeddedObject {

  @Id
  @GeneratedValue(
      strategy = GenerationType.AUTO
  )
  @Column(name = "usergroupaccessid")
  private int id;

  private String access;

  @ManyToOne
  @JoinColumn(name = "usergroupid")
  private UserGroup userGroup;

  private transient String uid;

  public UserGroupAccess() {
  }

  public UserGroupAccess(UserGroup userGroup, String access) {
    this.userGroup = userGroup;
    this.access = access;
  }

  public int getId() {
    return id;
  }

  @JsonIgnore
  public void setId(int id) {
    this.id = id;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getAccess() {
    return access;
  }

  public void setAccess(String access) {
    this.access = access;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getUserGroupUid() {
    return userGroup != null ? userGroup.getUid() : null;
  }

  @JsonProperty("id")
  @JacksonXmlProperty(localName = "id", namespace = DxfNamespaces.DXF_2_0)
  public String getUid() {
    return uid != null ? uid : (userGroup != null ? userGroup.getUid() : null);
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String displayName() {
    return userGroup != null ? userGroup.getDisplayName() : null;
  }

  @JsonIgnore
  public UserGroup getUserGroup() {
    if (userGroup == null) {
      UserGroup userGroup = new UserGroup();
      userGroup.setUid(uid);
      return userGroup;
    }

    return userGroup;
  }

  @JsonProperty
  public void setUserGroup(UserGroup userGroup) {
    this.userGroup = userGroup;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UserGroupAccess that = (UserGroupAccess) o;

    return Objects.equals(access, that.access) && Objects.equals(getUid(), that.getUid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(access, getUid());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("uid", getUid())
        .add("access", access)
        .toString();
  }
}
