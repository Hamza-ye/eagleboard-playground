package com.mass3d.startup;

import com.mass3d.system.startup.AbstractStartupRoutine;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.UserQueryParams;
import com.mass3d.user.UserService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TwoFAPopulator
    extends AbstractStartupRoutine
{
    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Override
    public void execute()
        throws Exception
    {
        UserQueryParams userQueryParams = new UserQueryParams( currentUserService.getCurrentUser() );
        userQueryParams.setNot2FA( true );

        userService.getUsers( userQueryParams ).forEach( user -> {
            user.getUserCredentials().setSecret( null );
            userService.updateUser( user );
        } );
    }
}
