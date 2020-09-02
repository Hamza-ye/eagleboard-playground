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
@Table(name = "useraccess")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JacksonXmlRootElement(localName = "userAccess", namespace = DxfNamespaces.DXF_2_0)
public class UserAccess
    implements Serializable, EmbeddedObject {

  @Id
  @GeneratedValue(
      strategy = GenerationType.AUTO
  )
  @Column(name = "useraccessid")
  private int id;

  private String access;

  @ManyToOne
  @JoinColumn(name = "userid")
  private User user;

  private transient String uid;

  public UserAccess() {
  }

  public UserAccess(User user, String access) {
    this.user = user;
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
  public String getUserUid() {
    return user != null ? user.getUid() : null;
  }

  @JsonProperty("id")
  @JacksonXmlProperty(localName = "id", namespace = DxfNamespaces.DXF_2_0)
  public String getUid() {
    return uid != null ? uid : (user != null ? user.getUid() : null);
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String displayName() {
    return user != null ? user.getDisplayName() : null;
  }

  @JsonIgnore
  public User getUser() {
    if (user == null) {
      User user = new User();
      user.setUid(uid);
      return user;
    }

    return user;
  }

  @JsonProperty
  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UserAccess that = (UserAccess) o;

    return Objects.equals(access, that.access) && Objects.equals(getUid(), that.getUid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(access, getUid());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("access", access)
        .add("uid", getUid())
        .toString();
  }
}
