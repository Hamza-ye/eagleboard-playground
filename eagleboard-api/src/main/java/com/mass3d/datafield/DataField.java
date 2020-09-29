package com.mass3d.datafield;


import static com.mass3d.fieldset.FieldSet.NO_EXPIRY;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.ImmutableSet;
import com.mass3d.common.BaseDimensionalItemObject;
import com.mass3d.common.BaseNameableObject;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.MetadataObject;
import com.mass3d.common.ValueType;
import com.mass3d.common.ValueTypedDimensionalItemObject;
import com.mass3d.fieldset.FieldSet;
import com.mass3d.fieldset.FieldSetField;
import com.mass3d.period.Period;
import com.mass3d.period.PeriodType;
import com.mass3d.period.YearlyPeriodType;
import com.mass3d.schema.PropertyType;
import com.mass3d.schema.annotation.Property;
import com.mass3d.schema.annotation.PropertyRange;
import java.util.ArrayList;
import java.util.Date;
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
import org.joda.time.DateTime;


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
    extends BaseDimensionalItemObject
    implements MetadataObject, ValueTypedDimensionalItemObject {

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

  //TODO can also be dimension

  /**
   * Returns the PeriodType of the DataElement, based on the PeriodType of the
   * DataSet which the DataElement is associated with. If this data element has
   * multiple data sets, the data set with the highest collection frequency is
   * returned.
   */
  public PeriodType getPeriodType()
  {
    FieldSet fieldSet = getFieldSet();

    return fieldSet != null ? fieldSet.getPeriodType() : null;
  }

  /**
   * Returns the PeriodTypes of the DataElement, based on the PeriodType of the
   * DataSets which the DataElement is associated with.
   */
  public Set<PeriodType> getPeriodTypes()
  {
    return getFieldSets().stream().map( FieldSet::getPeriodType ).collect( Collectors.toSet() );
  }

  /**
   * Indicates whether this data element requires approval of data. Returns true
   * if only one of the data sets associated with this data element requires
   * approval.
   */
  public boolean isApproveData()
  {
//    for ( FieldSet fieldSet : getFieldSets() )
//    {
//      if ( fieldSet != null && fieldSet.getWorkflow() != null )
//      {
//        return true;
//      }
//    }

    return false;
  }

  /**
   * Number of periods in the future to open for data capture, 0 means capture
   * not allowed for current period. Based on the data sets of which this data
   * element is a member.
   */
  public int getOpenFuturePeriods()
  {
    int maxOpenPeriods = 0;

    for ( FieldSet fieldSet : getFieldSets() )
    {
//      maxOpenPeriods = Math.max( maxOpenPeriods, fieldSet.getOpenFuturePeriods() );
    }

    return maxOpenPeriods;
  }

  /**
   * Returns the latest period which is open for data input. Returns null if
   * data element is not associated with any data sets.
   *
   * @return the latest period which is open for data input.
   */
  public Period getLatestOpenFuturePeriod()
  {
    int periods = getOpenFuturePeriods();

    PeriodType periodType = getPeriodType();

    if ( periodType != null )
    {
      Period period = periodType.createPeriod();

      // Rewind one as 0 open periods implies current period is locked

      period = periodType.getPreviousPeriod( period );

      return periodType.getNextPeriod( period, periods );
    }

    return null;
  }

  /**
   * Returns the frequency order for the PeriodType of this DataElement. If no
   * PeriodType exists, 0 is returned.
   */
  public int getFrequencyOrder()
  {
    PeriodType periodType = getPeriodType();

    return periodType != null ? periodType.getFrequencyOrder() : YearlyPeriodType.FREQUENCY_ORDER;
  }

  /**
   * Tests whether a PeriodType can be defined for the DataElement, which
   * requires that the DataElement is registered for DataSets with the same
   * PeriodType.
   */
  public boolean periodTypeIsValid()
  {
    PeriodType periodType = null;

    for ( FieldSet fieldSet : getFieldSets() )
    {
      if ( periodType != null && !periodType.equals( fieldSet.getPeriodType() ) )
      {
        return false;
      }

      periodType = fieldSet.getPeriodType();
    }

    return true;
  }

//  /**
//   * Tests whether more than one aggregation level exists for the DataElement.
//   */
//  public boolean hasAggregationLevels()
//  {
//    return aggregationLevels != null && aggregationLevels.size() > 0;
//  }

  /**
   * Returns the form name, or the name if it does not exist.
   */
  public String getFormNameFallback()
  {
    return formName != null && !formName.isEmpty() ? getFormName() : getDisplayName();
  }

  @JsonProperty
  @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
  public String getDisplayFormName()
  {
    displayFormName = null;
    return displayFormName != null ? displayFormName : getFormNameFallback();
  }

  public void setDisplayFormName( String displayFormName )
  {
    this.displayFormName = displayFormName;
  }

  /**
   * Returns the maximum number of expiry days from the data sets of this data
   * element. Returns {@link FieldSet#NO_EXPIRY} if any data set has no expiry.
   */
  public int getExpiryDays()
  {
    int expiryDays = Integer.MIN_VALUE;

    for ( FieldSet fieldSet : getFieldSets() )
    {
      if ( fieldSet.getExpiryDays() == NO_EXPIRY )
      {
        return NO_EXPIRY;
      }

      if ( fieldSet.getExpiryDays() > expiryDays )
      {
        expiryDays = fieldSet.getExpiryDays();
      }
    }

    return expiryDays == Integer.MIN_VALUE ? NO_EXPIRY : expiryDays;
  }

  /**
   * Indicates whether the given period is considered expired for the end date
   * of the given date based on the expiry days of the data sets associated
   * with this data element.
   *
   * @param period the period.
   * @param now    the date used as basis.
   * @return true or false.
   */
  public boolean isExpired( Period period, Date now )
  {
    int expiryDays = getExpiryDays();

    return expiryDays != NO_EXPIRY && new DateTime( period.getEndDate() ).plusDays( expiryDays ).isBefore( new DateTime( now ) );
  }

  /**
   * Indicates whether this data element has a description.
   *
   * @return true if this data element has a description.
   */
  public boolean hasDescription()
  {
    return description != null && !description.trim().isEmpty();
  }

  /**
   * Indicates whether this data element has a URL.
   *
   * @return true if this data element has a URL.
   */
  public boolean hasUrl()
  {
    return url != null && !url.trim().isEmpty();
  }

//  /**
//   * Indicates whether this data element has an option set.
//   *
//   * @return true if this data element has an option set.
//   */
//  @Override
//  public boolean hasOptionSet()
//  {
//    return optionSet != null;
//  }
  // -------------------------------------------------------------------------
  // Getters & Setters
  // -------------------------------------------------------------------------

//  @JsonProperty
//  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//  public boolean isOptionSetValue() {
//    return optionSet != null;
//  }

  public Set<FieldSetField> getFieldSetFields() {
    return fieldSetFields;
  }

  public void setFieldSetFields(Set<FieldSetField> fieldSetFields) {
    this.fieldSetFields = fieldSetFields;
  }

  @Override
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public ValueType getValueType() {
    //TODO return optionSet != null ? optionSet.getValueType() : valueType;
    return valueType;
  }

  public void setValueType(ValueType valueType) {
    this.valueType = valueType;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  @PropertyRange(min = 2)
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

//  @Override
//  @JsonProperty
//  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//  public OptionSet getOptionSet() {
//    return optionSet;
//  }
//
//  public void setOptionSet(OptionSet optionSet) {
//    this.optionSet = optionSet;
//  }
//
//  @JsonProperty
//  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//  public OptionSet getCommentOptionSet() {
//    return commentOptionSet;
//  }
//
//  public void setCommentOptionSet(OptionSet commentOptionSet) {
//    this.commentOptionSet = commentOptionSet;
//  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getFieldMask() {
    return fieldMask;
  }

  public void setFieldMask(String fieldMask) {
    this.fieldMask = fieldMask;
  }
}
