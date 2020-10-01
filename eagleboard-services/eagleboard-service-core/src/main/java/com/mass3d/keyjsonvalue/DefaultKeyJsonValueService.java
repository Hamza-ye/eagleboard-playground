package com.mass3d.keyjsonvalue;

import java.util.Date;
import java.util.List;
import com.mass3d.system.util.JacksonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.keyjsonvalue.KeyJsonValueService")
@Transactional
public class DefaultKeyJsonValueService
    implements KeyJsonValueService
{
    private KeyJsonValueStore keyJsonValueStore;

    public void setKeyJsonValueStore( KeyJsonValueStore keyJsonValueStore )
    {
        this.keyJsonValueStore = keyJsonValueStore;
    }

    // -------------------------------------------------------------------------
    // KeyJsonValueService implementation
    // -------------------------------------------------------------------------

    @Override
    public List<String> getNamespaces()
    {
        return keyJsonValueStore.getNamespaces();
    }

    @Override
    public List<String> getKeysInNamespace( String namespace )
    {
        return keyJsonValueStore.getKeysInNamespace( namespace );
    }

    @Override
    public List<String> getKeysInNamespace( String namespace, Date lastUpdated )
    {
        return keyJsonValueStore.getKeysInNamespace( namespace, lastUpdated );
    }

    @Override
    public void deleteNamespace( String namespace )
    {
        keyJsonValueStore.getKeyJsonValueByNamespace( namespace ).forEach( keyJsonValueStore::delete );
    }

    @Override
    public KeyJsonValue getKeyJsonValue( String namespace, String key )
    {
        return keyJsonValueStore.getKeyJsonValue( namespace, key );
    }
    
    @Override
    public List<KeyJsonValue> getKeyJsonValuesInNamespace( String namespace )
    {
        return keyJsonValueStore.getKeyJsonValueByNamespace( namespace );
    }
   
    @Override
    public Long addKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        keyJsonValueStore.save( keyJsonValue );

        return keyJsonValue.getId();
    }

    @Override
    public void updateKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        keyJsonValueStore.update( keyJsonValue );
    }

    @Override
    public void deleteKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        keyJsonValueStore.delete( keyJsonValue );
    }

    @Override
    public <T> T getValue( String namespace, String key, Class<T> clazz )
    {
        KeyJsonValue value = getKeyJsonValue( namespace, key );

        if ( value == null || value.getJbPlainValue() == null )
        {
            return null;
        }
        
        return JacksonUtils.fromJson( value.getJbPlainValue(), clazz );
    }

    @Override
    public <T> void addValue( String namespace, String key, T object )
    {
        String value = JacksonUtils.toJson( object );
        
        KeyJsonValue keyJsonValue = new KeyJsonValue( namespace, key, value, false );
        
        keyJsonValueStore.save( keyJsonValue );
    }

    @Override
    public <T> void updateValue( String namespace, String key, T object )
    {
        KeyJsonValue keyJsonValue = getKeyJsonValue( namespace, key );
        
        if ( keyJsonValue == null )
        {
            throw new IllegalStateException( String.format( 
                "No object found for namespace '%s' and key '%s'", namespace, key ) );
        }

        String value = JacksonUtils.toJson( object );
        
        keyJsonValue.setValue( value );
        
        keyJsonValueStore.update( keyJsonValue );
    }
}
