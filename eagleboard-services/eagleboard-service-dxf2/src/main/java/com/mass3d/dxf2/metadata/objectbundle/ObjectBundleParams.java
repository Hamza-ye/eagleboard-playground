package com.mass3d.dxf2.metadata.objectbundle;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.MergeMode;
import com.mass3d.dxf2.metadata.AtomicMode;
import com.mass3d.dxf2.metadata.FlushMode;
import com.mass3d.dxf2.metadata.UserOverrideMode;
import com.mass3d.dxf2.metadata.feedback.ImportReportMode;
import com.mass3d.importexport.ImportStrategy;
import com.mass3d.preheat.PreheatIdentifier;
import com.mass3d.preheat.PreheatMode;
import com.mass3d.preheat.PreheatParams;
import com.mass3d.scheduling.JobConfiguration;
import com.mass3d.user.User;

public class ObjectBundleParams
{
    private User user;

    private UserOverrideMode userOverrideMode = UserOverrideMode.NONE;

    private User overrideUser;

    private ObjectBundleMode objectBundleMode = ObjectBundleMode.COMMIT;

    private PreheatIdentifier preheatIdentifier = PreheatIdentifier.UID;

    private PreheatMode preheatMode = PreheatMode.REFERENCE;

    private ImportStrategy importStrategy = ImportStrategy.CREATE_AND_UPDATE;

    private AtomicMode atomicMode = AtomicMode.ALL;

    private MergeMode mergeMode = MergeMode.REPLACE;

    private FlushMode flushMode = FlushMode.AUTO;

    private ImportReportMode importReportMode = ImportReportMode.ERRORS;

    private Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> objects = new HashMap<>();

    private boolean skipSharing;

    private boolean skipTranslation;

    private boolean skipValidation;

    private JobConfiguration jobId;

    public ObjectBundleParams()
    {

    }

    public User getUser()
    {
        return user;
    }

    public ObjectBundleParams setUser( User user )
    {
        this.user = user;
        return this;
    }

    public UserOverrideMode getUserOverrideMode()
    {
        return userOverrideMode;
    }

    public ObjectBundleParams setUserOverrideMode( UserOverrideMode userOverrideMode )
    {
        this.userOverrideMode = userOverrideMode;
        return this;
    }

    public User getOverrideUser()
    {
        return overrideUser;
    }

    public ObjectBundleParams setOverrideUser( User overrideUser )
    {
        this.overrideUser = overrideUser;
        return this;
    }

    public ObjectBundleMode getObjectBundleMode()
    {
        return objectBundleMode;
    }

    public ObjectBundleParams setObjectBundleMode( ObjectBundleMode objectBundleMode )
    {
        this.objectBundleMode = objectBundleMode;
        return this;
    }

    public PreheatIdentifier getPreheatIdentifier()
    {
        return preheatIdentifier;
    }

    public ObjectBundleParams setPreheatIdentifier( PreheatIdentifier preheatIdentifier )
    {
        this.preheatIdentifier = preheatIdentifier;
        return this;
    }

    public PreheatMode getPreheatMode()
    {
        return preheatMode;
    }

    public ObjectBundleParams setPreheatMode( PreheatMode preheatMode )
    {
        this.preheatMode = preheatMode;
        return this;
    }

    public ImportStrategy getImportStrategy()
    {
        return importStrategy;
    }

    public ObjectBundleParams setImportStrategy( ImportStrategy importStrategy )
    {
        this.importStrategy = importStrategy;
        return this;
    }

    public AtomicMode getAtomicMode()
    {
        return atomicMode;
    }

    public ObjectBundleParams setAtomicMode( AtomicMode atomicMode )
    {
        this.atomicMode = atomicMode;
        return this;
    }

    public MergeMode getMergeMode()
    {
        return mergeMode;
    }

    public ObjectBundleParams setMergeMode( MergeMode mergeMode )
    {
        this.mergeMode = mergeMode;
        return this;
    }

    public FlushMode getFlushMode()
    {
        return flushMode;
    }

    public ObjectBundleParams setFlushMode( FlushMode flushMode )
    {
        this.flushMode = flushMode;
        return this;
    }

    public ImportReportMode getImportReportMode()
    {
        return importReportMode;
    }

    public ObjectBundleParams setImportReportMode( ImportReportMode importReportMode )
    {
        this.importReportMode = importReportMode;
        return this;
    }

    public boolean isSkipSharing()
    {
        return skipSharing;
    }

    public ObjectBundleParams setSkipSharing( boolean skipSharing )
    {
        this.skipSharing = skipSharing;
        return this;
    }

    public boolean isSkipTranslation()
    {
        return skipTranslation;
    }

    public ObjectBundleParams setSkipTranslation( boolean skipTranslation )
    {
        this.skipTranslation = skipTranslation;
        return this;
    }

    public boolean isSkipValidation()
    {
        return skipValidation;
    }

    public ObjectBundleParams setSkipValidation( boolean skipValidation )
    {
        this.skipValidation = skipValidation;
        return this;
    }

    public JobConfiguration getJobId()
    {
        return jobId;
    }

    public ObjectBundleParams setJobId( JobConfiguration jobId )
    {
        this.jobId = jobId;
        return this;
    }

    public boolean haveJobId()
    {
        return jobId != null;
    }

    public Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> getObjects()
    {
        return objects;
    }

    public ObjectBundleParams setObjects( Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> objects )
    {
        this.objects = objects;
        return this;
    }

    public ObjectBundleParams addObject( Class<? extends IdentifiableObject> klass, IdentifiableObject object )
    {
        if ( object == null )
        {
            return this;
        }

        if ( !objects.containsKey( klass ) )
        {
            objects.put( klass, new ArrayList<>() );
        }

        objects.get( klass ).add( object );

        return this;
    }

    public ObjectBundleParams addObject( IdentifiableObject object )
    {
        if ( object == null )
        {
            return this;
        }

        if ( !objects.containsKey( object.getClass() ) )
        {
            objects.put( object.getClass(), new ArrayList<>() );
        }

        objects.get( object.getClass() ).add( object );

        return this;
    }

    public PreheatParams getPreheatParams()
    {
        PreheatParams params = new PreheatParams();
        params.setPreheatIdentifier( preheatIdentifier );
        params.setPreheatMode( preheatMode );

        return params;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "user", user )
            .add( "objectBundleMode", objectBundleMode )
            .add( "preheatIdentifier", preheatIdentifier )
            .add( "preheatMode", preheatMode )
            .add( "importStrategy", importStrategy )
            .add( "mergeMode", mergeMode )
            .toString();
    }
}
