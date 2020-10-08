package com.mass3d.period;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.mass3d.common.BaseDimensionalItemObject;
import com.mass3d.common.DimensionItemType;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.adapter.JacksonPeriodTypeDeserializer;
import com.mass3d.common.adapter.JacksonPeriodTypeSerializer;
import com.mass3d.schema.PropertyType;
import com.mass3d.schema.annotation.Property;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
import org.joda.time.Days;

//@Entity
//@Table(name = "period", uniqueConstraints=
//@UniqueConstraint(columnNames={"periodtypeid", "startDate", "endDate"}))
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@AttributeOverride(name = "id", column = @Column(name = "periodid"))
//@AssociationOverride(
//    name="userGroupAccesses",
//    joinTable=@JoinTable(
//        name="periodusergroupaccesses",
//        joinColumns=@JoinColumn(name="periodid"),
//        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
//    )
//)
//@AssociationOverride(
//    name="userAccesses",
//    joinTable=@JoinTable(
//        name="perioduseraccesses",
//        joinColumns=@JoinColumn(name="periodid"),
//        inverseJoinColumns=@JoinColumn(name="useraccessid")
//    )
//)
@JacksonXmlRootElement(localName = "period", namespace = DxfNamespaces.DXF_2_0)
public class Period
    extends BaseDimensionalItemObject {

  public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

  /**
   * Required.
   */
  @ManyToOne
  @JoinColumn(name = "periodtypeid")
  private PeriodType periodType;

  /**
   * Required. Must be unique together with endDate.
   */
  private Date startDate;

  /**
   * Required. Must be unique together with startDate.
   */
  private Date endDate;

  /**
   * Transient string holding the ISO representation of the period.
   */
  private transient String isoPeriod;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public Period() {
  }

  public Period(Period period) {
    this.id = period.getId();
    this.periodType = period.getPeriodType();
    this.startDate = period.getStartDate();
    this.endDate = period.getEndDate();
    this.name = period.getName();
    this.isoPeriod = period.getIsoDate();
  }

  protected Period(PeriodType periodType, Date startDate, Date endDate) {
    this.periodType = periodType;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  protected Period(PeriodType periodType, Date startDate, Date endDate, String isoPeriod) {
    this.periodType = periodType;
    this.startDate = startDate;
    this.endDate = endDate;
    this.isoPeriod = isoPeriod;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  @Override
  public void setAutoFields() {
  }

  @Override
  public String getDimensionItem() {
    return getIsoDate();
  }

  @Override
  public String getUid() {
    return uid != null ? uid : getIsoDate();
  }

  public String getRealUid() {
    return uid;
  }

  @Override
  public String getCode() {
    return getIsoDate();
  }

  @Override
  public String getName() {
    return name != null ? name : getIsoDate();
  }

  @Override
  public String getShortName() {
    return shortName != null ? shortName : getIsoDate();
  }

  /**
   * Returns an ISO8601 formatted string version of the period.
   *
   * @return the period string
   */
  public String getIsoDate() {
    return isoPeriod != null ? isoPeriod : periodType.getIsoDate(this);
  }

  /**
   * Copies the transient properties (name) from the argument Period to this Period.
   *
   * @param other Period to copy from.
   * @return this Period.
   */
  public Period copyTransientProperties(Period other) {
    this.name = other.getName();
    this.shortName = other.getShortName();
    this.code = other.getCode();

    return this;
  }

  /**
   * Returns the frequency order of the period type of the period.
   *
   * @return the frequency order.
   */
  public int frequencyOrder() {
    return periodType != null ? periodType.getFrequencyOrder() : YearlyPeriodType.FREQUENCY_ORDER;
  }

  /**
   * Returns start date formatted as string.
   *
   * @return start date formatted as string.
   */
  public String getStartDateString() {
    return getMediumDateString(startDate);
  }

  /**
   * Returns end date formatted as string.
   *
   * @return end date formatted as string.
   */
  public String getEndDateString() {
    return getMediumDateString(endDate);
  }

  /**
   * Formats a Date to the format YYYY-MM-DD.
   *
   * @param date the Date to parse.
   * @return A formatted date string. Null if argument is null.
   */
  private String getMediumDateString(Date date) {
    final SimpleDateFormat format = new SimpleDateFormat();

    format.applyPattern(DEFAULT_DATE_FORMAT);

    return date != null ? format.format(date) : null;
  }

  /**
   * Returns the potential number of periods of the given period type which is spanned by this
   * period.
   *
   * @param type the period type.
   * @return the potential number of periods of the given period type spanned by this period.
   */
  public int getPeriodSpan(PeriodType type) {
    double no = (double) this.periodType.getFrequencyOrder() / type.getFrequencyOrder();

    return (int) Math.floor(no);
  }

  /**
   * Returns the number of days in the period, i.e. the days between the start and end date.
   *
   * @return number of days in period.
   */
  public int getDaysInPeriod() {
    Days days = Days.daysBetween(new DateTime(startDate), new DateTime(endDate));
    return days.getDays() + 1;
  }

  /**
   * Validates this period. TODO Make more comprehensive.
   */
  public boolean isValid() {
    if (startDate == null || endDate == null || periodType == null) {
      return false;
    }

    return DailyPeriodType.NAME.equals(periodType.getName()) || getDaysInPeriod() >= 2;
  }

  /**
   * Determines whether this is a future period relative to the current time.
   *
   * @return true if this period ends in the future, false otherwise.
   */
  public boolean isFuture() {
    return getEndDate().after(new Date());
  }

  /**
   * Indicates whether this period is after the given period. Bases the comparison on the end dates
   * of the periods. If the given period is null, false is returned.
   *
   * @param period the period to compare.
   * @return true if this period is after the given period.
   */
  public boolean isAfter(Period period) {
    if (period == null || period.getEndDate() == null) {
      return false;
    }

    return getEndDate().after(period.getEndDate());
  }

  /**
   * Returns a unique key suitable for caching and lookups.
   */
  public String getCacheKey() {
    return periodType.getName() + "-" + startDate.toString() + "-" + endDate.toString();
  }

  // -------------------------------------------------------------------------
  // DimensionalItemObject
  // -------------------------------------------------------------------------

  @Override
  public DimensionItemType getDimensionItemType() {
    return DimensionItemType.PERIOD;
  }

  // -------------------------------------------------------------------------
  // hashCode, equals and toString
  // -------------------------------------------------------------------------

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;

    result = result * prime + (startDate != null ? startDate.hashCode() : 0);
    result = result * prime + (endDate != null ? endDate.hashCode() : 0);
    result = result * prime + (periodType != null ? periodType.hashCode() : 0);

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null) {
      return false;
    }

    if (!(o instanceof Period)) {
      return false;
    }

    final Period other = (Period) o;

    return startDate.equals(other.getStartDate()) &&
        endDate.equals(other.getEndDate()) &&
        periodType.equals(other.getPeriodType());
  }

  @Override
  public String toString() {
    return "[" + (periodType == null ? "" : periodType.getName() + ": ") + startDate + " - "
        + endDate + "]";
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  @JsonProperty
  @JsonSerialize(using = JacksonPeriodTypeSerializer.class)
  @JsonDeserialize(using = JacksonPeriodTypeDeserializer.class)
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  @Property(PropertyType.TEXT)
  public PeriodType getPeriodType() {
    return periodType;
  }

  public void setPeriodType(PeriodType periodType) {
    this.periodType = periodType;
  }
}
