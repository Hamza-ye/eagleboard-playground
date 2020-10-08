package com.mass3d;

import com.mass3d.common.IdentifiableObjectManager;
import com.mass3d.common.ValueType;
import com.mass3d.dataelement.DataElement;
import com.mass3d.dataelement.DataElementStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication /* (exclude = {
    SecurityAutoConfiguration.class,
    RedisAutoConfiguration.class}) */
// , exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class
@EnableTransactionManagement
public class EagleboardPlaygroundApplication {

  @Autowired
  DataElementStore dataElementStore;

  @Autowired
  IdentifiableObjectManager identifiableObjectManager;

  public static void main(String[] args) {
    SpringApplication.run(EagleboardPlaygroundApplication.class, args);
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
    dataElement.setValueType( ValueType.INTEGER );

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

//  @Bean
  public CommandLineRunner dataLoader() {
    return new CommandLineRunner() {
      @Override
      public void run(String... args) throws Exception {
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
      }
    };
  }
}
