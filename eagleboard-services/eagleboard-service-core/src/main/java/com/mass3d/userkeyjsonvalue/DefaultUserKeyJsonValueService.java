package com.mass3d.userkeyjsonvalue;

import java.util.List;
import com.mass3d.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service( "com.mass3d.userkeyjsonvalue.UserKeyJsonValueService" )
@Transactional
public class DefaultUserKeyJsonValueService
    implements UserKeyJsonValueService
{
    private UserKeyJsonValueStore userKeyJsonValueStore;

    public void setUserKeyJsonValueStore( UserKeyJsonValueStore userKeyJsonValueStore )
    {
        this.userKeyJsonValueStore = userKeyJsonValueStore;
    }

    public UserKeyJsonValueStore getUserKeyJsonValueStore()
    {
        return this.userKeyJsonValueStore;
    }

    @Override
    public UserKeyJsonValue getUserKeyJsonValue( User user, String namespace, String key )
    {
        return userKeyJsonValueStore.getUserKeyJsonValue( user, namespace, key );
    }

    @Override
    public Long addUserKeyJsonValue( UserKeyJsonValue userKeyJsonValue )
    {
        userKeyJsonValueStore.save( userKeyJsonValue );
        return userKeyJsonValue.getId();
    }

    @Override
    public void updateUserKeyJsonValue( UserKeyJsonValue userKeyJsonValue )
    {
        userKeyJsonValueStore.update( userKeyJsonValue );
    }

    @Override
    public void deleteUserKeyJsonValue( UserKeyJsonValue userKeyJsonValue )
    {
        userKeyJsonValueStore.delete( userKeyJsonValue );
    }

    @Override
    public List<String> getNamespacesByUser( User user )
    {
        return userKeyJsonValueStore.getNamespacesByUser( user );
    }

    @Override
    public List<String> getKeysByUserAndNamespace( User user, String namespace )
    {
        return userKeyJsonValueStore.getKeysByUserAndNamespace( user, namespace );
    }

    @Override
    public void deleteNamespaceFromUser( User user, String namespace )
    {
        userKeyJsonValueStore.getUserKeyJsonValueByUserAndNamespace( user, namespace ).forEach(
            userKeyJsonValueStore::delete );
    }
}
