package com.mass3d;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mass3d.analytics.AggregationType;
import com.mass3d.common.IdentifiableObjectManager;
import com.mass3d.common.ValueType;
import com.mass3d.dataelement.DataElement;
import com.mass3d.dataelement.DataElementService;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserAuthorityGroup;
import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

@SpringBootApplication
//    (exclude = {
//    SecurityAutoConfiguration.class,
//    RedisAutoConfiguration.class})
// , exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class
@EnableTransactionManagement
public class MainApp {

//  @Autowired
//  DataElementStore dataElementStore;
  @Autowired
  List<CurrentUserService> currentUserServices;

  @Autowired
  protected UserService userService;
//  @Bean
//  CurrentUserService currentUserService() {
//    return new DefaultCurrentUserService();
//  }

  @Autowired
  IdentifiableObjectManager identifiableObjectManager;

  @Autowired
  DataElementService dataElementService;

  public static void main(String[] args) {
    SpringApplication.run(MainApp.class, args);
  }

  protected User createAdminUser( String... authorities )
  {
    Assert.notNull( userService, "UserService must be injected in test" );

    String username = "admin";
    String password = "district";

    UserAuthorityGroup userAuthorityGroup = new UserAuthorityGroup();
    userAuthorityGroup.setUid( "yrB6vc5Ip3r" );
    userAuthorityGroup.setCode( "Superuser" );
    userAuthorityGroup.setName( "Superuser" );
    userAuthorityGroup.setDescription( "Superuser" );
    userAuthorityGroup.setAuthorities( Sets.newHashSet( authorities ) );

    userService.addUserAuthorityGroup( userAuthorityGroup );

    User user = new User();
    user.setUid( "M5zQapPyTZI" );
    user.setCode( username );
    user.setFirstName( username );
    user.setSurname( username );

    userService.addUser( user );

    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setUid( "KvMx6c1eoYo" );
//    userCredentials.setUuid( UUID.fromString( "6507f586-f154-4ec1-a25e-d7aa51de5216" ) );
    userCredentials.setCode( username );
    userCredentials.setUser( user );
    userCredentials.setUserInfo( user );
    userCredentials.setUsername( username );
    userCredentials.getUserAuthorityGroups().add( userAuthorityGroup );

    userService.encodeAndSetPassword( userCredentials, password );
    userService.addUserCredentials( userCredentials );

    user.setUserCredentials( userCredentials );
    userService.updateUser( user );

    return user;
  }

  protected void preCreateInjectAdminUserWithoutPersistence()
  {
    List<GrantedAuthority> grantedAuthorities = ImmutableList.of( new SimpleGrantedAuthority( "ALL" ) );

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        "admin", "district", grantedAuthorities );

    Authentication authentication = new UsernamePasswordAuthenticationToken( userDetails, "", grantedAuthorities );

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication( authentication );
    SecurityContextHolder.setContext( context );
  }


  protected User preCreateInjectAdminUser()
  {
    List<GrantedAuthority> grantedAuthorities = ImmutableList.of( new SimpleGrantedAuthority( "ALL" ) );

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        "admin", "district", grantedAuthorities );

    Authentication authentication = new UsernamePasswordAuthenticationToken( userDetails, "", grantedAuthorities );

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication( authentication );
    SecurityContextHolder.setContext( context );

    return createAndInjectAdminUser( "ALL" );
  }

  protected User createAndInjectAdminUser( String... authorities )
  {
    User user = createAdminUser( authorities );

    List<GrantedAuthority> grantedAuthorities = user.getUserCredentials().getAllAuthorities()
        .stream().map( SimpleGrantedAuthority::new ).collect( Collectors.toList() );

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        user.getUserCredentials().getUsername(), user.getUserCredentials().getPassword(), grantedAuthorities );

    Authentication authentication = new UsernamePasswordAuthenticationToken( userDetails, "", grantedAuthorities );

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication( authentication );
    SecurityContextHolder.setContext( securityContext );

    return user;
  }

  public static DataElement createDataField( char uniqueCharacter )
  {
    DataElement dataElement = new DataElement();
    dataElement.setAutoFields();

    dataElement.setUid( "deabcdefgh" + uniqueCharacter );
    dataElement.setName( "DataElement" + uniqueCharacter );
    dataElement.setShortName( "DataElementShort" + uniqueCharacter );
    dataElement.setCode( "DataElementCode" + uniqueCharacter );
    dataElement.setDescription( "DataElementDescription" + uniqueCharacter );
//    dataElement.setDomainType( DataFieldDomain.AGGREGATE );
    dataElement.setValueType( ValueType.INTEGER );
    dataElement.setAggregationType( AggregationType.SUM );
    return dataElement;
  }

//  public void save( IdentifiableObject object )
//  {
//    save( object, true );
//  }
//
//  public void save( IdentifiableObject object, boolean clearSharing )
//  {
//    IdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( object.getClass() );
//
//    if ( store != null )
//    {
//      store.save( object, clearSharing );
//    }
//  }
//
//  private <T extends IdentifiableObject> IdentifiableObjectStore<IdentifiableObject> getIdentifiableObjectStore( Class<T> clazz )
//  {
//    initMaps();
//
//    IdentifiableObjectStore<? extends IdentifiableObject> store = identifiableObjectStoreMap.get( clazz );
//
//    if ( store == null )
//    {
//      store = identifiableObjectStoreMap.get( clazz.getSuperclass() );
//
//      if ( store == null && !UserCredentials.class.isAssignableFrom( clazz ) )
//      {
////        log.debug( "No IdentifiableObjectStore found for class: " + clazz );
//      }
//    }
//
//    return (IdentifiableObjectStore<IdentifiableObject>) store;
//  }
//
//  private void initMaps()
//  {
//    if ( identifiableObjectStoreMap != null )
//    {
//      return; // Already initialized
//    }
//
//    identifiableObjectStoreMap = new HashMap<>();
//
//    for ( IdentifiableObjectStore<? extends IdentifiableObject> store : identifiableObjectStores )
//    {
//      identifiableObjectStoreMap.put( store.getClazz(), store );
//    }
//  }

  @Bean
  public CommandLineRunner dataLoader() {
    return new CommandLineRunner() {
      @Override
      public void run(String... args) throws Exception {

//        preCreateInjectAdminUser();
        createAdminUser("ALL");

        DataElement dataElementA = createDataField( 'A' );
        DataElement dataElementB = createDataField( 'B' );
        DataElement dataElementC = createDataField( 'C' );

        identifiableObjectManager.save(dataElementA);
        Long idA = dataElementA.getId();
        identifiableObjectManager.save(dataElementB);
        Long idB = dataElementB.getId();
        identifiableObjectManager.save(dataElementC);
        Long idC = dataElementC.getId();

//        dataElementA = dataElementStore.get( idA );

        System.out.println("idA = " + idA);
        System.out.println("idB = " + idB);
        System.out.println("idC = " + idC);
        System.out.println("currentUserServices.size() = " + currentUserServices.size());
        DataElement dataElementGet = identifiableObjectManager.get(DataElement.class, idC);
        System.out.println("DataElement-------- = " + dataElementGet);

        int count = identifiableObjectManager.getCount(DataElement.class);
        System.out.println("DataElement Count = " + count);

        List<Long> ids = Arrays.asList(idA, idB, idC);
        List<DataElement> dataElements = identifiableObjectManager.getById(DataElement.class, ids);
        for (DataElement dataElement : dataElements) {
          System.out.println("DataElement in list = " + dataElement);
        }

        List<DataElement> dataFieldsAll = dataElementService.getAllDataElements();
        for (DataElement dataElement : dataFieldsAll) {
          System.out.println("DataElement in list = " + dataElement);
        }
      }
    };
  }
}
