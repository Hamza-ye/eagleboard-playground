package com.mass3d.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
//import com.mass3d.category.CategoryCombo;
import com.mass3d.common.AccessLevel;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.BaseNameableObject;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.MetadataObject;
//import com.mass3d.common.ObjectStyle;
import com.mass3d.common.VersionedObject;
import com.mass3d.common.adapter.JacksonPeriodTypeDeserializer;
import com.mass3d.common.adapter.JacksonPeriodTypeSerializer;
import com.mass3d.dataelement.DataElement;
//import com.mass3d.dataentryform.DataEntryForm;
//import com.mass3d.organisationunit.FeatureType;
//import com.mass3d.organisationunit.OrganisationUnit;
import com.mass3d.period.PeriodType;
//import com.mass3d.program.notification.ProgramNotificationTemplate;
//import com.mass3d.programrule.ProgramRuleVariable;
import com.mass3d.schema.annotation.PropertyRange;
//import com.mass3d.trackedentity.TrackedEntityAttribute;
//import com.mass3d.trackedentity.TrackedEntityType;
import com.mass3d.user.UserAuthorityGroup;

@JacksonXmlRootElement( localName = "program", namespace = DxfNamespaces.DXF_2_0 )
public class Program
   // extends BaseNameableObject
   // implements VersionedObject, MetadataObject
{
//    private String formName;
//
//    private int version;
//
//    private String enrollmentDateLabel;
//
//    private String incidentDateLabel;
//
//    private Set<OrganisationUnit> organisationUnits = new HashSet<>();
//
//    private Set<ProgramStage> programStages = new HashSet<>();
//
//    private Set<ProgramSection> programSections = new HashSet<>();
//
//    private ProgramType programType;
//
//    private Boolean displayIncidentDate = true;
//
//    private Boolean ignoreOverdueEvents = false;
//
//    private List<ProgramTrackedEntityAttribute> programAttributes = new ArrayList<>();
//
//    private Set<UserAuthorityGroup> userRoles = new HashSet<>();
//
//    private Set<ProgramIndicator> programIndicators = new HashSet<>();
//
//    private Set<ProgramRuleVariable> programRuleVariables = new HashSet<>();
//
//    private Boolean onlyEnrollOnce = false;
//
//    private Set<ProgramNotificationTemplate> notificationTemplates = new HashSet<>();
//
//    private Boolean selectEnrollmentDatesInFuture = false;
//
//    private Boolean selectIncidentDatesInFuture = false;
//
//    private Program relatedProgram;
//
//    private TrackedEntityType trackedEntityType;
//
//    private DataEntryForm dataEntryForm;
//
//    private ObjectStyle style;
//
//    /**
//     * The CategoryCombo used for data attributes.
//     */
//    private CategoryCombo categoryCombo;
//
//    /**
//     * Property indicating whether offline storage is enabled for this program
//     * or not
//     */
//    private boolean skipOffline;
//
//    /**
//     * Property indicating whether a list of tracked entity instances should be
//     * displayed, or whether a query must be made.
//     */
//    private Boolean displayFrontPageList = false;
//
//    /**
//     * Property indicating whether first stage can appear for data entry on the
//     * same page with registration
//     */
//    private Boolean useFirstStageDuringRegistration = false;
//
//    /**
//     * Property indicating type of feature - none, point, symbol, polygon or
//     * multipolygon - to capture for program.
//     */
//    private FeatureType featureType;
//
//    /**
//     * How many days after period is over will this program block creation and modification of events
//     */
//    private int expiryDays;
//
//    /**
//     * The PeriodType indicating the frequency that this program will use to decide on expiration. This
//     * relates to the {@link Program#expiryDays} property. The end date of the relevant period is used
//     * as basis for the number of expiration days.
//     */
//    private PeriodType expiryPeriodType;
//
//    /**
//     * How many days after an event is completed will this program block modification of the event
//     */
//    private int completeEventsExpiryDays;
//
//    /**
//     * Property indicating minimum number of attributes required to fill
//     * before search is triggered
//     */
//    private int minAttributesRequiredToSearch = 1;
//
//    /**
//     * Property indicating maximum number of TEI to return after search
//     */
//    private int maxTeiCountToReturn = 0;
//
//    /**
//     * Property indicating level of access
//     */
//    private AccessLevel accessLevel = AccessLevel.OPEN;
//
//    // -------------------------------------------------------------------------
//    // Constructors
//    // -------------------------------------------------------------------------
//
//    public Program()
//    {
//    }
//
//    public Program( String name, String description )
//    {
//        this.name = name;
//        this.description = description;
//    }
//
//    // -------------------------------------------------------------------------
//    // Logic methods
//    // -------------------------------------------------------------------------
//
//    public void addOrganisationUnit( OrganisationUnit organisationUnit )
//    {
//        organisationUnits.add( organisationUnit );
//        organisationUnit.getPrograms().add( this );
//    }
//
//    public void removeOrganisationUnit( OrganisationUnit organisationUnit )
//    {
//        organisationUnits.remove( organisationUnit );
//        organisationUnit.getPrograms().remove( this );
//    }
//
//    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
//    {
//        Set<OrganisationUnit> toRemove = Sets.difference( organisationUnits, updates );
//        Set<OrganisationUnit> toAdd = Sets.difference( updates, organisationUnits );
//
//        toRemove.forEach( u -> u.getPrograms().remove( this ) );
//        toAdd.forEach( u -> u.getPrograms().add( this ) );
//
//        organisationUnits.clear();
//        organisationUnits.addAll( updates );
//    }
//
//    /**
//     * Returns IDs of searchable TrackedEntityAttributes.
//     */
//    public List<String> getSearchableAttributeIds()
//    {
//        return programAttributes.stream()
//            .filter( pa -> pa.getAttribute().isSystemWideUnique() || pa.isSearchable() )
//            .map( ProgramTrackedEntityAttribute::getAttribute )
//            .map( TrackedEntityAttribute::getUid )
//            .collect( Collectors.toList() );
//    }
//
//    /**
//     * Returns display in list TrackedEntityAttributes
//     */
//    public List<TrackedEntityAttribute> getDisplayInListAttributes()
//    {
//        return programAttributes.stream()
//            .filter( pa -> pa.isDisplayInList() )
//            .map( ProgramTrackedEntityAttribute::getAttribute )
//            .collect( Collectors.toList() );
//    }
//
//    /**
//     * Returns the ProgramTrackedEntityAttribute of this Program which contains
//     * the given TrackedEntityAttribute.
//     */
//    public ProgramTrackedEntityAttribute getAttribute( TrackedEntityAttribute attribute )
//    {
//        for ( ProgramTrackedEntityAttribute programAttribute : programAttributes )
//        {
//            if ( programAttribute != null && programAttribute.getAttribute().equals( attribute ) )
//            {
//                return programAttribute;
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * Returns all data elements which are part of the stages of this program.
//     */
//    public Set<DataElement> getDataElements()
//    {
//        return programStages.stream()
//            .flatMap( ps -> ps.getDataElements().stream() )
//            .collect( Collectors.toSet() );
//    }
//
//    /**
//     * Returns data elements which are part of the stages of this program which
//     * have a legend set and is of numeric value type.
//     */
//    public Set<DataElement> getDataElementsWithLegendSet()
//    {
//        return getDataElements().stream()
//            .filter( de -> de.hasLegendSet() && de.isNumericType() )
//            .collect( Collectors.toSet() );
//    }
//
//    /**
//     * Returns TrackedEntityAttributes from ProgramTrackedEntityAttributes. Use
//     * getAttributes() to access the persisted attribute list.
//     */
//    public List<TrackedEntityAttribute> getTrackedEntityAttributes()
//    {
//        return programAttributes.stream()
//            .map( ProgramTrackedEntityAttribute::getAttribute )
//            .collect( Collectors.toList() );
//    }
//
//    /**
//     * Returns non-confidential TrackedEntityAttributes from ProgramTrackedEntityAttributes. Use
//     * getAttributes() to access the persisted attribute list.
//     */
//    public List<TrackedEntityAttribute> getNonConfidentialTrackedEntityAttributes()
//    {
//        return getTrackedEntityAttributes().stream()
//            .filter( a -> !a.isConfidentialBool() )
//            .collect( Collectors.toList() );
//    }
//
//    /**
//     * Returns TrackedEntityAttributes from ProgramTrackedEntityAttributes which
//     * have a legend set and is of numeric value type.
//     */
//    public List<TrackedEntityAttribute> getNonConfidentialTrackedEntityAttributesWithLegendSet()
//    {
//        return getTrackedEntityAttributes().stream()
//            .filter( a -> !a.isConfidentialBool() && a.hasLegendSet() && a.isNumericType() )
//            .collect( Collectors.toList() );
//    }
//
//    /**
//     * Indicates whether this program contains the given data element.
//     */
//    public boolean containsDataElement( DataElement dataElement )
//    {
//        for ( ProgramStage stage : programStages )
//        {
//            for ( ProgramStageDataElement element : stage.getProgramStageDataElements() )
//            {
//                if ( dataElement.getUid().equals( element.getDataElement().getUid() ) )
//                {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * Indicates whether this program contains the given tracked entity
//     * attribute.
//     */
//    public boolean containsAttribute( TrackedEntityAttribute attribute )
//    {
//        for ( ProgramTrackedEntityAttribute programAttribute : programAttributes )
//        {
//            if ( attribute.equals( programAttribute.getAttribute() ) )
//            {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public ProgramStage getProgramStageByStage( int stage )
//    {
//        int count = 1;
//
//        for ( ProgramStage programStage : programStages )
//        {
//            if ( count == stage )
//            {
//                return programStage;
//            }
//
//            count++;
//        }
//
//        return null;
//    }
//
//    public boolean isSingleProgramStage()
//    {
//        return programStages != null && programStages.size() == 1;
//    }
//
//    public boolean hasOrganisationUnit( OrganisationUnit unit )
//    {
//        return organisationUnits.contains( unit );
//    }
//
//    @Override
//    public int increaseVersion()
//    {
//        return ++version;
//    }
//
//    public boolean isOpen()
//    {
//        return this.accessLevel == AccessLevel.OPEN;
//    }
//
//    public boolean isAudited()
//    {
//        return this.accessLevel == AccessLevel.AUDITED;
//    }
//
//    public boolean isProtected()
//    {
//        return this.accessLevel == AccessLevel.PROTECTED;
//    }
//
//    public boolean isClosed()
//    {
//        return this.accessLevel == AccessLevel.CLOSED;
//    }
//
//    // -------------------------------------------------------------------------
//    // Getters and setters
//    // -------------------------------------------------------------------------
//
//    @Override
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public int getVersion()
//    {
//        return version;
//    }
//
//    @Override
//    public void setVersion( int version )
//    {
//        this.version = version;
//    }
//
//    @JsonProperty( "organisationUnits" )
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<OrganisationUnit> getOrganisationUnits()
//    {
//        return organisationUnits;
//    }
//
//    public void setOrganisationUnits( Set<OrganisationUnit> organisationUnits )
//    {
//        this.organisationUnits = organisationUnits;
//    }
//
//    @JsonProperty( "programStages" )
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "programStages", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "programStage", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<ProgramStage> getProgramStages()
//    {
//        return programStages;
//    }
//
//    public void setProgramStages( Set<ProgramStage> programStages )
//    {
//        this.programStages = programStages;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    @PropertyRange( min = 2 )
//    public String getEnrollmentDateLabel()
//    {
//        return enrollmentDateLabel;
//    }
//
//    public void setEnrollmentDateLabel( String enrollmentDateLabel )
//    {
//        this.enrollmentDateLabel = enrollmentDateLabel;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    @PropertyRange( min = 2 )
//    public String getIncidentDateLabel()
//    {
//        return incidentDateLabel;
//    }
//
//    public void setIncidentDateLabel( String incidentDateLabel )
//    {
//        this.incidentDateLabel = incidentDateLabel;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public ProgramType getProgramType()
//    {
//        return programType;
//    }
//
//    public void setProgramType( ProgramType programType )
//    {
//        this.programType = programType;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Boolean getDisplayIncidentDate()
//    {
//        return displayIncidentDate;
//    }
//
//    public void setDisplayIncidentDate( Boolean displayIncidentDate )
//    {
//        this.displayIncidentDate = displayIncidentDate;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Boolean getIgnoreOverdueEvents()
//    {
//        return ignoreOverdueEvents;
//    }
//
//    public void setIgnoreOverdueEvents( Boolean ignoreOverdueEvents )
//    {
//        this.ignoreOverdueEvents = ignoreOverdueEvents;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public boolean isRegistration()
//    {
//        return programType == ProgramType.WITH_REGISTRATION;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public boolean isWithoutRegistration()
//    {
//        return programType == ProgramType.WITHOUT_REGISTRATION;
//    }
//
//    @JsonProperty
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "userRoles", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "userRole", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<UserAuthorityGroup> getUserRoles()
//    {
//        return userRoles;
//    }
//
//    public void setUserRoles( Set<UserAuthorityGroup> userRoles )
//    {
//        this.userRoles = userRoles;
//    }
//
//    @JsonProperty
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "programIndicators", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "programIndicator", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<ProgramIndicator> getProgramIndicators()
//    {
//        return programIndicators;
//    }
//
//    public void setProgramIndicators( Set<ProgramIndicator> programIndicators )
//    {
//        this.programIndicators = programIndicators;
//    }
//
//    @JsonProperty
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "programRuleVariables", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "programRuleVariable", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<ProgramRuleVariable> getProgramRuleVariables()
//    {
//        return programRuleVariables;
//    }
//
//    public void setProgramRuleVariables( Set<ProgramRuleVariable> programRuleVariables )
//    {
//        this.programRuleVariables = programRuleVariables;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Boolean getOnlyEnrollOnce()
//    {
//        return onlyEnrollOnce;
//    }
//
//    public void setOnlyEnrollOnce( Boolean onlyEnrollOnce )
//    {
//        this.onlyEnrollOnce = onlyEnrollOnce;
//    }
//
//    @JsonProperty
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlProperty( localName = "notificationTemplate", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlElementWrapper( localName = "notificationTemplates", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<ProgramNotificationTemplate> getNotificationTemplates()
//    {
//        return notificationTemplates;
//    }
//
//    public void setNotificationTemplates( Set<ProgramNotificationTemplate> notificationTemplates )
//    {
//        this.notificationTemplates = notificationTemplates;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Boolean getSelectEnrollmentDatesInFuture()
//    {
//        return selectEnrollmentDatesInFuture;
//    }
//
//    public void setSelectEnrollmentDatesInFuture( Boolean selectEnrollmentDatesInFuture )
//    {
//        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Boolean getSelectIncidentDatesInFuture()
//    {
//        return selectIncidentDatesInFuture;
//    }
//
//    public void setSelectIncidentDatesInFuture( Boolean selectIncidentDatesInFuture )
//    {
//        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
//    }
//
//    @JsonProperty
//    @JsonSerialize( as = BaseIdentifiableObject.class )
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Program getRelatedProgram()
//    {
//        return relatedProgram;
//    }
//
//    public void setRelatedProgram( Program relatedProgram )
//    {
//        this.relatedProgram = relatedProgram;
//    }
//
//    @JsonProperty( "programTrackedEntityAttributes" )
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "programTrackedEntityAttributes", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "programTrackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0 )
//    public List<ProgramTrackedEntityAttribute> getProgramAttributes()
//    {
//        return programAttributes;
//    }
//
//    public void setProgramAttributes( List<ProgramTrackedEntityAttribute> programAttributes )
//    {
//        this.programAttributes = programAttributes;
//    }
//
//    @JsonProperty
//    @JacksonXmlElementWrapper( localName = "trackedEntityType", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "trackedEntityType", namespace = DxfNamespaces.DXF_2_0 )
//    public TrackedEntityType getTrackedEntityType()
//    {
//        return trackedEntityType;
//    }
//
//    public void setTrackedEntityType( TrackedEntityType trackedEntityType )
//    {
//        this.trackedEntityType = trackedEntityType;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( localName = "dataEntryForm", namespace = DxfNamespaces.DXF_2_0 )
//    public DataEntryForm getDataEntryForm()
//    {
//        return dataEntryForm;
//    }
//
//    public void setDataEntryForm( DataEntryForm dataEntryForm )
//    {
//        this.dataEntryForm = dataEntryForm;
//    }
//
//    @JsonProperty
//    @JsonSerialize( as = BaseIdentifiableObject.class )
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public CategoryCombo getCategoryCombo()
//    {
//        return categoryCombo;
//    }
//
//    public void setCategoryCombo( CategoryCombo categoryCombo )
//    {
//        this.categoryCombo = categoryCombo;
//    }
//
//    /**
//     * Indicates whether this program has a category combination which is different
//     * from the default category combination.
//     */
//    public boolean hasCategoryCombo()
//    {
//        return categoryCombo != null && !CategoryCombo.DEFAULT_CATEGORY_COMBO_NAME.equals( categoryCombo.getName() );
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public boolean isSkipOffline()
//    {
//        return skipOffline;
//    }
//
//    public void setSkipOffline( boolean skipOffline )
//    {
//        this.skipOffline = skipOffline;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Boolean getDisplayFrontPageList()
//    {
//        return displayFrontPageList;
//    }
//
//    public void setDisplayFrontPageList( Boolean displayFrontPageList )
//    {
//        this.displayFrontPageList = displayFrontPageList;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public Boolean getUseFirstStageDuringRegistration()
//    {
//        return useFirstStageDuringRegistration;
//    }
//
//    public void setUseFirstStageDuringRegistration( Boolean useFirstStageDuringRegistration )
//    {
//        this.useFirstStageDuringRegistration = useFirstStageDuringRegistration;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public FeatureType getFeatureType()
//    {
//        return featureType;
//    }
//
//    public void setFeatureType( FeatureType featureType )
//    {
//        this.featureType = featureType;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public int getExpiryDays()
//    {
//        return expiryDays;
//    }
//
//    public void setExpiryDays( int expiryDays )
//    {
//        this.expiryDays = expiryDays;
//    }
//
//    @JsonProperty
//    @JsonSerialize( using = JacksonPeriodTypeSerializer.class )
//    @JsonDeserialize( using = JacksonPeriodTypeDeserializer.class )
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public PeriodType getExpiryPeriodType()
//    {
//        return expiryPeriodType;
//    }
//
//    public void setExpiryPeriodType( PeriodType expiryPeriodType )
//    {
//        this.expiryPeriodType = expiryPeriodType;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public int getCompleteEventsExpiryDays()
//    {
//        return completeEventsExpiryDays;
//    }
//
//    public void setCompleteEventsExpiryDays( int completeEventsExpiryDays )
//    {
//        this.completeEventsExpiryDays = completeEventsExpiryDays;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public int getMinAttributesRequiredToSearch()
//    {
//        return minAttributesRequiredToSearch;
//    }
//
//    public void setMinAttributesRequiredToSearch( int minAttributesRequiredToSearch )
//    {
//        this.minAttributesRequiredToSearch = minAttributesRequiredToSearch;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public int getMaxTeiCountToReturn()
//    {
//        return maxTeiCountToReturn;
//    }
//
//    public void setMaxTeiCountToReturn( int maxTeiCountToReturn )
//    {
//        this.maxTeiCountToReturn = maxTeiCountToReturn;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public ObjectStyle getStyle()
//    {
//        return style;
//    }
//
//    public void setStyle( ObjectStyle style )
//    {
//        this.style = style;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public String getFormName()
//    {
//        return formName;
//    }
//
//    public void setFormName( String formName )
//    {
//        this.formName = formName;
//    }
//
//    @JsonProperty( "programSections" )
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "programSections", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "programSection", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<ProgramSection> getProgramSections()
//    {
//        return programSections;
//    }
//
//    public void setProgramSections( Set<ProgramSection> programSections )
//    {
//        this.programSections = programSections;
//    }
//
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public AccessLevel getAccessLevel()
//    {
//        return accessLevel;
//    }
//
//    public void setAccessLevel( AccessLevel accessLevel )
//    {
//        this.accessLevel = accessLevel;
//    }
}