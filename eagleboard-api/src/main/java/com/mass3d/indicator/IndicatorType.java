package com.mass3d.indicator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.MetadataObject;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "indicatortype")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "indicatortypeid"))
@AssociationOverride(
    name="userGroupAccesses",
    joinTable=@JoinTable(
        name="indicatortypeusergroupaccesses",
        joinColumns=@JoinColumn(name="indicatortypeid"),
        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
    )
)
@AssociationOverride(
    name="userAccesses",
    joinTable=@JoinTable(
        name="indicatortypeuseraccesses",
        joinColumns=@JoinColumn(name="indicatortypeid"),
        inverseJoinColumns=@JoinColumn(name="useraccessid")
    )
)
@JacksonXmlRootElement(localName = "indicatorType", namespace = DxfNamespaces.DXF_2_0)
public class IndicatorType
    extends BaseIdentifiableObject implements MetadataObject {

  private int factor;

  private boolean number;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public IndicatorType() {

  }

  public IndicatorType(String name, int factor, Boolean number) {
    this.name = name;
    this.factor = factor;
    this.number = number;
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public int getFactor() {
    return factor;
  }

  public void setFactor(int factor) {
    this.factor = factor;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isNumber() {
    return number;
  }

  public void setNumber(boolean number) {
    this.number = number;
  }
}
