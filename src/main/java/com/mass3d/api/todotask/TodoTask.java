package com.mass3d.api.todotask;

import com.google.common.collect.Sets;
import com.mass3d.api.activity.Activity;
import com.mass3d.api.common.BaseNameableObject;
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
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

  public Set<FieldSet> getFieldSets() {
    return fieldSets;
  }

  public void setFieldSets(Set<FieldSet> fieldSets) {
    this.fieldSets = fieldSets;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public boolean isMobile() {
    return mobile;
  }

  public void setMobile(boolean mobile) {
    this.mobile = mobile;
  }
}
