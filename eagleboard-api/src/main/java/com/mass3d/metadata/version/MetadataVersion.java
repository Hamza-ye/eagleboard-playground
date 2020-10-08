package com.mass3d.metadata.version;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DxfNamespaces;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Base class for MetadataVersion
 *
 */
//@Entity
//@Table(name = "metadataversion")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@AttributeOverride(name="id", column=@Column(name="versionid"))
//@AssociationOverride(
//    name="userGroupAccesses",
//    joinTable=@JoinTable(
//        name="metadataversionusergroupaccesses",
//        joinColumns=@JoinColumn(name="versionid"),
//        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
//    )
//)
//@AssociationOverride(
//    name="userAccesses",
//    joinTable=@JoinTable(
//        name="metadataversionuseraccesses",
//        joinColumns=@JoinColumn(name="versionid"),
//        inverseJoinColumns=@JoinColumn(name="useraccessid")
//    )
//)
@JacksonXmlRootElement(localName = "metadataVersion", namespace = DxfNamespaces.DXF_2_0)
public class MetadataVersion
    extends BaseIdentifiableObject
    implements Serializable {

  @Temporal( TemporalType.TIMESTAMP )
  private Date importDate;

  private VersionType type;

  private String hashCode;

  public MetadataVersion() {
  }

  public MetadataVersion(String name, VersionType type) {
    super();
    this.type = type;
    this.name = name;
  }

  // -------------------------------------------------------------------------
  // Getter and setters
  // -------------------------------------------------------------------------

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public VersionType getType() {
    return type;
  }

  public void setType(VersionType type) {
    this.type = type;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public Date getImportDate() {
    return importDate;
  }

  public void setImportDate(Date importDate) {
    this.importDate = importDate;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getHashCode() {
    return hashCode;
  }

  public void setHashCode(String hashCode) {
    this.hashCode = hashCode;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getName() {
    return super.getName();
  }

  @Override
  public String toString() {
    return "MetadataVersion{" +
        "importDate=" + importDate +
        ", type=" + type +
        ", name='" + name + '\'' +
        ", hashCode='" + hashCode + '\'' +
        '}';
  }
}
