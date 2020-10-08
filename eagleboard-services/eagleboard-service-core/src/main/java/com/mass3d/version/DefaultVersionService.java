package com.mass3d.version;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service( "com.mass3d.version.VersionService" )
@Transactional
public class DefaultVersionService
    implements VersionService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private VersionStore versionStore;

    public void setVersionStore( VersionStore versionStore )
    {
        this.versionStore = versionStore;
    }

    // -------------------------------------------------------------------------
    // VersionService implementation
    // -------------------------------------------------------------------------

    @Override
    public int addVersion( Version version )
    {
        versionStore.save( version );
        return version.getId();
    }

    @Override
    public void updateVersion( Version version )
    {
        versionStore.update( version );
    }

    @Override
    public void updateVersion( String key )
    {
        updateVersion( key, UUID.randomUUID().toString() );
    }

    @Override
    public void updateVersion( String key, String value )
    {
        Version version = getVersionByKey( key );
        
        if ( version == null )
        {
            version = new Version( key, value );
            addVersion( version );
        }
        else
        {
            version.setValue( value );
            updateVersion( version );
        }
    }

    @Override
    public void deleteVersion( Version version )
    {
        versionStore.delete( version );
    }

    @Override
    public Version getVersion( Long id )
    {
        return versionStore.get( id );
    }

    @Override
    public Version getVersionByKey( String key )
    {
        return versionStore.getVersionByKey( key );
    }

    @Override
    public List<Version> getAllVersions()
    {
        return versionStore.getAll();
    }
}
