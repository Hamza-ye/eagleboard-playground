package com.mass3d.dxf2.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.MergeMode;
import com.mass3d.dxf2.metadata.feedback.ImportReportMode;
import com.mass3d.dxf2.metadata.objectbundle.ObjectBundleMode;
import com.mass3d.dxf2.metadata.objectbundle.ObjectBundleParams;
import com.mass3d.importexport.ImportStrategy;
import com.mass3d.preheat.PreheatIdentifier;
import com.mass3d.preheat.PreheatMode;
import com.mass3d.scheduling.JobConfiguration;
import com.mass3d.schema.Schema;
import com.mass3d.system.util.ReflectionUtils;
import com.mass3d.user.User;

//import com.mass3d.dxf2.csv.CsvImportClass;

@JacksonXmlRootElement( localName = "metadataImportParams", namespace = DxfNamespaces.DXF_2_0 )
public class MetadataImportParams
{
    /**
     * User to use for import job (important for threaded imports).
     */
    private User user;

    /**
     * How should the user property be handled, by default it is left as is. You can override this
     * to use current user, or a selected user instead (not yet supported).
     */
    private UserOverrideMode userOverrideMode = UserOverrideMode.NONE;

    /**
     * User to use for override, can be current or a selected user.
     */
    private User overrideUser;

    /**
     * Should import be imported or just validated.
     */
    private ObjectBundleMode importMode = ObjectBundleMode.COMMIT;

    /**
     * What identifiers to match on.
     */
    private PreheatIdentifier identifier = PreheatIdentifier.UID;

    /**
     * Preheat mode to use (default is REFERENCE and should not be changed).
     */
    private PreheatMode preheatMode = PreheatMode.REFERENCE;

    /**
     * Sets import strategy (create, update, etc).
     */
    private ImportStrategy importStrategy = ImportStrategy.CREATE_AND_UPDATE;

    /**
     * Should import be treated as a atomic import (all or nothing).
     */
    private AtomicMode atomicMode = AtomicMode.ALL;

    /**
     * Merge mode for object updates (default is REPLACE).
     */
    private MergeMode mergeMode = MergeMode.REPLACE;

    /**
     * Flush for every object or per type.
     */
    private FlushMode flushMode = FlushMode.AUTO;

    /**
     * Decides how much to report back to the user (errors only, or a more full per object report).
     */
    private ImportReportMode importReportMode = ImportReportMode.ERRORS;

    /**
     * Should sharing be considered when importing objects.
     */
    private boolean skipSharing;

    /**
     * Should translation be considered when importing objects.
     */
    private boolean skipTranslation;

    /**
     * Skip validation of objects (not recommended).
     */
    private boolean skipValidation;

    /**
     * Name of file that was used for import (if available).
     */
    private String filename;

//    /**
//     * Metadata Class name for importing using CSV
//     */
//    private CsvImportClass csvImportClass;

    /**
     * Job id to use for threaded imports.
     */
    private JobConfiguration id;

    /**
     * Objects to import.
     */
    private Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> objects = new HashMap<>();

    public MetadataImportParams()
    {
    }

    public MetadataImportParams( List<? extends IdentifiableObject> objects )
    {
        addObjects( objects );
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUsername()
    {
        return user != null ? user.getUsername() : "system-process";
    }

    public User getUser()
    {
        return user;
    }

    public MetadataImportParams setUser( User user )
    {
        this.user = user;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public UserOverrideMode getUserOverrideMode()
    {
        return userOverrideMode;
    }

    public MetadataImportParams setUserOverrideMode( UserOverrideMode userOverrideMode )
    {
        this.userOverrideMode = userOverrideMode;
        return this;
    }

    public User getOverrideUser()
    {
        return overrideUser;
    }

    public void setOverrideUser( User overrideUser )
    {
        this.overrideUser = overrideUser;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ObjectBundleMode getImportMode()
    {
        return importMode;
    }

    public MetadataImportParams setImportMode( ObjectBundleMode importMode )
    {
        this.importMode = importMode;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public PreheatIdentifier getIdentifier()
    {
        return identifier;
    }

    public MetadataImportParams setIdentifier( PreheatIdentifier identifier )
    {
        this.identifier = identifier;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public PreheatMode getPreheatMode()
    {
        return preheatMode;
    }

    public MetadataImportParams setPreheatMode( PreheatMode preheatMode )
    {
        this.preheatMode = preheatMode;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ImportStrategy getImportStrategy()
    {
        return importStrategy;
    }

    public MetadataImportParams setImportStrategy( ImportStrategy importStrategy )
    {
        this.importStrategy = importStrategy;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public AtomicMode getAtomicMode()
    {
        return atomicMode;
    }

    public MetadataImportParams setAtomicMode( AtomicMode atomicMode )
    {
        this.atomicMode = atomicMode;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public MergeMode getMergeMode()
    {
        return mergeMode;
    }

    public MetadataImportParams setMergeMode( MergeMode mergeMode )
    {
        this.mergeMode = mergeMode;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public FlushMode getFlushMode()
    {
        return flushMode;
    }

    public MetadataImportParams setFlushMode( FlushMode flushMode )
    {
        this.flushMode = flushMode;
        return this;
    }

    public ImportReportMode getImportReportMode()
    {
        return importReportMode;
    }

    public MetadataImportParams setImportReportMode( ImportReportMode importReportMode )
    {
        this.importReportMode = importReportMode;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isSkipSharing()
    {
        return skipSharing;
    }

    public MetadataImportParams setSkipSharing( boolean skipSharing )
    {
        this.skipSharing = skipSharing;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isSkipTranslation()
    {
        return skipTranslation;
    }

    public MetadataImportParams setSkipTranslation( boolean skipTranslation )
    {
        this.skipTranslation = skipTranslation;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isSkipValidation()
    {
        return skipValidation;
    }

    public MetadataImportParams setSkipValidation( boolean skipValidation )
    {
        this.skipValidation = skipValidation;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getFilename()
    {
        return filename;
    }

    public MetadataImportParams setFilename( String filename )
    {
        this.filename = filename;
        return this;
    }

    // Todo Eagle commenting out getCsvImportClass
//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public CsvImportClass getCsvImportClass()
//    {
//        return this.csvImportClass;
//    }
//
//    public void setCsvImportClass( CsvImportClass csvImportClass )
//    {
//        this.csvImportClass = csvImportClass;
//    }

    public JobConfiguration getId()
    {
        return id;
    }

    public MetadataImportParams setId( JobConfiguration id )
    {
        this.id = id;
        return this;
    }

    public boolean hasJobId()
    {
        return id != null;
    }

    public Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> getObjects()
    {
        return objects;
    }

    public MetadataImportParams setObjects( Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> objects )
    {
        this.objects = objects;
        return this;
    }

    public List<Class<? extends IdentifiableObject>> getClasses()
    {
        return new ArrayList<>( objects.keySet() );
    }

    public List<? extends IdentifiableObject> getObjects( Class<? extends IdentifiableObject> klass )
    {
        return objects.get( klass );
    }

    public MetadataImportParams addObject( IdentifiableObject object )
    {
        if ( object == null )
        {
            return this;
        }

        Class<? extends IdentifiableObject> klass = object.getClass();

        if ( !objects.containsKey( klass ) )
        {
            objects.put( klass, new ArrayList<>() );
        }

        objects.get( klass ).add( klass.cast( object ) );

        return this;
    }

    public MetadataImportParams addObjects( List<? extends IdentifiableObject> objects )
    {
        objects.forEach( this::addObject );
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public MetadataImportParams addMetadata( List<Schema> schemas, Metadata metadata )
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> objectMap = new HashMap<>();

        for ( Schema schema : schemas )
        {
            Object value = ReflectionUtils.invokeGetterMethod( schema.getPlural(), metadata );

            if ( value != null )
            {
                if ( Collection.class.isAssignableFrom( value.getClass() ) && schema.isIdentifiableObject() )
                {
                    List<IdentifiableObject> objects = new ArrayList<>( (Collection<IdentifiableObject>) value );

                    if ( !objects.isEmpty() )
                    {
                        objectMap.put( (Class<? extends IdentifiableObject>) schema.getKlass(), objects );
                    }
                }
            }
        }

        setObjects( objectMap );
        return this;
    }

    public ObjectBundleParams toObjectBundleParams()
    {
        ObjectBundleParams params = new ObjectBundleParams();
        params.setUser( user );
        params.setUserOverrideMode( userOverrideMode );
        params.setOverrideUser( overrideUser );
        params.setSkipSharing( skipSharing );
        params.setSkipTranslation( skipTranslation );
        params.setSkipValidation( skipValidation );
        params.setJobId( id );
        params.setImportStrategy( importStrategy );
        params.setAtomicMode( atomicMode );
        params.setObjects( objects );
        params.setPreheatIdentifier( identifier );
        params.setPreheatMode( preheatMode );
        params.setObjectBundleMode( importMode );
        params.setMergeMode( mergeMode );
        params.setFlushMode( flushMode );
        params.setImportReportMode( importReportMode );

        return params;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "user", user )
            .add( "importMode", importMode )
            .add( "identifier", identifier )
            .add( "preheatMode", preheatMode )
            .add( "importStrategy", importStrategy )
            .add( "mergeMode", mergeMode )
            .toString();
    }
}
