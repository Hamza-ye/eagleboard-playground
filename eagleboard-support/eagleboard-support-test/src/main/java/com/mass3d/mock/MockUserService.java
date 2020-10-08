package com.mass3d.mock;

import com.mass3d.feedback.ErrorReport;
import com.mass3d.dataset.DataSet;
import com.mass3d.todotask.TodoTask;
import com.mass3d.user.User;
import com.mass3d.user.UserAuthorityGroup;
import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserQueryParams;
import com.mass3d.user.UserService;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;

public class MockUserService implements UserService
{

    private List<User> users;

    public MockUserService( List<User> users )
    {
        this.users = users;
    }

    @Override
    public Long addUser( User user )
    {
        this.users.add( user );
        return user.getId();
    }

    @Override
    public void updateUser( User user )
    {
    }

    @Override
    public User getUser( Long id )
    {
        return null;
    }

    @Override
    public User getUser( String uid )
    {
        return null;
    }

    @Override
    public List<User> getUsers( Collection<String> uid )
    {
        return this.users;
    }

    @Override
    public List<User> getAllUsers()
    {
        return null;
    }

    @Override
    public List<User> getAllUsersBetweenByName( String name, int first, int max )
    {
        return null;
    }

    @Override
    public void deleteUser( User user )
    {
    }

    @Override
    public boolean isLastSuperUser( UserCredentials userCredentials )
    {
        return false;
    }

    @Override
    public boolean isLastSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        return false;
    }

    @Override
    public List<User> getUsers( UserQueryParams params )
    {
        return null;
    }

    @Override
    public List<User> getUsers( UserQueryParams params, @Nullable List<String> orders )
    {
        return null;
    }

    @Override
    public int getUserCount( UserQueryParams params )
    {
        return 0;
    }

    @Override
    public int getUserCount()
    {
        return 0;
    }

    @Override
    public List<User> getUsersByPhoneNumber( String phoneNumber )
    {
        return null;
    }

    @Override
    public boolean canAddOrUpdateUser( Collection<String> userGroups )
    {
        return false;
    }

    @Override
    public boolean canAddOrUpdateUser( Collection<String> userGroups, User currentUser )
    {
        return false;
    }

    @Override
    public Long addUserCredentials( UserCredentials userCredentials )
    {
        return 0L;
    }

    @Override
    public void updateUserCredentials( UserCredentials userCredentials )
    {
    }

    @Override
    public UserCredentials getUserCredentialsByUsername( String username )
    {
        for ( User user : users )
        {
            if ( user.getUsername().equals( username ) )
            {
                return user.getUserCredentials();
            }
        }
        return null;
    }

    @Override
    public UserCredentials getUserCredentialsByOpenId( String openId )
    {
        return null;
    }

    @Override
    public UserCredentials getUserCredentialsByLdapId( String ldapId )
    {
        return null;
    }

    @Override
    public List<UserCredentials> getAllUserCredentials()
    {
        return null;
    }

    @Override
    public void encodeAndSetPassword( User user, String rawPassword )
    {
    }

    @Override
    public void encodeAndSetPassword( UserCredentials userCredentials, String rawPassword )
    {
    }

    @Override
    public void setLastLogin( String username )
    {
    }

    @Override
    public int getActiveUsersCount( int days )
    {
        return 0;
    }

    @Override
    public int getActiveUsersCount( Date since )
    {
        return 0;
    }

    @Override
    public boolean credentialsNonExpired( UserCredentials credentials )
    {
        return false;
    }

    @Override
    public Long addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        return 0L;
    }

    @Override
    public void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
    }

    @Override
    public UserAuthorityGroup getUserAuthorityGroup( Long id )
    {
        return null;
    }

    @Override
    public UserAuthorityGroup getUserAuthorityGroup( String uid )
    {
        return null;
    }

    @Override
    public UserAuthorityGroup getUserAuthorityGroupByName( String name )
    {
        return null;
    }

    @Override
    public void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
    }

    @Override
    public List<UserAuthorityGroup> getAllUserAuthorityGroups()
    {
        return null;
    }

    @Override
    public List<UserAuthorityGroup> getUserRolesByUid( Collection<String> uids )
    {
        return null;
    }

    @Override
    public List<UserAuthorityGroup> getUserRolesBetween( int first, int max )
    {
        return null;
    }

    @Override
    public List<UserAuthorityGroup> getUserRolesBetweenByName( String name, int first, int max )
    {
        return null;
    }

    @Override
    public int countTodoTaskUserAuthorityGroups( TodoTask todoTask )
    {
        return 0;
    }

    @Override
    public int countFieldSetUserAuthorityGroups( DataSet dataSet )
    {
        return 0;
    }

    @Override
    public void canIssueFilter( Collection<UserAuthorityGroup> userRoles )
    {
    }

    @Override
    public List<ErrorReport> validateUser( User user, User currentUser )
    {
        return null;
    }

    @Override
    public List<User> getExpiringUsers()
    {
        return null;
    }

    @Override
    public void set2FA( User user, Boolean twoFA )
    {
    }
}