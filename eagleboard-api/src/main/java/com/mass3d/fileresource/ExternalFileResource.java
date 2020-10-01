package com.mass3d.fileresource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.MetadataObject;
import com.mass3d.user.User;

//@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExternalFileResource
    extends BaseIdentifiableObject implements MetadataObject {

  /**
   * FileResource containing the file we are exposing
   */
  @ManyToOne
  @JoinColumn(name = "fileresourceid")
  private FileResource fileResource;

  /**
   * The accessToken required to identify ExternalFileResources trough the api
   */
  private String accessToken;

  /**
   * Date when the resource expires. Null means it never expires
   */
  private Date expires;

  // -------------------------------------------------------------------------
  // Overridden methods
  // -------------------------------------------------------------------------

  @Override
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "externalfileresourceid")
  @JsonIgnore
  public Long getId() {
    return super.getId();
  }

  @Override
  @Column(name = "uid", unique = true, length = 11, nullable = false)
  public String getUid() {
    return super.getUid();
  }

  @Override
  @Column(name = "code", unique = true, length = 50)
  public String getCode() {
    return super.getCode();
  }

  @Override
  @ManyToOne
  @JoinColumn(name = "lastupdatedby")
  public User getLastUpdatedBy() {
    return super.getLastUpdatedBy();
  }

  @Override
  @ManyToOne
  @JoinColumn(name = "userid")
  public User getUser() {
    return super.getUser();
  }

  public Date getExpires() {
    return expires;
  }

  public void setExpires(Date expires) {
    this.expires = expires;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public FileResource getFileResource() {
    return fileResource;
  }

  public void setFileResource(FileResource fileResource) {
    this.fileResource = fileResource;
  }
}
