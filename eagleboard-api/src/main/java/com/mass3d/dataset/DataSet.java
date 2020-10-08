package com.mass3d.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.ImmutableSet;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import com.mass3d.common.BaseDimensionalItemObject;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DimensionItemType;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.InterpretableObject;
import com.mass3d.common.MetadataObject;
import com.mass3d.common.VersionedObject;
import com.mass3d.common.adapter.JacksonPeriodTypeDeserializer;
import com.mass3d.common.adapter.JacksonPeriodTypeSerializer;
import com.mass3d.dataelement.DataElement;
import com.mass3d.todotask.TodoTask;
import com.mass3d.indicator.Indicator;
import com.mass3d.interpretation.Interpretation;
import com.mass3d.period.Period;
import com.mass3d.period.PeriodType;
import com.mass3d.schema.PropertyType;
import com.mass3d.schema.annotation.Property;
import com.mass3d.schema.annotation.PropertyRange;
import com.mass3d.security.Authorities;
import com.mass3d.user.User;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;

//@Entity
//@Table(name = "dataset")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@AttributeOverride(name = "id", column = @Column(name = "datasetid"))
//@AssociationOverride(
//    name="userGroupAccesses",
//    joinTable=@JoinTable(
//        name="datasetusergroupaccesses",
//        joinColumns=@JoinColumn(name="datasetid"),
//        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
//    )
//)
//@AssociationOverride(
//    name="userAccesses",
//    joinTable=@JoinTable(
//        name="datasetuseraccesses",
//        joinColumns=@JoinColumn(name="datasetid"),
//        inverseJoinColumns=@JoinColumn(name="useraccessid")
//    )
//)
@JacksonXmlRootElement(localName = "dataSet", namespace = DxfNamespaces.DXF_2_0)
public class DataSet
    extends BaseDimensionalItemObject
    implements VersionedObject, MetadataObject, InterpretableObject {

  public static final int NO_EXPIRY = 0;

  @OneToMany(
      mappedBy = "dataSet",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<DataSetElement> dataSetElements = new HashSet<>();

  @ManyToMany
  @JoinTable(name = "datasetsource",
      joinColumns = @JoinColumn(name = "datasetid", referencedColumnName = "datasetid"),
      inverseJoinColumns = @JoinColumn(name = "sourceid", referencedColumnName = "todotaskid")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<TodoTask> sources = new HashSet<>();

  private String formName;
  /**
   * The PeriodType indicating the frequency that this DataSet should be used
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "periodtypeid")
  private PeriodType periodType;
  /**
   * The dataInputPeriods is a set of periods with opening and closing dates, which determines the
   * period of which data can belong (period) and at which dates (between opening and closing dates)
   * actually registering this data is allowed. The same period can exist at the same time with
   * different opening and closing dates to allow for multiple periods for registering data.
   */
  @OneToMany (cascade = CascadeType.ALL)
  @JoinTable(
      name = "datasetdatainputperiods",
      joinColumns = @JoinColumn(name = "datasetid", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "datainputperiodid", referencedColumnName = "id")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<DataInputPeriod> dataInputPeriods = new HashSet<>();
  /**
   * Property indicating if the dataSet could be collected using mobile data entry.
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

  /**
   * Indicators associated with this data set. Indicators are used for view and output purposes,
   * such as calculated elements in forms and reports.
   */
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "datasetindicators",
      joinColumns = @JoinColumn(name = "datasetid", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "indicatorid", referencedColumnName = "id")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<Indicator> indicators = new HashSet<>();

  /**
   * User group which will receive notifications when data set is marked complete, can be null.
   */
  @OneToMany(mappedBy = "dataSet")
  private Set<Interpretation> interpretations = new HashSet<>();

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public DataSet()
  {
  }

  public DataSet( String name )
  {
    this.name = name;
  }

  public DataSet( String name, PeriodType periodType )
  {
    this( name );
    this.periodType = periodType;
  }

  public DataSet( String name, String shortName, PeriodType periodType )
  {
    this( name, periodType );
    this.shortName = shortName;
  }

  public DataSet( String name, String shortName, String code, PeriodType periodType )
  {
    this( name, shortName, periodType );
    this.code = code;
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public boolean addDataSetElement(DataSetElement dataSetElement) {
    dataSetElement.getDataElement().getDataSetElements().add(dataSetElement);
    return dataSetElements.add(dataSetElement);
  }

  /**
   * Adds a data set element using this element set, the given data element
   *
   * @param dataElement the data element.
   */
  public boolean addDataSetElement(DataElement dataElement) {
    DataSetElement dataSetElement = new DataSetElement(this, dataElement);
    dataElement.getDataSetElements().add(dataSetElement);
    return dataSetElements.add(dataSetElement);
  }


  public Set<DataElement> getDataElements() {
    return ImmutableSet.copyOf(dataSetElements.stream().map(e -> e.getDataElement()).collect(Collectors
        .toSet()));
  }

  public boolean removeDataSetElement(DataSetElement dataSetElement) {
    dataSetElements.remove(dataSetElement);
    return dataSetElement.getDataElement().getDataSetElements().remove(dataSetElement);
  }

  public void removeDataSetElement(DataElement dataElement) {
    Iterator<DataSetElement> elements = dataSetElements.iterator();

    while (elements.hasNext()) {
      DataSetElement element = elements.next();

      DataSetElement other = new DataSetElement(this, dataElement);

      if (element.objectEquals(other)) {
        elements.remove();
        element.getDataElement().getDataSetElements().remove(element);
      }
    }
  }

  public void removeAllDataSetElements() {
    for (DataSetElement element : dataSetElements) {
      element.getDataElement().getDataSetElements().remove(element);
    }

    dataSetElements.clear();
  }

  public boolean removeTodoTask(TodoTask todoTask) {
    sources.remove(todoTask);
    return todoTask.getDataSets().remove(this);
  }

  public void addIndicator(Indicator indicator) {
    indicators.add(indicator);
    indicator.getDataSets().add(this);
  }

  public boolean removeIndicator(Indicator indicator) {
    indicators.remove(indicator);
    return indicator.getDataSets().remove(this);
  }

  /**
   * Indicates whether the data set is locked for data entry based on the expiry days.
   *
   * @param period the period to compare with.
   * @param now the date indicating now, uses current date if null.
   */
  public boolean isLocked(User user, Period period, Date now) {
    if (user != null && user.isAuthorized(Authorities.F_EDIT_EXPIRED.getAuthority())) {
      return false;
    }

    DateTime date = now != null ? new DateTime(now) : new DateTime();

    return expiryDays != DataSet.NO_EXPIRY &&
        new DateTime(period.getEndDate()).plusDays(expiryDays).isBefore(date);
  }

  /**
   * Checks if the given period and date combination conforms to any of the dataInputPeriods.
   * Returns true if no dataInputPeriods exists, or the combination conforms to at least one
   * dataInputPeriod.
   *
   * @return true if period and date conforms to a dataInputPeriod, or no dataInputPeriods exists.
   */
  public boolean isDataInputPeriodAndDateAllowed(Period period, Date date) {
    return dataInputPeriods.isEmpty() || dataInputPeriods.stream()
        .map(dataInputPeriod -> dataInputPeriod.isPeriodAndDateValid(period, date))
        .reduce((a, b) -> a || b)
        .orElse(true);
  }

  @Override
  public DimensionItemType getDimensionItemType() {
    return DimensionItemType.REPORTING_RATE;
  }
  // -------------------------------------------------------------------------
  // getters & setters
  // -------------------------------------------------------------------------

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

  @JsonProperty
  @JacksonXmlElementWrapper(localName = "dataInputPeriods", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "dataInputPeriods", namespace = DxfNamespaces.DXF_2_0)
  public Set<DataInputPeriod> getDataInputPeriods() {
    return dataInputPeriods;
  }

  public void setDataInputPeriods(Set<DataInputPeriod> dataInputPeriods) {
    this.dataInputPeriods = dataInputPeriods;
  }


  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getFormName() {
    return formName;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isMobile() {
    return mobile;
  }

  public void setMobile(boolean mobile) {
    this.mobile = mobile;
  }


  @JsonProperty
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "dataSetElements", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "dataSetElement", namespace = DxfNamespaces.DXF_2_0)
  public Set<DataSetElement> getDataSetElements() {
    return dataSetElements;
  }

  public void setDataSetElements(Set<DataSetElement> dataSetElements) {
    this.dataSetElements = dataSetElements;
  }

  @JsonProperty(value = "todoTasks")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "todoTasks", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "todoTask", namespace = DxfNamespaces.DXF_2_0)
  public Set<TodoTask> getSources() {
    return sources;
  }

  public void setSources(Set<TodoTask> sources) {
    this.sources = sources;
  }

  @JsonProperty
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "indicators", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "indicator", namespace = DxfNamespaces.DXF_2_0)
  public Set<Indicator> getIndicators() {
    return indicators;
  }

  public void setIndicators(Set<Indicator> indicators) {
    this.indicators = indicators;
  }

  @Override
  @JsonProperty
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "interpretations", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "interpretation", namespace = DxfNamespaces.DXF_2_0)
  public Set<Interpretation> getInterpretations() {
    return interpretations;
  }

  public void setInterpretations(Set<Interpretation> interpretations) {
    this.interpretations = interpretations;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  @PropertyRange(min = Integer.MIN_VALUE)
  public int getExpiryDays() {
    return expiryDays;
  }

  public void setExpiryDays(int expiryDays) {
    this.expiryDays = expiryDays;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public int getTimelyDays() {
    return timelyDays;
  }

  public void setTimelyDays(int timelyDays) {
    this.timelyDays = timelyDays;
  }

  @Override
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public int increaseVersion() {
    return ++version;
  }

}
