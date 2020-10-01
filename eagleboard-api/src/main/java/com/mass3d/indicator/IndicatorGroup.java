package com.mass3d.indicator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.HashSet;
import java.util.Set;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.MetadataObject;
import com.mass3d.schema.PropertyType;
import com.mass3d.schema.annotation.Property;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "indicatorgroup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "indicatorgroupid"))
@AssociationOverride(
    name="userGroupAccesses",
    joinTable=@JoinTable(
        name="indicatorgroupusergroupaccesses",
        joinColumns=@JoinColumn(name="indicatorgroupid"),
        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
    )
)
@AssociationOverride(
    name="userAccesses",
    joinTable=@JoinTable(
        name="indicatorgroupuseraccesses",
        joinColumns=@JoinColumn(name="indicatorgroupid"),
        inverseJoinColumns=@JoinColumn(name="useraccessid")
    )
)
@JacksonXmlRootElement(localName = "indicatorGroup", namespace = DxfNamespaces.DXF_2_0)
public class IndicatorGroup
    extends BaseIdentifiableObject implements MetadataObject {

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "indicatorgroupmembers",
      joinColumns = @JoinColumn(name = "indicatorgroupid", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "indicatorid", referencedColumnName = "id")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<Indicator> members = new HashSet<>();

  @ManyToOne
  @JoinTable(name = "indicatorgroupsetmembers",
      joinColumns = @JoinColumn(name = "indicatorgroupid", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "indicatorgroupsetid", referencedColumnName = "id")
  )
  private IndicatorGroupSet groupSet;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public IndicatorGroup() {
  }

  public IndicatorGroup(String name) {
    this.name = name;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void addIndicator(Indicator indicator) {
    members.add(indicator);
    indicator.getGroups().add(this);
  }

  public void removeIndicator(Indicator indicator) {
    members.remove(indicator);
    indicator.getGroups().remove(this);
  }

  public void updateIndicators(Set<Indicator> updates) {
    for (Indicator indicator : new HashSet<>(members)) {
      if (!updates.contains(indicator)) {
        removeIndicator(indicator);
      }
    }

    for (Indicator indicator : updates) {
      addIndicator(indicator);
    }
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void removeAllIndicators() {
    members.clear();
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty("indicators")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "indicators", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "indicator", namespace = DxfNamespaces.DXF_2_0)
  public Set<Indicator> getMembers() {
    return members;
  }

  public void setMembers(Set<Indicator> members) {
    this.members = members;
  }

  @JsonProperty("indicatorGroupSet")
  @JsonSerialize(as = BaseIdentifiableObject.class)
  @JacksonXmlProperty(localName = "indicatorGroupSet", namespace = DxfNamespaces.DXF_2_0)
  @Property(value = PropertyType.REFERENCE, required = Property.Value.FALSE)
  public IndicatorGroupSet getGroupSet() {
    return groupSet;
  }

  public void setGroupSet(IndicatorGroupSet groupSet) {
    this.groupSet = groupSet;
  }
}
