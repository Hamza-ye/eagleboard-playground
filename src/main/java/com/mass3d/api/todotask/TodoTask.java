package com.mass3d.api.todotask;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Sets;
import com.mass3d.api.activity.Activity;
import com.mass3d.api.common.BaseIdentifiableObject;
import com.mass3d.api.common.BaseNameableObject;
import com.mass3d.api.common.DxfNamespaces;
import com.mass3d.api.common.MetadataObject;
import com.mass3d.api.fieldset.FieldSet;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
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
@Table(name = "todotask")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "todotaskid"))
@AssociationOverride(
    name="userGroupAccesses",
    joinTable=@JoinTable(
        name="todotaskusergroupaccesses",
        joinColumns=@JoinColumn(name="todotaskid"),
        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
    )
)
@AssociationOverride(
    name="userAccesses",
    joinTable=@JoinTable(
        name="todotaskuseraccesses",
        joinColumns=@JoinColumn(name="todotaskid"),
        inverseJoinColumns=@JoinColumn(name="useraccessid")
    )
)
@JacksonXmlRootElement(localName = "todoTask", namespace = DxfNamespaces.DXF_2_0)
public class TodoTask
    extends BaseNameableObject
    implements MetadataObject {

  @ManyToMany (mappedBy="sources")
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  Set<FieldSet> fieldSets = new HashSet<>();
  /**
   * Property indicating if the todoTask could be collected using mobile data entry.
   */

  @ManyToOne
  @JoinTable(
      name = "activitytodotasks",
      joinColumns = @JoinColumn(name = "todotaskid", insertable = false,
          updatable = false, referencedColumnName = "todotaskid"),
      inverseJoinColumns = @JoinColumn(name = "activityid", insertable = false,
          updatable = false, referencedColumnName = "activityid")
  )
  private Activity activity;

  private boolean mobile;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public TodoTask() {
  }

  public TodoTask(String name) {
    this();
    this.name = name;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public boolean addFieldSet(FieldSet fieldSet) {
    fieldSets.add(fieldSet);
    return fieldSet.getSources().add(this);
  }

  public void updateFieldSets(Set<FieldSet> updates) {
    Set<FieldSet> toRemove = Sets.difference(fieldSets, updates);
    Set<FieldSet> toAdd = Sets.difference(updates, fieldSets);

    toRemove.forEach(u -> u.getSources().remove(this));
    toAdd.forEach(u -> u.getSources().add(this));

    fieldSets.clear();
    fieldSets.addAll(updates);
  }

  // ----------------------------------------------------------------------------
  // Getters and Setters
  // ----------------------------------------------------------------------------

  @JsonProperty
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "fieldSets", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "fieldSet", namespace = DxfNamespaces.DXF_2_0)
  public Set<FieldSet> getFieldSets() {
    return fieldSets;
  }

  public void setFieldSets(Set<FieldSet> fieldSets) {
    this.fieldSets = fieldSets;
  }

  @JsonProperty
  @JsonSerialize(as = BaseIdentifiableObject.class)
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isMobile() {
    return mobile;
  }

  public void setMobile(boolean mobile) {
    this.mobile = mobile;
  }

}
