package com.mass3d.setting;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.mass3d.cache.Cache;
import com.mass3d.cache.CacheProvider;
import com.mass3d.commons.util.SystemUtils;
import com.mass3d.system.util.SerializableOptional;
import com.mass3d.system.util.ValidationUtils;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

/**
 * Declare transactions on individual methods. The get-methods do not have
 * transactions declared, instead a programmatic transaction is initiated on
 * cache miss in order to reduce the number of transactions to improve performance.
 *
 */
@Slf4j
public class DefaultSystemSettingManager
    implements SystemSettingManager
{
    private static final Map<String, SettingKey> NAME_KEY_MAP = Lists.newArrayList(
        SettingKey.values() ).stream().collect( Collectors.toMap( SettingKey::getName, e -> e ) );

    /**
     * Cache for system settings. Does not accept nulls. Disabled during test phase.
     */
    private Cache<SerializableOptional> settingCache;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingStore systemSettingStore;

    private PBEStringEncryptor pbeStringEncryptor;

    private CacheProvider cacheProvider;

    private Environment environment;

    private List<String> flags;

    public DefaultSystemSettingManager( SystemSettingStore systemSettingStore,
        @Qualifier( "tripleDesStringEncryptor" ) PBEStringEncryptor pbeStringEncryptor, CacheProvider cacheProvider,
        Environment environment, List<String> flags )
    {
        checkNotNull( systemSettingStore );
        checkNotNull( pbeStringEncryptor );
        checkNotNull( cacheProvider );
        checkNotNull( environment );
        checkNotNull( flags );

        this.systemSettingStore = systemSettingStore;
        this.pbeStringEncryptor = pbeStringEncryptor;
        this.cacheProvider = cacheProvider;
        this.environment = environment;
        this.flags = flags;
    }

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------

    @PostConstruct
    public void init()
    {
        settingCache = cacheProvider.newCacheBuilder( SerializableOptional.class )
            .forRegion( "systemSetting" )
            .expireAfterWrite( 12, TimeUnit.HOURS )
            .withMaximumSize( SystemUtils.isTestRun( environment.getActiveProfiles() ) ? 0 : 400 ).build();
    }

    // -------------------------------------------------------------------------
    // SystemSettingManager implementation
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void saveSystemSetting( SettingKey key, Serializable value )
    {
        settingCache.invalidate( key.getName() );

        SystemSetting setting = systemSettingStore.getByName( key.getName() );

        if ( isConfidential( key.getName() ) )
        {
            value = pbeStringEncryptor.encrypt( value.toString() );
        }

        if ( setting == null )
        {
            setting = new SystemSetting();

            setting.setName( key.getName() );
            setting.setDisplayValue( value );

            systemSettingStore.save( setting );
        }
        else
        {
            setting.setDisplayValue( value );

            systemSettingStore.update( setting );
        }
    }

//    @Override
//    @Transactional
//    public void saveSystemSettingTranslation( SettingKey key, String locale, String translation )
//    {
//        SystemSetting setting = systemSettingStore.getByName( key.getName() );
//
//        if ( setting == null && !translation.isEmpty() )
//        {
//            throw new IllegalStateException( "No entry found for key: " + key );
//        }
//        else if ( setting != null )
//        {
//            if ( translation.isEmpty() )
//            {
//                setting.getTranslations().remove( locale );
//            }
//            else
//            {
//                setting.getTranslations().put( locale, translation );
//            }
//
//            settingCache.invalidate( key.getName() );
//            systemSettingStore.update( setting );
//        }
//    }

    @Override
    @Transactional
    public void deleteSystemSetting( SettingKey key )
    {
        SystemSetting setting = systemSettingStore.getByName( key.getName() );

        if ( setting != null )
        {
            settingCache.invalidate( key.getName() );

            systemSettingStore.delete( setting );
        }
    }

    /**
     * Note: No transaction for this method, transaction is instead initiated at the
     * store level behind the cache to avoid the transaction overhead for cache hits.
     */
    @Override
    public Serializable getSystemSetting( SettingKey key )
    {
        SerializableOptional value = settingCache.get( key.getName(),
            k -> getSystemSettingOptional( k, key.getDefaultValue() ) ).get();

        return value.get();
    }

    /**
     * Note: No transaction for this method, transaction is instead initiated at the
     * store level behind the cache to avoid the transaction overhead for cache hits.
     */
    @Override
    public Serializable getSystemSetting( SettingKey key, Serializable defaultValue )
    {
        SerializableOptional value = settingCache.get( key.getName(),
            k -> getSystemSettingOptional( k, defaultValue ) ).get();

        return value.get();
    }

    /**
     * Get system setting {@link SerializableOptional}. The return object is never
     * null in order to cache requests for system settings which have no value or default value.
     *
     * @param name the system setting name.
     * @param defaultValue the default value for the system setting.
     * @return an optional system setting value.
     */
    private SerializableOptional getSystemSettingOptional( String name, Serializable defaultValue )
    {
        SystemSetting setting = systemSettingStore.getByNameTx( name );

        if ( setting != null && setting.hasValue() )
        {
            if ( isConfidential( name ) )
            {
                try
                {
                    return SerializableOptional.of( pbeStringEncryptor.decrypt( (String) setting.getDisplayValue() ) );
                }
                catch ( EncryptionOperationNotPossibleException e ) // Most likely this means the value is not encrypted or not existing
                {
                    log.warn( "Could not decrypt system setting '" + name + "'" );
                    return SerializableOptional.empty();
                }
            }
            else
            {
                return SerializableOptional.of( setting.getDisplayValue() );
            }
        }
        else
        {
            return SerializableOptional.of( defaultValue );
        }
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Optional<String> getSystemSettingTranslation( SettingKey key, String locale )
//    {
//        SystemSetting setting = systemSettingStore.getByName( key.getName() );
//
//        if ( setting != null )
//        {
//            return setting.getTranslation( locale );
//        }
//
//        return Optional.empty();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSetting> getAllSystemSettings()
    {
        return systemSettingStore.getAll().stream().
            filter( systemSetting -> !isConfidential( systemSetting.getName() ) ).
            collect( Collectors.toList() );
    }

    @Override
    public Map<String, Serializable> getSystemSettingsAsMap()
    {
        final Map<String, Serializable> settingsMap = new HashMap<>();

        for ( SettingKey key : SettingKey.values() )
        {
            if ( key.hasDefaultValue() )
            {
                settingsMap.put( key.getName(), key.getDefaultValue() );
            }
        }

        Collection<SystemSetting> systemSettings = getAllSystemSettings();

        for ( SystemSetting systemSetting : systemSettings )
        {
            Serializable settingValue = systemSetting.getDisplayValue();

            if ( settingValue == null )
            {
                Optional<SettingKey> setting = SettingKey.getByName( systemSetting.getName() );

                if ( setting.isPresent() )
                {
                    settingValue = setting.get().getDefaultValue();
                }
            }

            settingsMap.put( systemSetting.getName(), settingValue );
        }

        return settingsMap;
    }

    @Override
    public Map<String, Serializable> getSystemSettings( Collection<SettingKey> keys )
    {
        Map<String, Serializable> map = new HashMap<>();

        for ( SettingKey setting : keys )
        {
            Serializable value = getSystemSetting( setting );

            if ( value != null )
            {
                map.put( setting.getName(), value );
            }
        }

        return map;
    }

    @Override
    public void invalidateCache()
    {
        settingCache.invalidateAll();
    }

    // -------------------------------------------------------------------------
    // Specific methods
    // -------------------------------------------------------------------------

    @Override
    public List<String> getFlags()
    {
        Collections.sort( flags );
        return flags;
    }

    @Override
    public String getFlagImage()
    {
        String flag = (String) getSystemSetting( SettingKey.FLAG );

        return flag != null ? flag + ".png" : null;
    }

    @Override
    public String getEmailHostName()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.EMAIL_HOST_NAME ) );
    }

    @Override
    public int getEmailPort()
    {
        return (Integer) getSystemSetting( SettingKey.EMAIL_PORT );
    }

    @Override
    public String getEmailUsername()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.EMAIL_USERNAME ) );
    }

    @Override
    public boolean getEmailTls()
    {
        return (Boolean) getSystemSetting( SettingKey.EMAIL_TLS );
    }

    @Override
    public String getEmailSender()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.EMAIL_SENDER ) );
    }

    @Override
    public String getInstanceBaseUrl() {
        return StringUtils.trimToNull((String) getSystemSetting(SettingKey.INSTANCE_BASE_URL));
    }

    @Override
    public boolean accountRecoveryEnabled()
    {
        return (Boolean) getSystemSetting( SettingKey.ACCOUNT_RECOVERY );
    }

    @Override
    public boolean selfRegistrationNoRecaptcha()
    {
        return (Boolean) getSystemSetting( SettingKey.SELF_REGISTRATION_NO_RECAPTCHA );
    }

    @Override
    public boolean emailConfigured()
    {
        return StringUtils.isNotBlank( getEmailHostName() )
            && StringUtils.isNotBlank( getEmailUsername() );
    }

    @Override
    public boolean systemNotificationEmailValid()
    {
        String address = (String) getSystemSetting( SettingKey.SYSTEM_NOTIFICATIONS_EMAIL );

        return address != null && ValidationUtils.emailIsValid( address );
    }

    @Override
    public boolean hideUnapprovedDataInAnalytics()
    {
        // -1 means approval is disabled
        return (int) getSystemSetting( SettingKey.IGNORE_ANALYTICS_APPROVAL_YEAR_THRESHOLD ) >= 0;
    }

    @Override
    public boolean isOpenIdConfigured()
    {
        return getSystemSetting( SettingKey.OPENID_PROVIDER ) != null &&
            getSystemSetting( SettingKey.OPENID_PROVIDER_LABEL ) != null;
    }

    @Override
    public String googleAnalyticsUA()
    {
        return StringUtils.trimToNull( (String) getSystemSetting( SettingKey.GOOGLE_ANALYTICS_UA ) );
    }

    @Override
    public Integer credentialsExpires()
    {
        return (Integer) getSystemSetting( SettingKey.CREDENTIALS_EXPIRES );
    }

    @Override
    public boolean isConfidential( String name )
    {
        return NAME_KEY_MAP.containsKey( name ) && NAME_KEY_MAP.get( name ).isConfidential();
    }

//    @Override
//    public boolean isTranslatable( final String name )
//    {
//        return NAME_KEY_MAP.containsKey( name ) && NAME_KEY_MAP.get( name ).isTranslatable();
//    }
}
