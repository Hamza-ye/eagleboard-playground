package com.mass3d.api.fieldset;

import com.mass3d.api.common.EmbeddedObject;
import com.mass3d.api.datafield.DataField;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FieldSetField implements EmbeddedObject {

  /**
   * The database internal identifier for this Object.
   */
  @Id
  @GeneratedValue(
      strategy = GenerationType.AUTO
  )
  private int id;

  /**
   * Data field set, never null.
   */
  @ManyToOne
  @JoinColumn(name = "datasetid")
  private FieldSet fieldSet;

  /**
   * Data field, never null.
   */
  @ManyToOne
  @JoinColumn(name = "datafieldid")
  private DataField dataField;

  /**
   * Category combination, potentially null.
   */
//  private DataElementCategoryCombo categoryCombo;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------
  public FieldSetField() {

  }

  public FieldSetField(FieldSet fieldSet, DataField dataField) {
    this.fieldSet = fieldSet;
    this.dataField = dataField;
  }

//  public FieldSetField( FieldSet fieldSet, DataField datafield, DataElementCategoryCombo categoryCombo )
//  {
//    this.fieldSet = fieldSet;
//    this.datafield = datafield;
//    this.categoryCombo = categoryCombo;
//  }

// -------------------------------------------------------------------------
  // Hash code and equals
  // -------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fieldSet, dataField);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (!getClass().isAssignableFrom(object.getClass())) {
      return false;
    }

    FieldSetField other = (FieldSetField) object;

    return objectEquals(other);
  }

  public boolean objectEquals(FieldSetField other) {
    return fieldSet.equals(other.getFieldSet()) && dataField.equals(other.getDataField());
  }

  @Override
  public String toString() {
    return "{" +
        "\"class\":\"" + getClass() + "\", " +
        "\"fieldSet\":\"" + fieldSet + "\", " +
        "\"datafield\":\"" + dataField + "\" " +
//        "\"categoryCombo\":\"" + categoryCombo + "\" " +
        "}";
  }

  // -------------------------------------------------------------------------
  // Get and set methods
  // -------------------------------------------------------------------------

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public FieldSet getFieldSet() {
    return fieldSet;
  }

  public void setFieldSet(FieldSet fieldSet) {
    this.fieldSet = fieldSet;
  }

  public DataField getDataField() {
    return dataField;
  }

  public void setDataField(DataField dataField) {
    this.dataField = dataField;
  }

}
