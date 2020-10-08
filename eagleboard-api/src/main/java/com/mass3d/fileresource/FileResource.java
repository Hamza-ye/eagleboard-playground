package com.mass3d.fileresource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.Optional;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DxfNamespaces;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.util.MimeTypeUtils;

//@Entity
//@Table(name = "fileresource")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@AttributeOverride(name="id", column=@Column(name="fileresourceid"))
//@AssociationOverride(
//    name="userGroupAccesses",
//    joinTable=@JoinTable(
//        name="fileresourceusergroupaccesses",
//        joinColumns=@JoinColumn(name="fileresourceid"),
//        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
//    )
//)
//@AssociationOverride(
//    name="userAccesses",
//    joinTable=@JoinTable(
//        name="fileresourceuseraccesses",
//        joinColumns=@JoinColumn(name="fileresourceid"),
//        inverseJoinColumns=@JoinColumn(name="useraccessid")
//    )
//)
public class FileResource
    extends BaseIdentifiableObject {

  public static final String DEFAULT_FILENAME = "untitled";

  public static final String DEFAULT_CONTENT_TYPE = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;

  /**
   * MIME type.
   */
  @Column(name = "contenttype", nullable = false, unique = false, length = 255)
  private String contentType;

  /**
   * Byte size of content, non negative.
   */
  @Column(name = "contentlength", nullable = false, unique = false)
  private long contentLength;

  /**
   * MD5 digest of content.
   */
  @Column(name = "contentmd5", nullable = false, length = 32)
  private String contentMd5;

  /**
   * Key used for content storage at external location.
   */
  @Column(name = "storagekey", nullable = false, unique = true, length = 1024)
  private String storageKey;

  /**
   * Flag indicating whether the resource is assigned (e.g. to a DataValue) or not. Unassigned
   * FileResources are generally safe to delete when reaching a certain age (unassigned objects
   * might be in staging).
   */
  @Column(name = "isassigned", nullable = false)
  private boolean assigned = false;

  /**
   * The domain which this FileResource belongs to.
   */
  private FileResourceDomain domain;

  /**
   * Current storage status of content.
   */
  private transient FileResourceStorageStatus storageStatus = FileResourceStorageStatus.NONE;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public FileResource() {
  }

  public FileResource(String name, String contentType, long contentLength, String contentMd5,
      FileResourceDomain domain) {
    this.name = name;
    this.contentType = contentType;
    this.contentLength = contentLength;
    this.contentMd5 = contentMd5;
    this.domain = domain;
    this.storageKey = FileResourceKeyUtil.makeKey(domain, Optional.empty());
  }

  public FileResource(String key, String name, String contentType, long contentLength,
      String contentMd5,
      FileResourceDomain domain) {
    this.name = name;
    this.contentType = contentType;
    this.contentLength = contentLength;
    this.contentMd5 = contentMd5;
    this.domain = domain;
    this.storageKey = FileResourceKeyUtil.makeKey(domain, Optional.of(key));
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public long getContentLength() {
    return contentLength;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getContentMd5() {
    return contentMd5;
  }

  public void setContentMd5(String contentMd5) {
    this.contentMd5 = contentMd5;
  }

  public String getStorageKey() {
    return storageKey;
  }

  public void setStorageKey(String storageKey) {
    this.storageKey = storageKey;
  }

  public boolean isAssigned() {
    return assigned;
  }

  public void setAssigned(boolean assigned) {
    this.assigned = assigned;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public FileResourceStorageStatus getStorageStatus() {
    return storageStatus;
  }

  public void setStorageStatus(FileResourceStorageStatus storageStatus) {
    this.storageStatus = storageStatus;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public FileResourceDomain getDomain() {
    return domain;
  }

  public void setDomain(FileResourceDomain domain) {
    this.domain = domain;
  }

  public String getFormat() {
    return this.contentType.split("[/;]")[1];
  }
}
