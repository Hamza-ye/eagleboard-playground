package com.mass3d;

import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.IdentifiableObjectManager;
import com.mass3d.common.IdentifiableObjectStore;
import com.mass3d.common.ValueType;
import com.mass3d.datafield.DataField;
import com.mass3d.datafield.DataFieldStore;
import com.mass3d.user.UserCredentials;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication /* (exclude = {
    SecurityAutoConfiguration.class,
    RedisAutoConfiguration.class}) */
// , exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class
@EnableTransactionManagement
public class EagleboardPlaygroundApplication {

  @Autowired
  DataFieldStore dataFieldStore;

  @Autowired
  IdentifiableObjectManager identifiableObjectManager;

  public static void main(String[] args) {
    SpringApplication.run(EagleboardPlaygroundApplication.class, args);
  }

  public static DataField createDataField( char uniqueCharacter )
  {
    DataField dataField = new DataField();
    dataField.setAutoFields();

    dataField.setUid( "deabcdefgh" + uniqueCharacter );
    dataField.setName( "DataElement" + uniqueCharacter );
    dataField.setShortName( "DataElementShort" + uniqueCharacter );
    dataField.setCode( "DataElementCode" + uniqueCharacter );
    dataField.setDescription( "DataElementDescription" + uniqueCharacter );
    dataField.setValueType( ValueType.INTEGER );

    return dataField;
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
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );

        identifiableObjectManager.save( dataFieldA );
        Long idA = dataFieldA.getId();
        identifiableObjectManager.save( dataFieldB );
        Long idB = dataFieldB.getId();
        identifiableObjectManager.save( dataFieldC );
        Long idC = dataFieldC.getId();

//        dataFieldA = dataFieldStore.get( idA );

        System.out.println("idA = " + idA);
        System.out.println("idB = " + idB);
        System.out.println("idC = " + idC);
      }
    };
  }
}
