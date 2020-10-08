package com.mass3d.startup;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.configuration.Configuration;
import com.mass3d.configuration.ConfigurationService;
import com.mass3d.encryption.EncryptionStatus;
import com.mass3d.external.conf.DhisConfigurationProvider;
import com.mass3d.system.startup.TransactionContextStartupRoutine;

public class ConfigurationPopulator
    extends TransactionContextStartupRoutine
{
//    @Autowired
    private ConfigurationService configurationService;

//    @Autowired
    private DhisConfigurationProvider dhisConfigurationProvider;

    private static final Log log = LogFactory.getLog( ConfigurationPopulator.class );

    public ConfigurationPopulator( ConfigurationService configurationService,
        DhisConfigurationProvider dhisConfigurationProvider )
    {
        checkNotNull( configurationService );
        checkNotNull( dhisConfigurationProvider );

        this.configurationService = configurationService;
        this.dhisConfigurationProvider = dhisConfigurationProvider;
    }

    @Override
    public void executeInTransaction()
    {
        checkSecurityConfiguration();

        Configuration config = configurationService.getConfiguration();

        if ( config != null && config.getSystemId() == null )
        {
            config.setSystemId( UUID.randomUUID().toString() );
            configurationService.setConfiguration( config );
        }
    }

    private void checkSecurityConfiguration()
    {
        EncryptionStatus status = dhisConfigurationProvider.getEncryptionStatus();

        if ( !status.isOk() )
        {
            log.warn( "Encryption not configured: " + status.getKey() );
        }
        else
        {
            log.info( "Encryption is available" );
        }
    }
}
