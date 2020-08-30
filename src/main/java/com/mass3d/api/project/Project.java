package com.mass3d.api.project;

import com.mass3d.api.activity.Activity;
import com.mass3d.api.common.BaseNameableObject;
import com.mass3d.api.common.MetadataObject;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "projectid"))
@AssociationOverride(
    name="userGroupAccesses",
    joinTable=@JoinTable(
        name="projectusergroupaccesses",
        joinColumns=@JoinColumn(name="projectid"),
        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
    )
)
@AssociationOverride(
    name="userAccesses",
    joinTable=@JoinTable(
        name="projectuseraccesses",
        joinColumns=@JoinColumn(name="projectid"),
        inverseJoinColumns=@JoinColumn(name="useraccessid")
    )
)
public class Project
    extends BaseNameableObject
    implements MetadataObject {

  @OneToMany
  @JoinTable(
      name = "projectactivities",
      joinColumns = @JoinColumn(name = "projectid", referencedColumnName = "projectid"),
      inverseJoinColumns = @JoinColumn(name = "activityid", referencedColumnName = "activityid")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<Activity> activities = new HashSet<>();

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------
  public Project() {
  }

  public Project(String name) {
    this();
    this.name = name;
  }

  public void addActivity(Activity activity) {
    activity.setProject(this);
    this.activities.add(activity);
  }

  public void removeActivity(Activity activity) {
    this.activities.remove(activity);
    activity.setProject(null);
  }
  // ----------------------------------------------------------------------------
  // Getters and Setters
  // ----------------------------------------------------------------------------

  public Set<Activity> getActivities() {
    return activities;
  }

  public void setActivities(Set<Activity> activities) {
    this.activities = activities;
  }
}
