package com.mass3d.security;

import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserService;
import com.mass3d.util.ObjectUtils;
import com.mass3d.system.util.SecurityUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultUserDetailsService
    implements UserDetailsService
{
  public static final String ID = UserDetailsService.class.getName();

  private static final Log log = LogFactory.getLog( DefaultUserDetailsService.class );

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private UserService userService;

  @Autowired
  public void setUserService( UserService userService )
  {
    this.userService = userService;
  }

  private SecurityService securityService;

  public void setSecurityService( SecurityService securityService )
  {
    this.securityService = securityService;
  }

  // -------------------------------------------------------------------------
  // UserDetailsService implementation
  // -------------------------------------------------------------------------

  @Override
  @Transactional
  public final UserDetails loadUserByUsername( String username )
      throws UsernameNotFoundException, DataAccessException
  {
    UserCredentials credentials = userService.getUserCredentialsByUsername( username );

    // ---------------------------------------------------------------------
    // OpenId
    // ---------------------------------------------------------------------

    if ( credentials == null )
    {
      credentials = userService.getUserCredentialsByOpenId( username );

      if ( credentials == null )
      {
        throw new UsernameNotFoundException( "Username does not exist" );
      }
    }

    boolean enabled = !credentials.isDisabled();
    boolean credentialsNonExpired = true;
    boolean accountNonLocked = !securityService.isLocked( credentials.getUsername() );

    if ( ObjectUtils.anyIsFalse( enabled, credentialsNonExpired, accountNonLocked ) )
    {
      log.info( String.format( "Login attempt for disabled/locked user: '%s', enabled: %b, credentials non-expired: %b, account non-locked: %b",
          username, enabled, credentialsNonExpired, accountNonLocked ) );
    }

    return new User( credentials.getUsername(), credentials.getPassword(),
        enabled, true, credentialsNonExpired, accountNonLocked, SecurityUtils.getGrantedAuthorities( credentials ) );
  }
}
