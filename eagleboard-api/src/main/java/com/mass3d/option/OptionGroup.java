package com.mass3d.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.HashSet;
import java.util.Set;
import com.mass3d.common.BaseDimensionalItemObject;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DimensionItemType;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.MetadataObject;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "optiongroup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "optiongroupid"))
@AssociationOverride(
    name="userGroupAccesses",
    joinTable=@JoinTable(
        name="optiongroupusergroupaccesses",
        joinColumns=@JoinColumn(name="optiongroupid"),
        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
    )
)
@AssociationOverride(
    name="userAccesses",
    joinTable=@JoinTable(
        name="optiongroupuseraccesses",
        joinColumns=@JoinColumn(name="optiongroupid"),
        inverseJoinColumns=@JoinColumn(name="useraccessid")
    )
)
@JacksonXmlRootElement(localName = "optionGroup", namespace = DxfNamespaces.DXF_2_0)
public class OptionGroup
    extends BaseDimensionalItemObject implements MetadataObject {

  @ManyToMany
  @JoinTable(name = "optiongroupmembers",
      joinColumns = @JoinColumn(name = "optiongroupid", referencedColumnName = "optiongroupid"),
      inverseJoinColumns = @JoinColumn(name = "optionid", referencedColumnName = "optionid")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<Option> members = new HashSet<>();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "optionsetid")
  private OptionSet optionSet;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public OptionGroup() {
  }

  public OptionGroup(String name) {
    this.name = name;
  }

  // -------------------------------------------------------------------------
  // DimensionalItemObject
  // -------------------------------------------------------------------------

  @Override
  public DimensionItemType getDimensionItemType() {
    return DimensionItemType.OPTION_GROUP;
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty("options")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "options", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "option", namespace = DxfNamespaces.DXF_2_0)
  public Set<Option> getMembers() {
    return members;
  }

  public void setMembers(Set<Option> members) {
    this.members = members;
  }

  @JsonProperty("optionSet")
  @JsonSerialize(as = BaseIdentifiableObject.class)
  @JacksonXmlProperty(localName = "optionSet", namespace = DxfNamespaces.DXF_2_0)
  public OptionSet getOptionSet() {
    return optionSet;
  }

  public void setOptionSet(OptionSet optionSet) {
    this.optionSet = optionSet;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void addOption(Option option) {
    members.add(option);
  }

  public void removeOption(Option option) {
    members.remove(option);
  }

  public void updateOptions(Set<Option> updates) {
    for (Option option : new HashSet<>(members)) {
      if (!updates.contains(option)) {
        removeOption(option);
      }
    }

    for (Option option : updates) {
      addOption(option);
    }
  }

  public void removeAllOptions() {
    members.clear();
  }
}
