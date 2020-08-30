package com.mass3d.api.fieldset;

import com.google.common.collect.ImmutableSet;
import com.mass3d.api.common.BaseNameableObject;
import com.mass3d.api.common.MetadataObject;
import com.mass3d.api.datafield.DataField;
import com.mass3d.api.todotask.TodoTask;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "fieldsetid"))
@AssociationOverride(
    name="userGroupAccesses",
    joinTable=@JoinTable(
        name="fieldsetusergroupaccesses",
        joinColumns=@JoinColumn(name="fieldsetid"),
        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
    )
)
@AssociationOverride(
    name="userAccesses",
    joinTable=@JoinTable(
        name="fieldsetuseraccesses",
        joinColumns=@JoinColumn(name="fieldsetid"),
        inverseJoinColumns=@JoinColumn(name="useraccessid")
    )
)
public class FieldSet
    extends BaseNameableObject
    implements MetadataObject {

  public static final int NO_EXPIRY = 0;

  @OneToMany(
      mappedBy = "fieldSet",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<FieldSetField> fieldSetFields = new HashSet<>();

  @ManyToMany
  @JoinTable(name = "fieldsetsource",
      joinColumns = @JoinColumn(name = "fieldsetid", referencedColumnName = "fieldsetid"),
      inverseJoinColumns = @JoinColumn(name = "sourceid", referencedColumnName = "todotaskid")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<TodoTask> sources = new HashSet<>();

  private String formName;

  /**
   * Property indicating if the fieldSet could be collected using mobile data entry.
   */
  private boolean mobile;

  /**
   * Indicating version number.
   */
  private int version;

  /**
   * How many days after period is over will this dataSet auto-lock
   */
  private int expiryDays;

  /**
   * Days after period end to qualify for timely data submission
   */
  private int timelyDays;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------
  public FieldSet() {
  }

  public FieldSet(String name) {
    this.name = name;
  }

  public FieldSet(String name, String shortName) {
    this(name);
    this.shortName = shortName;
  }

  public FieldSet(String name, String shortName, String code) {
    this(name, shortName);
    this.code = code;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public boolean addFieldSetField(FieldSetField fieldSetField) {
    fieldSetField.getDataField().getFieldSetFields().add(fieldSetField);
    return fieldSetFields.add(fieldSetField);
  }

  /**
   * Adds a data set field using this field set, the given data field
   *
   * @param dataField the data element.
   */
  public boolean addFieldSetField(DataField dataField) {
    FieldSetField fieldSetField = new FieldSetField(this, dataField);
    dataField.getFieldSetFields().add(fieldSetField);
    return fieldSetFields.add(fieldSetField);
  }


  public Set<DataField> getDataFields() {
    return ImmutableSet.copyOf(fieldSetFields.stream().map(e -> e.getDataField()).collect(Collectors
        .toSet()));
  }

  public boolean removeFieldSetField(FieldSetField fieldSetField) {
    fieldSetFields.remove(fieldSetField);
    return fieldSetField.getDataField().getFieldSetFields().remove(fieldSetField);
  }

  public void removeFieldSetField(DataField dataField) {
    Iterator<FieldSetField> elements = fieldSetFields.iterator();

    while (elements.hasNext()) {
      FieldSetField element = elements.next();

      FieldSetField other = new FieldSetField(this, dataField);

      if (element.objectEquals(other)) {
        elements.remove();
        element.getDataField().getFieldSetFields().remove(element);
      }
    }
  }

  public void removeAllDataSetElements() {
    for (FieldSetField element : fieldSetFields) {
      element.getDataField().getFieldSetFields().remove(element);
    }

    fieldSetFields.clear();
  }

  public boolean removeTodoTask(TodoTask todoTask) {
    sources.remove(todoTask);
    return todoTask.getFieldSets().remove(this);
  }

  public String getFormName() {
    return formName;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }

  public boolean isMobile() {
    return mobile;
  }

  public void setMobile(boolean mobile) {
    this.mobile = mobile;
  }


  public Set<FieldSetField> getFieldSetFields() {
    return fieldSetFields;
  }

  public void setFieldSetFields(Set<FieldSetField> fieldSetFields) {
    this.fieldSetFields = fieldSetFields;
  }

  public Set<TodoTask> getSources() {
    return sources;
  }

  public void setSources(Set<TodoTask> sources) {
    this.sources = sources;
  }

  public int getExpiryDays() {
    return expiryDays;
  }

  public void setExpiryDays(int expiryDays) {
    this.expiryDays = expiryDays;
  }

  public int getTimelyDays() {
    return timelyDays;
  }

  public void setTimelyDays(int timelyDays) {
    this.timelyDays = timelyDays;
  }
}
