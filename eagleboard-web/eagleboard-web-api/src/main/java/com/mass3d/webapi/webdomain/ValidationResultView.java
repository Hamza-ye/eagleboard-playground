package com.mass3d.webapi.webdomain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement
public class ValidationResultView
{
    private String validationRuleId;

    private String validationRuleDescription;

    private String organisationUnitId;

    private String organisationUnitDisplayName;

    private String periodId;

    private String periodDisplayName;

    private String importance;

    private Double leftSideValue;

    private String operator;

    private Double rightSideValue;

    @JsonProperty
    public String getValidationRuleId()
    {
        return validationRuleId;
    }

    public void setValidationRuleId( String validationRuleId )
    {
        this.validationRuleId = validationRuleId;
    }

    @JsonProperty
    public String getValidationRuleDescription()
    {
        return validationRuleDescription;
    }

    public void setValidationRuleDescription( String validationRuleDescription )
    {
        this.validationRuleDescription = validationRuleDescription;
    }

    @JsonProperty
    public String getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    public void setOrganisationUnitId( String organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    @JsonProperty
    public String getOrganisationUnitDisplayName()
    {
        return organisationUnitDisplayName;
    }

    public void setOrganisationUnitDisplayName( String organisationUnitDisplayName )
    {
        this.organisationUnitDisplayName = organisationUnitDisplayName;
    }

    @JsonProperty
    public String getPeriodId()
    {
        return periodId;
    }

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    @JsonProperty
    public String getPeriodDisplayName()
    {
        return periodDisplayName;
    }

    public void setPeriodDisplayName( String periodDisplayName )
    {
        this.periodDisplayName = periodDisplayName;
    }

    @JsonProperty
    public String getImportance()
    {
        return importance;
    }

    public void setImportance( String importance )
    {
        this.importance = importance;
    }

    @JsonProperty
    public Double getLeftSideValue()
    {
        return leftSideValue;
    }

    public void setLeftSideValue( Double leftSideValue )
    {
        this.leftSideValue = leftSideValue;
    }

    @JsonProperty
    public String getOperator()
    {
        return operator;
    }

    public void setOperator( String operator )
    {
        this.operator = operator;
    }

    @JsonProperty
    public Double getRightSideValue()
    {
        return rightSideValue;
    }

    public void setRightSideValue( Double rightSideValue )
    {
        this.rightSideValue = rightSideValue;
    }
}
