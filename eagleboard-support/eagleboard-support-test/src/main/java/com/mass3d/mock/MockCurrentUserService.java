package com.mass3d.mock;

import com.google.common.collect.Sets;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserAuthorityGroup;
import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserInfo;
import java.util.Arrays;
import java.util.Set;

public class MockCurrentUserService
    implements CurrentUserService
{
    private User currentUser;

    private boolean superUserFlag;
    
    public MockCurrentUserService( User currentUser )
    {
        this.currentUser = currentUser;
    }

//    public MockCurrentUserService( Set<OrganisationUnit> organisationUnits, Set<OrganisationUnit> dataViewOrganisationUnits, String... auths )
//    {
//        this( true, organisationUnits, dataViewOrganisationUnits, auths );
//    }

    public MockCurrentUserService( boolean superUserFlag, String... auths )
    {
        UserAuthorityGroup userRole = new UserAuthorityGroup();
        userRole.setAutoFields();
        userRole.getAuthorities().addAll( Arrays.asList( auths ) );

        this.superUserFlag = superUserFlag;
        UserCredentials credentials = new UserCredentials();
        credentials.setUsername( "currentUser" );
        credentials.getUserAuthorityGroups().add( userRole );
        credentials.setAutoFields();
        
        User user = new User();
        user.setFirstName( "Current" );
        user.setSurname( "User" );
//        user.setOrganisationUnits( organisationUnits );
//        user.setDataViewOrganisationUnits( dataViewOrganisationUnits );
        user.setUserCredentials( credentials );
        user.setAutoFields();
        credentials.setUserInfo( user );
        
        this.currentUser = user;
    }
    
    @Override
    public String getCurrentUsername()
    {
        return currentUser.getUsername();
    }

    @Override
    public Set<String> getCurrentUserAuthorities()
    {
        return Sets.newHashSet( currentUser.getUserCredentials().getAllAuthorities() );
    }
    
    @Override
    public User getCurrentUser()
    {
        return currentUser;
    }

    @Override
    public UserInfo getCurrentUserInfo()
    {
        return new UserInfo( currentUser.getId(),
            currentUser.getUsername(), currentUser.getUserCredentials().getAllAuthorities() );
    }

    @Override
    public boolean currentUserIsSuper()
    {
        return superUserFlag;
    }

    @Override
    public void clearCurrentUser()
    {
        currentUser = null;
    }

    @Override
    public boolean currentUserIsAuthorized( String auth )
    {
        return true;
    }
}
