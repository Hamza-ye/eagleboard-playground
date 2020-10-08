package com.mass3d.indicator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.common.TotalAggregationType;
import com.mass3d.dataset.DataSet;
import java.util.HashSet;
import java.util.Set;
import com.mass3d.common.BaseDataDimensionalItemObject;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.DimensionItemType;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.MetadataObject;
import com.mass3d.schema.PropertyType;
import com.mass3d.schema.annotation.Property;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

//@Entity
//@Table(name = "indicator")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@AttributeOverride(name = "id", column = @Column(name = "indicatorid"))
//@AssociationOverride(
//    name="userGroupAccesses",
//    joinTable=@JoinTable(
//        name="indicatorusergroupaccesses",
//        joinColumns=@JoinColumn(name="indicatorid"),
//        inverseJoinColumns=@JoinColumn(name="usergroupaccessid")
//    )
//)
//@AssociationOverride(
//    name="userAccesses",
//    joinTable=@JoinTable(
//        name="indicatoruseraccesses",
//        joinColumns=@JoinColumn(name="indicatorid"),
//        inverseJoinColumns=@JoinColumn(name="useraccessid")
//    )
//)
@JacksonXmlRootElement(localName = "indicator", namespace = DxfNamespaces.DXF_2_0)
public class Indicator
    extends BaseDataDimensionalItemObject implements MetadataObject {

  private boolean annualized;

  private String formName;

  /**
   * Number of decimals to use for indicator value, null implies default.
   */
  private Integer decimals;

  @ManyToOne
  @JoinColumn(name = "indicatortypeid")
  private IndicatorType indicatorType;

  private String numerator;

  private String numeratorDescription;

  private transient String explodedNumerator;

  private String denominator;

  private String denominatorDescription;

  private transient String explodedDenominator;

  private String url;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "indicatorgroupmembers",
      joinColumns = @JoinColumn(name = "indicatorid", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "indicatorgroupid", referencedColumnName = "id")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<IndicatorGroup> groups = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "datasetindicators",
      joinColumns = @JoinColumn(name = "indicatorid", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "datasetid", referencedColumnName = "id")
  )
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  private Set<DataSet> dataSets = new HashSet<>();

  public Indicator() {
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

  public void addIndicatorGroup(IndicatorGroup group) {
    groups.add(group);
    group.getMembers().add(this);
  }

  public void removeIndicatorGroup(IndicatorGroup group) {
    groups.remove(group);
    group.getMembers().remove(this);
  }

  public void updateIndicatorGroups(Set<IndicatorGroup> updates) {
    for (IndicatorGroup group : new HashSet<>(groups)) {
      if (!updates.contains(group)) {
        removeIndicatorGroup(group);
      }
    }

    for (IndicatorGroup group : updates) {
      addIndicatorGroup(group);
    }
  }

  public void addFieldSet(DataSet dataSet) {
    this.dataSets.add(dataSet);
    dataSet.getIndicators().add(this);
  }

  public void removeFieldSet(DataSet dataSet) {
    this.dataSets.remove(dataSet);
    dataSet.getIndicators().remove(this);
  }

  public boolean hasDecimals() {
    return decimals != null && decimals >= 0;
  }

  public boolean hasZeroDecimals() {
    return decimals != null && decimals == 0;
  }

  // -------------------------------------------------------------------------
  // DimensionalItemObject
  // -------------------------------------------------------------------------

  @Override
  public DimensionItemType getDimensionItemType() {
    return DimensionItemType.INDICATOR;
  }

  /**
   * A denominator value of "1" implies that there is no denominator and that the indicator
   * represents a sum.
   */
    @Override
    public TotalAggregationType getTotalAggregationType()
    {
        return "1".equals( denominator ) ? TotalAggregationType.SUM : TotalAggregationType.AVERAGE;
    }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isAnnualized() {
    return annualized;
  }

  public void setAnnualized(boolean annualized) {
    this.annualized = annualized;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public Integer getDecimals() {
    return decimals;
  }

  public void setDecimals(Integer decimals) {
    this.decimals = decimals;
  }

  @JsonProperty
  @JsonSerialize(as = BaseIdentifiableObject.class)
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public IndicatorType getIndicatorType() {
    return indicatorType;
  }

  public void setIndicatorType(IndicatorType indicatorType) {
    this.indicatorType = indicatorType;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getNumerator() {
    return numerator;
  }

  public void setNumerator(String numerator) {
    this.numerator = numerator;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getNumeratorDescription() {
    return numeratorDescription;
  }

  public void setNumeratorDescription(String numeratorDescription) {
    this.numeratorDescription = numeratorDescription;
  }

  @JsonIgnore
  public String getExplodedNumerator() {
    return explodedNumerator;
  }

  public void setExplodedNumerator(String explodedNumerator) {
    this.explodedNumerator = explodedNumerator;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getDenominator() {
    return denominator;
  }

  public void setDenominator(String denominator) {
    this.denominator = denominator;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getDenominatorDescription() {
    return denominatorDescription;
  }

  public void setDenominatorDescription(String denominatorDescription) {
    this.denominatorDescription = denominatorDescription;
  }

  @JsonIgnore
  public String getExplodedDenominator() {
    return explodedDenominator;
  }

  public void setExplodedDenominator(String explodedDenominator) {
    this.explodedDenominator = explodedDenominator;
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

  @JsonProperty("indicatorGroups")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "indicatorGroups", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "indicatorGroup", namespace = DxfNamespaces.DXF_2_0)
  public Set<IndicatorGroup> getGroups() {
    return groups;
  }

  public void setGroups(Set<IndicatorGroup> groups) {
    this.groups = groups;
  }

  @JsonProperty
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  @JacksonXmlElementWrapper(localName = "dataSets", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "fieldSet", namespace = DxfNamespaces.DXF_2_0)
  public Set<DataSet> getDataSets() {
    return dataSets;
  }

  public void setDataSets(Set<DataSet> dataSets) {
    this.dataSets = dataSets;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getFormName() {
    return formName;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }
}
