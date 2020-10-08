package com.mass3d.datasource;

import static com.mass3d.external.conf.ConfigurationKey.CONNECTION_DRIVER_CLASS;
import static com.mass3d.external.conf.ConfigurationKey.CONNECTION_PASSWORD;
import static com.mass3d.external.conf.ConfigurationKey.CONNECTION_POOL_MAX_SIZE;
import static com.mass3d.external.conf.ConfigurationKey.CONNECTION_URL;
import static com.mass3d.external.conf.ConfigurationKey.CONNECTION_USERNAME;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.external.conf.ConfigurationKey;
import com.mass3d.external.conf.DhisConfigurationProvider;
import com.mass3d.util.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;

public class DefaultDataSourceManager
    implements DataSourceManager, InitializingBean
{
    private static final Log log = LogFactory.getLog( DefaultDataSourceManager.class );
    
    private static final String FORMAT_READ_PREFIX = "read%d.";    
    private static final String FORMAT_CONNECTION_URL = FORMAT_READ_PREFIX + CONNECTION_URL.getKey();
    private static final String FORMAT_CONNECTION_USERNAME = FORMAT_READ_PREFIX + CONNECTION_USERNAME.getKey();
    private static final String FORMAT_CONNECTION_PASSWORD = FORMAT_READ_PREFIX + CONNECTION_PASSWORD.getKey();
    
    private static final int VAL_ACQUIRE_INCREMENT = 6;
    private static final int VAL_MAX_IDLE_TIME = 21600;    
    private static final int MAX_READ_REPLICAS = 5;    
    private static final String DEFAULT_POOL_SIZE = "40";    

    /**
     * State holder for the resolved read only data source.
     */
    private DataSource internalReadOnlyDataSource;
    
    /**
     * State holder for explicitly defined read only data sources.
     */
    private List<DataSource> internalReadOnlyInstanceList;

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        List<DataSource> ds = getReadOnlyDataSources();
        
        this.internalReadOnlyInstanceList = ds;        
        this.internalReadOnlyDataSource = !ds.isEmpty() ? new CircularRoutingDataSource( ds ) : mainDataSource;
    }
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DhisConfigurationProvider config;
    
    public void setConfig( DhisConfigurationProvider config )
    {
        this.config = config;
    }

    private DataSource mainDataSource;

    public void setMainDataSource( DataSource mainDataSource )
    {
        this.mainDataSource = mainDataSource;
    }

    // -------------------------------------------------------------------------
    // DataSourceManager implementation
    // -------------------------------------------------------------------------

    @Override
    public DataSource getReadOnlyDataSource()
    {
        return internalReadOnlyDataSource;
    }
    
    public int getReadReplicaCount()
    {
        return internalReadOnlyInstanceList != null ? internalReadOnlyInstanceList.size() : 0;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private List<DataSource> getReadOnlyDataSources()
    {
        String mainUser = config.getProperty( ConfigurationKey.CONNECTION_USERNAME );
        String mainPassword = config.getProperty( ConfigurationKey.CONNECTION_PASSWORD );
        String driverClass = config.getProperty( CONNECTION_DRIVER_CLASS );
        String maxPoolSize = config.getPropertyOrDefault( CONNECTION_POOL_MAX_SIZE, DEFAULT_POOL_SIZE );
        
        Properties props = config.getProperties();
                
        List<DataSource> dataSources = new ArrayList<>();
        
        for ( int i = 1; i <= MAX_READ_REPLICAS; i++ )
        {
            String jdbcUrl = props.getProperty( String.format( FORMAT_CONNECTION_URL, i ) );
            String user = props.getProperty( String.format( FORMAT_CONNECTION_USERNAME, i ) );
            String password = props.getProperty( String.format( FORMAT_CONNECTION_PASSWORD, i ) );
            
            user = StringUtils.defaultIfEmpty( user, mainUser );
            password = StringUtils.defaultIfEmpty( password, mainPassword );
            
            if ( ObjectUtils.allNonNull( jdbcUrl, user, password ) )
            {
                try
                {
                    ComboPooledDataSource ds = new ComboPooledDataSource();
                    
                    ds.setDriverClass( driverClass );          
                    ds.setJdbcUrl( jdbcUrl );
                    ds.setUser( user );
                    ds.setPassword( password );
                    ds.setMaxPoolSize( Integer.valueOf( maxPoolSize ) );
                    ds.setAcquireIncrement( VAL_ACQUIRE_INCREMENT );
                    ds.setMaxIdleTime( VAL_MAX_IDLE_TIME );      
                    
                    dataSources.add( ds );
                    
                    log.info( "Found read replica, connection URL: " + jdbcUrl );
                }
                catch ( PropertyVetoException ex )
                {
                    throw new IllegalArgumentException( "Invalid configuration of read replica: " + jdbcUrl, ex );
                }
            }
        }

        log.info( "Read only configuration initialized, read replicas found: " + dataSources.size() );
        
        return dataSources;
    }
}
