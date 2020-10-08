package com.mass3d.datavalue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Date;
import com.mass3d.period.Period;
import com.mass3d.period.PeriodType;

/**
 * The purpose of this class is to avoid the overhead of creating objects for associated objects, in
 * order to reduce heap space usage during export.
 *
 * @version $Id$
 */
@JacksonXmlRootElement
public class DeflatedDataValue {

  private Long dataElementId;

  private Long periodId;

  private Long sourceId;

//    private int categoryOptionComboId;
//
//    private int attributeOptionComboId;

  private String value;

  private String storedBy;

  private Date created;

  private Date lastUpdated;

  private String comment;

  private boolean followup;

  // -------------------------------------------------------------------------
  // Optional attributes
  // -------------------------------------------------------------------------

  private int min;

  private int max;

  private String dataElementName;

  private Period period = new Period();

  private String sourceName;

  private String categoryOptionComboName;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public DeflatedDataValue() {
  }

  public DeflatedDataValue(DataValue dataValue) {
    this.dataElementId = dataValue.getDataElement().getId();
    this.periodId = dataValue.getPeriod().getId();
    this.sourceId = dataValue.getSource().getId();
//        this.categoryOptionComboId = dataValue.getCategoryOptionCombo().getId();
//        this.attributeOptionComboId = dataValue.getAttributeOptionCombo().getId();
    this.value = dataValue.getValue();
    this.storedBy = dataValue.getStoredBy();
    this.created = dataValue.getCreated();
    this.lastUpdated = dataValue.getLastUpdated();
    this.comment = dataValue.getComment();
    this.followup = dataValue.isFollowup();
  }

  public DeflatedDataValue(Long dataElementId, Long periodId, Long sourceId,
      Long categoryOptionComboId, Long attributeOptionComboId, String value,
      String storedBy, Date created, Date lastUpdated,
      String comment, boolean followup) {
    this.dataElementId = dataElementId;
    this.periodId = periodId;
    this.sourceId = sourceId;
//        this.categoryOptionComboId = categoryOptionComboId;
//        this.attributeOptionComboId = attributeOptionComboId;
    this.value = value;
    this.storedBy = storedBy;
    this.created = created;
    this.lastUpdated = lastUpdated;
    this.comment = comment;
    this.followup = followup;
  }

  public DeflatedDataValue(Long dataElementId, Long periodId, Long sourceId,
      Long categoryOptionComboId, Long attributeOptionComboId, String value) {
    this.dataElementId = dataElementId;
    this.periodId = periodId;
    this.sourceId = sourceId;
//        this.categoryOptionComboId = categoryOptionComboId;
//        this.attributeOptionComboId = attributeOptionComboId;
    this.value = value;
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty
  public Long getDataElementId() {
    return dataElementId;
  }

  public void setDataElementId(Long dataElementId) {
    this.dataElementId = dataElementId;
  }

  @JsonProperty
  public Long getPeriodId() {
    return periodId;
  }

  public void setPeriodId(Long periodId) {
    this.periodId = periodId;
  }

  @JsonProperty
  public Long getSourceId() {
    return sourceId;
  }

  public void setSourceId(Long sourceId) {
    this.sourceId = sourceId;
  }

//    @JsonProperty
//    public int getCategoryOptionComboId()
//    {
//        return categoryOptionComboId;
//    }
//
//    public void setCategoryOptionComboId( int categoryOptionComboId )
//    {
//        this.categoryOptionComboId = categoryOptionComboId;
//    }

//    @JsonProperty
//    public int getAttributeOptionComboId()
//    {
//        return attributeOptionComboId;
//    }
//
//    public void setAttributeOptionComboId( int attributeOptionComboId )
//    {
//        this.attributeOptionComboId = attributeOptionComboId;
//    }

  @JsonProperty
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getStoredBy() {
    return storedBy;
  }

  public void setStoredBy(String storedBy) {
    this.storedBy = storedBy;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @JsonProperty
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @JsonProperty
  public boolean isFollowup() {
    return followup;
  }

  public void setFollowup(boolean followup) {
    this.followup = followup;
  }

  @JsonProperty
  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  @JsonProperty
  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  @JsonProperty
  public String getDataElementName() {
    return dataElementName;
  }

  public void setDataElementName(String dataElementName) {
    this.dataElementName = dataElementName;
  }

  @JsonProperty
  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  @JsonProperty
  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  @JsonProperty
  public String getCategoryOptionComboName() {
    return categoryOptionComboName;
  }

  public void setCategoryOptionComboName(String categoryOptionComboName) {
    this.categoryOptionComboName = categoryOptionComboName;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void setPeriod(String periodTypeName, Date startDate, Date endDate) {
    period.setPeriodType(PeriodType.getPeriodTypeByName(periodTypeName));
    period.setStartDate(startDate);
    period.setEndDate(endDate);
  }

//    public String getCategoryOptionComboNameParsed()
//    {
//        return categoryOptionComboName != null && categoryOptionComboName.equals( DEFAULT_TOSTRING ) ?
//            "" :
//            categoryOptionComboName;
//    }

  // -------------------------------------------------------------------------
  // hashCode and equals
  // -------------------------------------------------------------------------

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;

    result = (int) (prime * result + dataElementId);
    result = (int) (prime * result + periodId);
    result = (int) (prime * result + sourceId);
//        result = prime * result + categoryOptionComboId;

    return result;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (getClass() != object.getClass()) {
      return false;
    }

    final DeflatedDataValue other = (DeflatedDataValue) object;

    return dataElementId == other.dataElementId && periodId == other.periodId &&
        sourceId == other.sourceId;
  }
}
