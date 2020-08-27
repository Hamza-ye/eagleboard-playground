package com.mass3d.api.activity;

import com.mass3d.api.common.BaseNameableObject;
import com.mass3d.api.common.MetadataObject;
import com.mass3d.api.project.Project;
import com.mass3d.api.todotask.TodoTask;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name="id", column=@Column(name="activityid"))
public class Activity
    extends BaseNameableObject
    implements MetadataObject {

  @ManyToOne
  @JoinTable(name = "projectactivities",
      joinColumns = @JoinColumn(name = "activityid", insertable = false,
          updatable = false, referencedColumnName = "activityid"),
      inverseJoinColumns = @JoinColumn(name = "projectid", insertable = false,
          updatable = false, referencedColumnName = "projectid")
  )
  private Project project;

  @OneToMany
  @JoinTable(name = "activitytodotasks",
      joinColumns = @JoinColumn(name = "activityid", referencedColumnName = "activityid"),
      inverseJoinColumns = @JoinColumn(name = "todotaskid", referencedColumnName = "todotaskid")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<TodoTask> todoTasks = new HashSet<>();


  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public Activity() {
  }

  public Activity(String name) {
    this();
    this.name = name;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void addTask(TodoTask task) {
    this.todoTasks.add(task);
    task.setActivity(this);
  }

  public void removeTask(TodoTask task) {
    this.todoTasks.remove(task);
    task.setActivity(null);
  }
  // ----------------------------------------------------------------------------
  // Getters and Setters
  // ----------------------------------------------------------------------------

  public Set<TodoTask> getTodoTasks() {
    return todoTasks;
  }

  public void setTodoTasks(Set<TodoTask> todoTasks) {
    this.todoTasks = todoTasks;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }
}
