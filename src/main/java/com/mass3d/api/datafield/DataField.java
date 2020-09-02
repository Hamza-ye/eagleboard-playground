package com.mass3d.api.datafield;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.ImmutableSet;
import com.mass3d.api.common.BaseNameableObject;
import com.mass3d.api.common.DxfNamespaces;
import com.mass3d.api.common.MetadataObject;
import com.mass3d.api.common.ValueType;
import com.mass3d.api.fieldset.FieldSet;
import com.mass3d.api.fieldset.FieldSetField;
import com.mass3d.api.schema.PropertyType;
import com.mass3d.api.schema.annotation.Property;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Table(name = "datafield")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "datafieldid"))
@AssociationOverride(
    name="userGroupAccesses",
    joinTable=@JoinTable(
        name="datafieldusergroupaccesses",
        joinColumns=@JoinColumn(name="datafieldid"),
        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
    )
)
@AssociationOverride(
    name="userAccesses",
    joinTable=@JoinTable(
        name="datafielduseraccesses",
        joinColumns=@JoinColumn(name="datafieldid"),
        inverseJoinColumns=@JoinColumn(name="useraccessid")
    )
)
@JacksonXmlRootElement(localName = "dataField", namespace = DxfNamespaces.DXF_2_0)
public class DataField
    extends BaseNameableObject
    implements MetadataObject {

  /**
   * The i18n variant of the display name. Should not be persisted.
   */
  protected transient String displayFormName;

  @OneToMany(
      mappedBy = "dataField",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<FieldSetField> fieldSetFields = new HashSet<>();
  /**
   * Data field value type (int, boolean, etc)
   */
  private ValueType valueType;
  /**
   * The name to appear in forms.
   */
  private String formName;
  /**
   * URL for lookup of additional information on the web.
   */
  private String url;
  /**
   * Indicates whether to store zero data values.
   */
  private boolean zeroIsSignificant;

  /**
   * Field mask represent how the value should be formatted during input. This string will be
   * validated as a TextPatternSegment of type TEXT.
   */
  private String fieldMask;
  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public DataField() {
  }

  public DataField(String name) {
    this();
    this.name = name;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public boolean removeDataSetElement(FieldSetField fieldSetField) {
    fieldSetFields.remove(fieldSetField);
    return fieldSetField.getFieldSet().getFieldSetFields().remove(fieldSetField);
  }

  /**
   * Indicates whether the value type of this data field is numeric.
   */
  public boolean isNumericType() {
    return getValueType().isNumeric();
  }

  /**
   * Indicates whether the value type of this data field is a file (externally stored resource)
   */
  public boolean isFileType() {
    return getValueType().isFile();
  }

  /**
   * Returns the field set of this data field. If this data field has multiple field sets, the field
   * set with the highest collection frequency is returned.
   */
  public FieldSet getFieldSet() {
    List<FieldSet> list = new ArrayList<>(getFieldSets());
//    Collections.sort( list, DataSetFrequencyComparator.INSTANCE );
    return !list.isEmpty() ? list.get(0) : null;
  }

  /**
   * Note that this method returns an immutable set and can not be used to modify the model. Returns
   * an immutable set of field sets associated with this data field.
   */
  public Set<FieldSet> getFieldSets() {
    return ImmutableSet.copyOf(fieldSetFields.stream().map(FieldSetField::getFieldSet).filter(
        dataSet -> dataSet != null).collect(Collectors.toSet()));
  }

  // -------------------------------------------------------------------------
  // DimensionalItemObject
  // -------------------------------------------------------------------------

  /**
   * Returns the form name, or the name if it does not exist.
   */
  public String getFormNameFallback() {
    return formName != null && !formName.isEmpty() ? getFormName() : getDisplayName();
  }

  @JsonProperty
  @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
  public String getDisplayFormName() {
    return displayFormName != null ? displayFormName : getFormNameFallback();
  }

  public void setDisplayFormName(String displayFormName) {
    this.displayFormName = displayFormName;
  }

  /**
   * Returns the maximum number of expiry days from the field sets of this date field. Returns
   * {@link FieldSet#NO_EXPIRY} if any field set has no expiry.
   */
  public int getExpiryDays() {
    int expiryDays = Integer.MIN_VALUE;

    for (FieldSet fieldSet : getFieldSets()) {
      if (fieldSet.getExpiryDays() == FieldSet.NO_EXPIRY) {
        return FieldSet.NO_EXPIRY;
      }

      if (fieldSet.getExpiryDays() > expiryDays) {
        expiryDays = fieldSet.getExpiryDays();
      }
    }

    return expiryDays == Integer.MIN_VALUE ? FieldSet.NO_EXPIRY : expiryDays;
  }


  /**
   * Indicates whether this data field has a description.
   *
   * @return true if this data field has a description.
   */
  public boolean hasDescription() {
    return description != null && !description.trim().isEmpty();
  }

  /**
   * Indicates whether this data field has a URL.
   *
   * @return true if this data field has a URL.
   */
  public boolean hasUrl() {
    return url != null && !url.trim().isEmpty();
  }

  public Set<FieldSetField> getFieldSetFields() {
    return fieldSetFields;
  }

  public void setFieldSetFields(Set<FieldSetField> fieldSetFields) {
    this.fieldSetFields = fieldSetFields;
  }

  //  @Override
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public ValueType getValueType() {
    return valueType;
  }

  public void setValueType(ValueType valueType) {
    this.valueType = valueType;
  }

  public String getFormName() {
    return formName;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  @Property(PropertyType.URL)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isZeroIsSignificant() {
    return zeroIsSignificant;
  }

  public void setZeroIsSignificant(boolean zeroIsSignificant) {
    this.zeroIsSignificant = zeroIsSignificant;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getFieldMask() {
    return fieldMask;
  }

  public void setFieldMask(String fieldMask) {
    this.fieldMask = fieldMask;
  }
}
