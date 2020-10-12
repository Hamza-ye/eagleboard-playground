package com.mass3d.system.log;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import com.mass3d.external.conf.ConfigurationKey;
import com.mass3d.external.conf.DhisConfigurationProvider;
import com.mass3d.external.location.LocationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component( "logInitializer" )
public class Log4JLogConfigInitializer
    implements LogConfigInitializer
{
    private static final PatternLayout PATTERN_LAYOUT = new PatternLayout( "* %-5p %d{ISO8601} %m (%F [%t])%n" );

    private static final String LOG_DIR = "logs";
    private static final String ANALYTICS_TABLE_LOGGER_FILENAME = "dhis-analytics-table.log";
    private static final String DATA_EXCHANGE_LOGGER_FILENAME = "dhis-data-exchange.log";
    private static final String DATA_SYNC_LOGGER_FILENAME = "dhis-data-sync.log";
    private static final String METADATA_SYNC_LOGGER_FILENAME = "dhis-metadata-sync.log";
    private static final String GENERAL_LOGGER_FILENAME = "dhis.log";
    private static final String PUSH_ANALYSIS_LOGGER_FILENAME = "dhis-push-analysis.log";
    private static final String LOG4J_CONF_PROP = "log4j.configuration";

    private static final Log log = LogFactory.getLog( Log4JLogConfigInitializer.class );

    @Autowired
    private LocationManager locationManager;

    @Autowired
    private DhisConfigurationProvider config;

//    public Log4JLogConfigInitializer( LocationManager locationManager, DhisConfigurationProvider config )
//    {
//        checkNotNull( locationManager );
//        checkNotNull( config );
//        this.locationManager = locationManager;
//        this.config = config;
//    }

    @PostConstruct
    @Override
    public void initConfig()
    {
        if ( !locationManager.externalDirectorySet() )
        {
            log.warn( "Could not initialize additional log configuration, external home directory not set" );
            return;
        }

        if ( isNotBlank( System.getProperty( LOG4J_CONF_PROP ) ) )
        {
            log.info( "Aborting default log config, external config set through system prop " + LOG4J_CONF_PROP + ": " + System.getProperty( LOG4J_CONF_PROP ) );
            return;
        }

        log.info( String.format( "Initializing Log4j, max file size: '%s', max file archives: %s",
            config.getProperty( ConfigurationKey.LOGGING_FILE_MAX_SIZE ),
            config.getProperty( ConfigurationKey.LOGGING_FILE_MAX_ARCHIVES ) ) );

        locationManager.buildDirectory( LOG_DIR );

//        configureLoggers( ANALYTICS_TABLE_LOGGER_FILENAME, Lists.newArrayList( "com.mass3d.resourcetable", "com.mass3d.analytics.table" ) );

        configureLoggers( DATA_EXCHANGE_LOGGER_FILENAME, Lists.newArrayList( "com.mass3d.dxf2" ) );

        configureLoggers( DATA_SYNC_LOGGER_FILENAME, Lists.newArrayList( "com.mass3d.dxf2.synch" ) );

        configureLoggers( METADATA_SYNC_LOGGER_FILENAME, Lists.newArrayList( "com.mass3d.dxf2.metadata" ) );

//        configureLoggers( PUSH_ANALYSIS_LOGGER_FILENAME, Lists.newArrayList( "com.mass3d.pushanalysis" ) );

        configureRootLogger( GENERAL_LOGGER_FILENAME );
    }

    /**
     * Configures rolling file loggers.
     *
     * @param filename the filename to output logging to.
     * @param loggers the logger names.
     */
    private void configureLoggers( String filename, List<String> loggers )
    {
        String file = getLogFile( filename );

        RollingFileAppender appender = getRollingFileAppender( file );

        for ( String loggerName : loggers )
        {
            Logger logger = Logger.getRootLogger().getLoggerRepository().getLogger( loggerName );

            logger.addAppender( appender );

            log.info( "Added logger: " + loggerName + " using file: " + file );
        }
    }

    /**
     * Configures a root file logger.
     *
     * @param filename the filename to output logging to.
     */
    private void configureRootLogger( String filename )
    {
        String file = getLogFile( filename );

        RollingFileAppender appender = getRollingFileAppender( file );

        Logger.getRootLogger().addAppender( appender );

        log.info( "Added root logger using file: " + file );
    }

    /**
     * Returns a rolling file appender.
     *
     * @param file the file to output to, including path and filename.
     */
    private RollingFileAppender getRollingFileAppender( String file )
    {
        RollingFileAppender appender = new RollingFileAppender();

        appender.setThreshold( Level.INFO );
        appender.setFile( file );
        appender.setMaxFileSize( config.getProperty( ConfigurationKey.LOGGING_FILE_MAX_SIZE ) );
        appender.setMaxBackupIndex( Integer.parseInt( config.getProperty( ConfigurationKey.LOGGING_FILE_MAX_ARCHIVES ) ) );
        appender.setLayout( PATTERN_LAYOUT );
        appender.activateOptions();

        return appender;
    }

    /**
     * Returns a file including path and filename.
     *
     * @param filename the filename to use for the file path.
     */
    private String getLogFile( String filename )
    {
        return locationManager.getExternalDirectoryPath() + File.separator + LOG_DIR + File.separator + filename;
    }
}
