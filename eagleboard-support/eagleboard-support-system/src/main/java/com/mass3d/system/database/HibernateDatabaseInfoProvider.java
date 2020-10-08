package com.mass3d.system.database;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.external.conf.ConfigurationKey;
import com.mass3d.external.conf.DhisConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component( "databaseInfoProvider" )
public class HibernateDatabaseInfoProvider
    implements DatabaseInfoProvider
{
    private static final String POSTGIS_MISSING_ERROR = "Postgis extension is not installed. Execute \"CREATE EXTENSION postgis;\" as a superuser and start the application again.";
    private static final Log log = LogFactory.getLog( HibernateDatabaseInfoProvider.class );
    private static final String DEL_A = "/";
    private static final String DEL_B = ":";
    private static final String DEL_C = "?";

    private DatabaseInfo info;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

//    @Autowired
    private DhisConfigurationProvider config;

//    @Autowired
    private JdbcTemplate jdbcTemplate;

    public HibernateDatabaseInfoProvider( DhisConfigurationProvider config, JdbcTemplate jdbcTemplate)
    {
        checkNotNull( config );
        checkNotNull( jdbcTemplate );
//        checkNotNull( environment );

        this.config = config;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init()
    {
        checkDatabaseConnectivity();

        boolean spatialSupport = isSpatialSupport();

        // Todo disable postGIS exception
        // Check if postgis is installed. if not, fail startup
//        if ( !spatialSupport && !SystemUtils.isTestRun() )
//        {
//            log.error( POSTGIS_MISSING_ERROR );
//            throw new IllegalStateException( POSTGIS_MISSING_ERROR );
//        }

        String url = config.getProperty( ConfigurationKey.CONNECTION_URL );
        String user = config.getProperty( ConfigurationKey.CONNECTION_USERNAME );
        String password = config.getProperty( ConfigurationKey.CONNECTION_PASSWORD );

        info = new DatabaseInfo();
        info.setName( getNameFromConnectionUrl( url ) );
        info.setUser( user );
        info.setPassword( password );
        info.setUrl( url );
//        info.setSpatialSupport( spatialSupport );
    }

    // -------------------------------------------------------------------------
    // DatabaseInfoProvider implementation
    // -------------------------------------------------------------------------

    @Override
    public DatabaseInfo getDatabaseInfo()
    {
        return info;
    }

    @Override
    public boolean isInMemory()
    {
        return info.getUrl() != null && info.getUrl().contains( ":mem:" );
    }

    @Override
    public String getNameFromConnectionUrl( String url )
    {
        String name = null;

        if ( url != null && url.lastIndexOf( DEL_B ) != -1 )
        {
            int startPos = url.lastIndexOf( DEL_A ) != -1 ? url.lastIndexOf( DEL_A ) : url.lastIndexOf( DEL_B );
            int endPos = url.lastIndexOf( DEL_C ) != -1 ? url.lastIndexOf( DEL_C ) : url.length();
            name = url.substring( startPos + 1, endPos );
        }

        return name;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Attempts to create a spatial database extension. Checks if spatial operations
     * are supported.
     */
    private boolean isSpatialSupport()
    {
        try
        {
            jdbcTemplate.execute( "create extension postgis;" );
        }
        catch ( Exception ex )
        {
        }

        try
        {
            String version = jdbcTemplate.queryForObject( "select postgis_full_version();", String.class );

            return version != null;
        }
        catch ( Exception ex )
        {
            log.error( "Exception when checking postgis_full_version(), PostGIS not available" );
            log.debug( "Exception when checking postgis_full_version()", ex );

            return false;
        }
    }

    private void checkDatabaseConnectivity()
    {
        jdbcTemplate.queryForObject( "select 'checking db connection';", String.class );
    }
}
