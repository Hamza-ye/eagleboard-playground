package com.mass3d.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Date;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.logging.LoggingConfig;
import com.mass3d.system.database.DatabaseInfo;
import org.springframework.beans.BeanUtils;

@JacksonXmlRootElement( localName = "systemInfo", namespace = DxfNamespaces.DXF_2_0 )
public class SystemInfo
{
    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    private String contextPath;

    private String userAgent;

    // -------------------------------------------------------------------------
    // Volatile properties
    // -------------------------------------------------------------------------

    private String calendar;

    private String dateFormat;

    private Date serverDate;

    private Date lastAnalyticsTableSuccess;

    private String intervalSinceLastAnalyticsTableSuccess;

    private String lastAnalyticsTableRuntime;

    private Date lastSystemMonitoringSuccess;

    // -------------------------------------------------------------------------
    // Stable properties
    // -------------------------------------------------------------------------

    private String version;

    private String revision;

    private Date buildTime;

    private String jasperReportsVersion;

    private String environmentVariable;

    private String fileStoreProvider;

    private String cacheProvider;

    private String readOnlyMode;

    private String nodeId;

    private String javaVersion;

    private String javaVendor;

    private String javaOpts;

    private String osName;

    private String osArchitecture;

    private String osVersion;

    private String externalDirectory;

    private DatabaseInfo databaseInfo;

    private Integer readReplicaCount;

    private String memoryInfo;

    private Integer cpuCores;

    private boolean encryption;

    private boolean emailConfigured;
    
    private boolean redisEnabled;
    
    private String redisHostname;

    private String systemId;

    private String systemName;

    private String systemMetadataVersion;

    private String instanceBaseUrl;

    private String systemMonitoringUrl;

    private Boolean isMetadataVersionEnabled;

    private Date lastMetadataVersionSyncAttempt;

    private boolean isMetadataSyncEnabled;

    private MetadataAudit metadataAudit;

    private RabbitMQ rabbitMQ;

//    private KafkaConfig kafka;

    private LoggingConfig logging;

    public SystemInfo instance()
    {
        SystemInfo info = new SystemInfo();
        BeanUtils.copyProperties( this, info );
        return info;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void clearSensitiveInfo()
    {
        this.fileStoreProvider = null;
        this.readOnlyMode = null;
        this.nodeId = null;
        this.javaVersion = null;
        this.javaVendor = null;
        this.javaOpts = null;
        this.osName = null;
        this.osArchitecture = null;
        this.osVersion = null;
        this.externalDirectory = null;
        this.readReplicaCount = null;
        this.memoryInfo = null;
        this.cpuCores = null;
        this.systemMonitoringUrl = null;
        this.metadataAudit = null;

        if ( this.databaseInfo != null )
        {
            this.databaseInfo.clearSensitiveInfo();
        }
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath( String contextPath )
    {
        this.contextPath = contextPath;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent( String userAgent )
    {
        this.userAgent = userAgent;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getCalendar()
    {
        return calendar;
    }

    public void setCalendar( String calendar )
    {
        this.calendar = calendar;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDateFormat()
    {
        return dateFormat;
    }

    public void setDateFormat( String dateFormat )
    {
        this.dateFormat = dateFormat;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getServerDate()
    {
        return serverDate;
    }

    public void setServerDate( Date serverDate )
    {
        this.serverDate = serverDate;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getLastAnalyticsTableSuccess()
    {
        return lastAnalyticsTableSuccess;
    }

    public void setLastAnalyticsTableSuccess( Date lastAnalyticsTableSuccess )
    {
        this.lastAnalyticsTableSuccess = lastAnalyticsTableSuccess;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getIntervalSinceLastAnalyticsTableSuccess()
    {
        return intervalSinceLastAnalyticsTableSuccess;
    }

    public void setIntervalSinceLastAnalyticsTableSuccess( String intervalSinceLastAnalyticsTableSuccess )
    {
        this.intervalSinceLastAnalyticsTableSuccess = intervalSinceLastAnalyticsTableSuccess;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getLastAnalyticsTableRuntime()
    {
        return lastAnalyticsTableRuntime;
    }

    public void setLastAnalyticsTableRuntime( String lastAnalyticsTableRuntime )
    {
        this.lastAnalyticsTableRuntime = lastAnalyticsTableRuntime;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getLastSystemMonitoringSuccess()
    {
        return lastSystemMonitoringSuccess;
    }

    public void setLastSystemMonitoringSuccess( Date lastSystemMonitoringSuccess )
    {
        this.lastSystemMonitoringSuccess = lastSystemMonitoringSuccess;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getRevision()
    {
        return revision;
    }

    public void setRevision( String revision )
    {
        this.revision = revision;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getBuildTime()
    {
        return buildTime;
    }

    public void setBuildTime( Date buildTime )
    {
        this.buildTime = buildTime;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getJasperReportsVersion()
    {
        return jasperReportsVersion;
    }

    public void setJasperReportsVersion( String jasperReportsVersion )
    {
        this.jasperReportsVersion = jasperReportsVersion;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getEnvironmentVariable()
    {
        return environmentVariable;
    }

    public void setEnvironmentVariable( String environmentVariable )
    {
        this.environmentVariable = environmentVariable;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getFileStoreProvider()
    {
        return fileStoreProvider;
    }

    public void setFileStoreProvider( String fileStoreProvider )
    {
        this.fileStoreProvider = fileStoreProvider;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getCacheProvider()
    {
        return cacheProvider;
    }

    public void setCacheProvider( String cacheProvider )
    {
        this.cacheProvider = cacheProvider;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getReadOnlyMode()
    {
        return readOnlyMode;
    }

    public void setReadOnlyMode( String readOnlyMode )
    {
        this.readOnlyMode = readOnlyMode;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId( String nodeId )
    {
        this.nodeId = nodeId;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getJavaVersion()
    {
        return javaVersion;
    }

    public void setJavaVersion( String javaVersion )
    {
        this.javaVersion = javaVersion;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getJavaVendor()
    {
        return javaVendor;
    }

    public void setJavaVendor( String javaVendor )
    {
        this.javaVendor = javaVendor;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getJavaOpts()
    {
        return javaOpts;
    }

    public void setJavaOpts( String javaOpts )
    {
        this.javaOpts = javaOpts;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getOsName()
    {
        return osName;
    }

    public void setOsName( String osName )
    {
        this.osName = osName;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getOsArchitecture()
    {
        return osArchitecture;
    }

    public void setOsArchitecture( String osArchitecture )
    {
        this.osArchitecture = osArchitecture;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getOsVersion()
    {
        return osVersion;
    }

    public void setOsVersion( String osVersion )
    {
        this.osVersion = osVersion;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getExternalDirectory()
    {
        return externalDirectory;
    }

    public void setExternalDirectory( String externalDirectory )
    {
        this.externalDirectory = externalDirectory;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DatabaseInfo getDatabaseInfo()
    {
        return databaseInfo;
    }

    public void setDatabaseInfo( DatabaseInfo databaseInfo )
    {
        this.databaseInfo = databaseInfo;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getReadReplicaCount()
    {
        return readReplicaCount;
    }

    public void setReadReplicaCount( Integer readReplicaCount )
    {
        this.readReplicaCount = readReplicaCount;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getMemoryInfo()
    {
        return memoryInfo;
    }

    public void setMemoryInfo( String memoryInfo )
    {
        this.memoryInfo = memoryInfo;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getCpuCores()
    {
        return cpuCores;
    }

    public void setCpuCores( Integer cpuCores )
    {
        this.cpuCores = cpuCores;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isEncryption()
    {
        return encryption;
    }

    public void setEncryption( boolean encryption )
    {
        this.encryption = encryption;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isEmailConfigured()
    {
        return emailConfigured;
    }

    public void setEmailConfigured( boolean emailConfigured )
    {
        this.emailConfigured = emailConfigured;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isRedisEnabled()
    {
        return redisEnabled;
    }

    public void setRedisEnabled( boolean redisEnabled )
    {
        this.redisEnabled = redisEnabled;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getRedisHostname()
    {
        return redisHostname;
    }

    public void setRedisHostname( String redisHostname )
    {
        this.redisHostname = redisHostname;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId( String systemId )
    {
        this.systemId = systemId;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSystemName()
    {
        return systemName;
    }

    public void setSystemName( String systemName )
    {
        this.systemName = systemName;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSystemMetadataVersion()
    {
        return systemMetadataVersion;
    }

    public void setSystemMetadataVersion( String systemMetadataVersion )
    {
        this.systemMetadataVersion = systemMetadataVersion;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getInstanceBaseUrl()
    {
        return instanceBaseUrl;
    }

    public void setInstanceBaseUrl( String instanceBaseUrl )
    {
        this.instanceBaseUrl = instanceBaseUrl;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSystemMonitoringUrl()
    {
        return systemMonitoringUrl;
    }

    public void setSystemMonitoringUrl( String systemMonitoringUrl )
    {
        this.systemMonitoringUrl = systemMonitoringUrl;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getIsMetadataVersionEnabled()
    {
        return isMetadataVersionEnabled;
    }

    public void setIsMetadataVersionEnabled( Boolean isMetadataVersionEnabled )
    {
        this.isMetadataVersionEnabled = isMetadataVersionEnabled;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getLastMetadataVersionSyncAttempt()
    {
        return lastMetadataVersionSyncAttempt;
    }

    public void setLastMetadataVersionSyncAttempt( Date lastMetadataVersionSyncAttempt )
    {
        this.lastMetadataVersionSyncAttempt = lastMetadataVersionSyncAttempt;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isMetadataSyncEnabled()
    {
        return isMetadataSyncEnabled;
    }

    public void setMetadataSyncEnabled( boolean isMetadataSyncEnabled )
    {
        this.isMetadataSyncEnabled = isMetadataSyncEnabled;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public MetadataAudit getMetadataAudit()
    {
        return metadataAudit;
    }

    public void setMetadataAudit( MetadataAudit metadataAudit )
    {
        this.metadataAudit = metadataAudit;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public RabbitMQ getRabbitMQ()
    {
        return rabbitMQ;
    }

    public void setRabbitMQ( RabbitMQ rabbitMQ )
    {
        this.rabbitMQ = rabbitMQ;
    }

//    @JsonProperty
//    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
//    public KafkaConfig getKafka()
//    {
//        return kafka;
//    }
//
//    public void setKafka( KafkaConfig kafka )
//    {
//        this.kafka = kafka;
//    }
//
    public boolean isKafka()
    {
        return false;
//        return kafka != null && kafka.isValid();
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LoggingConfig getLogging()
    {
        return logging;
    }

    public void setLogging( LoggingConfig loggingConfig )
    {
        this.logging = loggingConfig;
    }
}
