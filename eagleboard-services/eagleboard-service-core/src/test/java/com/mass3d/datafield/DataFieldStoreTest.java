package com.mass3d.datafield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mass3d.EagleboardSpringTest;
import com.mass3d.common.IdentifiableObjectManager;
import com.mass3d.common.ValueType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@RunWith( SpringRunner.class )
//@DataJpaTest
public class DataFieldStoreTest
    extends EagleboardSpringTest
{
    @Autowired
    private DataFieldStore dataFieldStore;

//    @Autowired
//    private AttributeService attributeService;
    
    @Autowired
    private IdentifiableObjectManager idObjectManager;

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataField()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );

        dataFieldStore.save( dataFieldA );
        Long idA = dataFieldA.getId();
        dataFieldStore.save( dataFieldB );
        Long idB = dataFieldB.getId();
        dataFieldStore.save( dataFieldC );
        Long idC = dataFieldC.getId();

        dataFieldA = dataFieldStore.get( idA );
        assertNotNull( dataFieldA );
        assertEquals( idA, dataFieldA.getId() );
        assertEquals( "DataElementA", dataFieldA.getName() );

        dataFieldB = dataFieldStore.get( idB );
        assertNotNull( dataFieldB );
        assertEquals( idB, dataFieldB.getId() );
        assertEquals( "DataElementB", dataFieldB.getName() );

        dataFieldC = dataFieldStore.get( idC );
        assertNotNull( dataFieldC );
        assertEquals( idC, dataFieldC.getId() );
        assertEquals( "DataElementC", dataFieldC.getName() );
    }

    @Test
    public void testUpdateDataElement()
    {
        DataField dataElementA = createDataField( 'A' );
        dataFieldStore.save( dataElementA );
        Long idA = dataElementA.getId();
        dataElementA = dataFieldStore.get( idA );
        assertEquals( ValueType.INTEGER, dataElementA.getValueType() );

        dataElementA.setValueType( ValueType.BOOLEAN );
        dataFieldStore.update( dataElementA );
        dataElementA = dataFieldStore.get( idA );
        assertNotNull( dataElementA.getValueType() );
        assertEquals( ValueType.BOOLEAN, dataElementA.getValueType() );
    }

    @Test
    public void testDeleteAndGetDataField()
    {
        DataField dataElementA = createDataField( 'A' );
        DataField dataElementB = createDataField( 'B' );
        DataField dataElementC = createDataField( 'C' );
        DataField dataElementD = createDataField( 'D' );

        dataFieldStore.save( dataElementA );
        Long idA = dataElementA.getId();
        dataFieldStore.save( dataElementB );
        Long idB = dataElementB.getId();
        dataFieldStore.save( dataElementC );
        Long idC = dataElementC.getId();
        dataFieldStore.save( dataElementD );
        Long idD = dataElementD.getId();

            assertNotNull( dataFieldStore.get( idA ) );
        assertNotNull( dataFieldStore.get( idB ) );
        assertNotNull( dataFieldStore.get( idC ) );
        assertNotNull( dataFieldStore.get( idD ) );

        dataElementA = dataFieldStore.get( idA );
        dataElementB = dataFieldStore.get( idB );
        dataElementC = dataFieldStore.get( idC );
        dataElementD = dataFieldStore.get( idD );

        dataFieldStore.delete( dataElementA );
        assertNull( dataFieldStore.get( idA ) );
        assertNotNull( dataFieldStore.get( idB ) );
        assertNotNull( dataFieldStore.get( idC ) );
        assertNotNull( dataFieldStore.get( idD ) );

        dataFieldStore.delete( dataElementB );
        assertNull( dataFieldStore.get( idA ) );
        assertNull( dataFieldStore.get( idB ) );
        assertNotNull( dataFieldStore.get( idC ) );
        assertNotNull( dataFieldStore.get( idD ) );

        dataFieldStore.delete( dataElementC );
        assertNull( dataFieldStore.get( idA ) );
        assertNull( dataFieldStore.get( idB ) );
        assertNull( dataFieldStore.get( idC ) );
        assertNotNull( dataFieldStore.get( idD ) );

        dataFieldStore.delete( dataElementD );
        assertNull( dataFieldStore.get( idA ) );
        assertNull( dataFieldStore.get( idB ) );
        assertNull( dataFieldStore.get( idC ) );
        assertNull( dataFieldStore.get( idD ) );
    }

    @Test
    public void testGetDataFieldByName()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        dataFieldStore.save( dataFieldA );
        Long idA = dataFieldA.getId();
        dataFieldStore.save( dataFieldB );
        Long idB = dataFieldB.getId();

        dataFieldA = dataFieldStore.getByName( "DataElementA" );
        assertNotNull( dataFieldA );
        assertEquals( idA, dataFieldA.getId() );
        assertEquals( "DataElementA", dataFieldA.getName() );

        dataFieldB = dataFieldStore.getByName( "DataElementB" );
        assertNotNull( dataFieldB );
        assertEquals( idB, dataFieldB.getId() );
        assertEquals( "DataElementB", dataFieldB.getName() );

        DataField dataElementC = dataFieldStore.getByName( "DataElementC" );
        assertNull( dataElementC );
    }

    @Test
    public void testGetAllDataFields()
    {
        assertEquals( 0, dataFieldStore.getAll().size() );

        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );
        DataField dataFieldD = createDataField( 'D' );

        dataFieldStore.save( dataFieldA );
        dataFieldStore.save( dataFieldB );
        dataFieldStore.save( dataFieldC );
        dataFieldStore.save( dataFieldD );

        List<DataField> dataFieldsRef = new ArrayList<>();
        dataFieldsRef.add( dataFieldA );
        dataFieldsRef.add( dataFieldB );
        dataFieldsRef.add( dataFieldC );
        dataFieldsRef.add( dataFieldD );

        List<DataField> dataFields = dataFieldStore.getAll();
        assertNotNull( dataFields );
        assertEquals( dataFieldsRef.size(), dataFields.size() );
        assertTrue( dataFields.containsAll( dataFieldsRef ) );
    }

//    @Test
//    public void testGetDataFieldsByDomainType()
//    {
//        assertEquals( 0, dataFieldStore.getDataFieldsByDomainType( DataFieldDomain.AGGREGATE ).size() );
//        assertEquals( 0, dataFieldStore.getDataFieldsByDomainType( DataFieldDomain.TRACKER ).size() );
//
//        DataField dataElementA = createDataField( 'A' );
//        dataElementA.setDomainType( DataFieldDomain.AGGREGATE );
//        DataField dataElementB = createDataField( 'B' );
//        dataElementB.setDomainType( DataFieldDomain.TRACKER );
//        DataField dataElementC = createDataField( 'C' );
//        dataElementC.setDomainType( DataFieldDomain.TRACKER );
//        DataField dataElementD = createDataField( 'D' );
//        dataElementD.setDomainType( DataFieldDomain.TRACKER );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//        dataFieldStore.save( dataElementC );
//        dataFieldStore.save( dataElementD );
//
//        assertEquals( 1, dataFieldStore.getDataFieldsByDomainType( DataFieldDomain.AGGREGATE ).size() );
//        assertEquals( 3, dataFieldStore.getDataFieldsByDomainType( DataFieldDomain.TRACKER ).size() );
//    }

//    @Test
//    public void testGetDataFieldAggregationLevels()
//    {
//        List<Integer> aggregationLevels = Arrays.asList( 3, 5 );
//
//        DataField dataElementA = createDataField( 'A' );
//        dataElementA.setAggregationLevels( aggregationLevels );
//
//        dataFieldStore.save( dataElementA );
//        int idA = dataElementA.getId();
//
//        assertNotNull( dataFieldStore.get( idA ).getAggregationLevels() );
//        assertEquals( 2, dataFieldStore.get( idA ).getAggregationLevels().size() );
//        assertEquals( aggregationLevels, dataFieldStore.get( idA ).getAggregationLevels() );
//    }
//

//    @Test
//    public void testGetDataFieldsWithoutFieldSets()
//    {
//        DataField dataElementA = createDataField( 'A' );
//        DataField dataElementB = createDataField( 'B' );
//        DataField dataElementC = createDataField( 'C' );
//        DataField dataElementD = createDataField( 'D' );
//        DataField dataElementE = createDataField( 'E' );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//        dataFieldStore.save( dataElementC );
//        dataFieldStore.save( dataElementD );
//        dataFieldStore.save( dataElementE );
//
//        DataElementGroup dgA = createDataElementGroup( 'A' );
//        dgA.addDataElement( dataElementA );
//        dgA.addDataElement( dataElementD );
//
//        idObjectManager.save( dgA );
//
//        List<DataElement> dataElements = dataFieldStore.getDataFieldsWithoutFieldSets();
//
//        assertEquals( 3, dataElements.size() );
//        assertTrue( dataElements.contains( dataElementB ) );
//        assertTrue( dataElements.contains( dataElementC ) );
//        assertTrue( dataElements.contains( dataElementE ) );
//    }

//    @Test
//    public void testGetDataElementsByAggregationLevel()
//    {
//        DataElement dataElementA = createDataElement( 'A' );
//        DataElement dataElementB = createDataElement( 'B' );
//        DataElement dataElementC = createDataElement( 'C' );
//
//        dataElementA.getAggregationLevels().addAll( Arrays.asList( 3, 5 ) );
//        dataElementB.getAggregationLevels().addAll( Arrays.asList( 4, 5 ) );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//        dataFieldStore.save( dataElementC );
//
//        List<DataElement> dataElements = dataFieldStore.getDataElementsByAggregationLevel( 2 );
//
//        assertEquals( 0, dataElements.size() );
//
//        dataElements = dataFieldStore.getDataElementsByAggregationLevel( 3 );
//
//        assertEquals( 1, dataElements.size() );
//
//        dataElements = dataFieldStore.getDataElementsByAggregationLevel( 4 );
//
//        assertEquals( 1, dataElements.size() );
//
//        dataElements = dataFieldStore.getDataElementsByAggregationLevel( 5 );
//
//        assertEquals( 2, dataElements.size() );
//        assertTrue( dataElements.contains( dataElementA ) );
//        assertTrue( dataElements.contains( dataElementB ) );
//    }

    @Test
    public void testGetDataElementsZeroIsSignificant()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );
        DataField dataFieldD = createDataField( 'D' );

        dataFieldA.setZeroIsSignificant( true );
        dataFieldB.setZeroIsSignificant( true );

        dataFieldStore.save( dataFieldA );
        dataFieldStore.save( dataFieldB );
        dataFieldStore.save( dataFieldC );
        dataFieldStore.save( dataFieldD );

        List<DataField> dataFields = dataFieldStore.getDataFieldsByZeroIsSignificant( true );

        assertTrue( equals( dataFields, dataFieldA, dataFieldB ) );
    }

//    @Test
//    public void testAttributeValueFromAttribute() throws NonUniqueAttributeValueException
//    {
//        Attribute attribute = new Attribute( "test", ValueType.TEXT );
//        attribute.setDataElementAttribute( true );
//        attributeService.addAttribute( attribute );
//
//        DataElement dataElementA = createDataElement( 'A' );
//        DataElement dataElementB = createDataElement( 'B' );
//        DataElement dataElementC = createDataElement( 'C' );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//        dataFieldStore.save( dataElementC );
//
//        AttributeValue attributeValueA = new AttributeValue( "SOME VALUE", attribute );
//        AttributeValue attributeValueB = new AttributeValue( "SOME VALUE", attribute );
//        AttributeValue attributeValueC = new AttributeValue( "ANOTHER VALUE", attribute );
//
//        attributeService.addAttributeValue( dataElementA, attributeValueA );
//        attributeService.addAttributeValue( dataElementB, attributeValueB );
//        attributeService.addAttributeValue( dataElementC, attributeValueC );
//
//        dataFieldStore.update( dataElementA );
//        dataFieldStore.update( dataElementB );
//        dataFieldStore.update( dataElementC );
//
//        List<AttributeValue> values = dataFieldStore.getAttributeValueByAttribute( attribute );
//        assertEquals( 3, values.size() );
//    }
//
//    @Test
//    public void testAttributeValueFromAttributeAndValue() throws NonUniqueAttributeValueException
//    {
//        Attribute attribute = new Attribute( "test", ValueType.TEXT );
//        attribute.setDataElementAttribute( true );
//        attributeService.addAttribute( attribute );
//
//        DataElement dataElementA = createDataElement( 'A' );
//        DataElement dataElementB = createDataElement( 'B' );
//        DataElement dataElementC = createDataElement( 'C' );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//        dataFieldStore.save( dataElementC );
//
//        AttributeValue attributeValueA = new AttributeValue( "SOME VALUE", attribute );
//        AttributeValue attributeValueB = new AttributeValue( "SOME VALUE", attribute );
//        AttributeValue attributeValueC = new AttributeValue( "ANOTHER VALUE", attribute );
//
//        attributeService.addAttributeValue( dataElementA, attributeValueA );
//        attributeService.addAttributeValue( dataElementB, attributeValueB );
//        attributeService.addAttributeValue( dataElementC, attributeValueC );
//
//        dataFieldStore.update( dataElementA );
//        dataFieldStore.update( dataElementB );
//        dataFieldStore.update( dataElementC );
//
//        List<AttributeValue> values = dataFieldStore
//            .getAttributeValueByAttributeAndValue( attribute, "SOME VALUE" );
//        assertEquals( 2, values.size() );
//
//        values = dataFieldStore.getAttributeValueByAttributeAndValue( attribute, "ANOTHER VALUE" );
//        assertEquals( 1, values.size() );
//    }
//
//    @Test
//    public void testUniqueAttributesWithSameValues()
//    {
//        Attribute attributeA = new Attribute( "ATTRIBUTEA", ValueType.TEXT );
//        attributeA.setDataElementAttribute( true );
//        attributeA.setUnique( true );
//        attributeService.addAttribute( attributeA );
//
//        Attribute attributeB = new Attribute( "ATTRIBUTEB", ValueType.TEXT );
//        attributeB.setDataElementAttribute( true );
//        attributeB.setUnique( true );
//        attributeService.addAttribute( attributeB );
//
//        Attribute attributeC = new Attribute( "ATTRIBUTEC", ValueType.TEXT );
//        attributeC.setDataElementAttribute( true );
//        attributeC.setUnique( true );
//        attributeService.addAttribute( attributeC );
//
//        DataElement dataElementA = createDataElement( 'A' );
//        DataElement dataElementB = createDataElement( 'B' );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//
//        AttributeValue attributeValueA = new AttributeValue( "VALUE", attributeA );
//        AttributeValue attributeValueB = new AttributeValue( "VALUE", attributeB );
//        AttributeValue attributeValueC = new AttributeValue( "VALUE", attributeC );
//
//        attributeService.addAttributeValue( dataElementA, attributeValueA );
//        attributeService.addAttributeValue( dataElementB, attributeValueB );
//        attributeService.addAttributeValue( dataElementB, attributeValueC );
//
//        dataFieldStore.update( dataElementA );
//        dataFieldStore.update( dataElementB );
//
//        assertNotNull( dataFieldStore.getByUniqueAttributeValue( attributeA, "VALUE" ) );
//        assertNotNull( dataFieldStore.getByUniqueAttributeValue( attributeB, "VALUE" ) );
//        assertNotNull( dataFieldStore.getByUniqueAttributeValue( attributeC, "VALUE" ) );
//
//        assertEquals( "DataElementA", dataFieldStore.getByUniqueAttributeValue( attributeA, "VALUE" ).getName() );
//        assertEquals( "DataElementB", dataFieldStore.getByUniqueAttributeValue( attributeB, "VALUE" ).getName() );
//        assertEquals( "DataElementB", dataFieldStore.getByUniqueAttributeValue( attributeC, "VALUE" ).getName() );
//    }
//
//    @Test
//    public void testDataElementByUniqueAttributeValue() throws NonUniqueAttributeValueException
//    {
//        Attribute attribute = new Attribute( "cid", ValueType.TEXT );
//        attribute.setDataElementAttribute( true );
//        attribute.setUnique( true );
//        attributeService.addAttribute( attribute );
//
//        DataElement dataElementA = createDataElement( 'A' );
//        DataElement dataElementB = createDataElement( 'B' );
//        DataElement dataElementC = createDataElement( 'C' );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//        dataFieldStore.save( dataElementC );
//
//        AttributeValue attributeValueA = new AttributeValue( "CID1", attribute );
//        AttributeValue attributeValueB = new AttributeValue( "CID2", attribute );
//        AttributeValue attributeValueC = new AttributeValue( "CID3", attribute );
//
//        attributeService.addAttributeValue( dataElementA, attributeValueA );
//        attributeService.addAttributeValue( dataElementB, attributeValueB );
//        attributeService.addAttributeValue( dataElementC, attributeValueC );
//
//        dataFieldStore.update( dataElementA );
//        dataFieldStore.update( dataElementB );
//        dataFieldStore.update( dataElementC );
//
//        assertNotNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID1" ) );
//        assertNotNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID2" ) );
//        assertNotNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID3" ) );
//        assertNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID4" ) );
//        assertNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID5" ) );
//
//        assertEquals( "DataElementA", dataFieldStore.getByUniqueAttributeValue( attribute, "CID1" ).getName() );
//        assertEquals( "DataElementB", dataFieldStore.getByUniqueAttributeValue( attribute, "CID2" ).getName() );
//        assertEquals( "DataElementC", dataFieldStore.getByUniqueAttributeValue( attribute, "CID3" ).getName() );
//    }
//
//    @Test
//    public void testDataElementByNonUniqueAttributeValue() throws NonUniqueAttributeValueException
//    {
//        Attribute attribute = new Attribute( "cid", ValueType.TEXT );
//        attribute.setDataElementAttribute( true );
//        attributeService.addAttribute( attribute );
//
//        DataElement dataElementA = createDataElement( 'A' );
//        DataElement dataElementB = createDataElement( 'B' );
//        DataElement dataElementC = createDataElement( 'C' );
//
//        dataFieldStore.save( dataElementA );
//        dataFieldStore.save( dataElementB );
//        dataFieldStore.save( dataElementC );
//
//        AttributeValue attributeValueA = new AttributeValue( "CID1", attribute );
//        AttributeValue attributeValueB = new AttributeValue( "CID2", attribute );
//        AttributeValue attributeValueC = new AttributeValue( "CID3", attribute );
//
//        attributeService.addAttributeValue( dataElementA, attributeValueA );
//        attributeService.addAttributeValue( dataElementB, attributeValueB );
//        attributeService.addAttributeValue( dataElementC, attributeValueC );
//
//        dataFieldStore.update( dataElementA );
//        dataFieldStore.update( dataElementB );
//        dataFieldStore.update( dataElementC );
//
//        assertNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID1" ) );
//        assertNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID2" ) );
//        assertNull( dataFieldStore.getByUniqueAttributeValue( attribute, "CID3" ) );
//    }

    @Test
    public void testGetLastUpdated()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );

        dataFieldStore.save( dataFieldA );
        dataFieldStore.save( dataFieldB );
        Date lastUpdated = dataFieldStore.getLastUpdated();

        dataFieldA.setDescription( "testA" );
        dataFieldStore.update( dataFieldA );

        dataFieldB.setDescription( "testB" );
        dataFieldStore.update( dataFieldB );

        assertNotEquals( lastUpdated, dataFieldStore.getLastUpdated() );
    }

    @Test
    public void testCountByJpaQueryParameters()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );

        dataFieldStore.save( dataFieldA );
        dataFieldStore.save( dataFieldB );

        assertEquals( 2, dataFieldStore.getCount() );
    }

    @Test
    public void testGetDataElements()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );

        dataFieldStore.save( dataFieldA );
        dataFieldStore.save( dataFieldB );

        assertNotNull( dataFieldStore.getByUid( dataFieldA.getUid() ) );

        assertNotNull( dataFieldStore.getByUidNoAcl( dataFieldA.getUid() ) );

        List<String> uids = new ArrayList<>();
        uids.add( dataFieldA.getUid() );
        uids.add( dataFieldB.getUid() );

        assertNotNull( dataFieldStore.getByUidNoAcl( uids ) );

        assertNotNull( dataFieldStore.getByName( dataFieldA.getName() ) );

        assertNotNull( dataFieldStore.getByCode( dataFieldA.getCode() ) );

        assertEquals( 1, dataFieldStore.getAllEqName( "DataElementA" ).size() );

        assertEquals( 2, dataFieldStore.getAllLikeName( "DataElement" ).size() );
    }

    @Test
    public void testCountMethods()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );

        dataFieldStore.save( dataFieldA );
        dataFieldStore.save( dataFieldB );

        assertEquals( 2, dataFieldStore.getCountLikeName( "dataelement" ) );

        assertEquals( 2, dataFieldStore.getCount() );

        assertEquals( 0, dataFieldStore.getCountGeCreated( new Date() ) );

        assertEquals( 2, dataFieldStore.getCountGeCreated( dataFieldA.getCreated() ) );

        assertEquals( 2, dataFieldStore.getCountGeLastUpdated( dataFieldA.getLastUpdated() ) );
    }
}
