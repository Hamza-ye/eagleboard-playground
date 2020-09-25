package com.mass3d.datafield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mass3d.EagleboardSpringTest;
import com.mass3d.common.ValueType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DataFieldServiceTest
    extends EagleboardSpringTest
{
    @Autowired
    private DataFieldService dataFieldService;

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataField()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );

        Long idA = dataFieldService.addDataField( dataFieldA );
        Long idB = dataFieldService.addDataField( dataFieldB );
        Long idC = dataFieldService.addDataField( dataFieldC );

        assertNotNull( dataFieldA.getUid() );
        assertNotNull( dataFieldB.getUid() );
        assertNotNull( dataFieldC.getUid() );

        assertNotNull( dataFieldA.getLastUpdated() );
        assertNotNull( dataFieldB.getLastUpdated() );
        assertNotNull( dataFieldC.getLastUpdated() );

        dataFieldA = dataFieldService.getDataField( idA );
        assertNotNull( dataFieldA );
        assertEquals( idA, dataFieldA.getId() );
        assertEquals( "DataElementA", dataFieldA.getName() );

        dataFieldB = dataFieldService.getDataField( idB );
        assertNotNull( dataFieldB );
        assertEquals( idB, dataFieldB.getId() );
        assertEquals( "DataElementB", dataFieldB.getName() );

        dataFieldC = dataFieldService.getDataField( idC );
        assertNotNull( dataFieldC );
        assertEquals( idC, dataFieldC.getId() );
        assertEquals( "DataElementC", dataFieldC.getName() );
    }

    @Test
    public void testUpdateDataField()
    {
        DataField dataFieldA = createDataField( 'A' );

        Long idA = dataFieldService.addDataField( dataFieldA );
        assertNotNull( dataFieldA.getUid() );
        assertNotNull( dataFieldA.getLastUpdated() );

        dataFieldA = dataFieldService.getDataField( idA );
        assertEquals( ValueType.INTEGER, dataFieldA.getValueType() );

        dataFieldA.setValueType( ValueType.BOOLEAN );
        dataFieldService.updateDataField( dataFieldA );
        dataFieldA = dataFieldService.getDataField( idA );
        assertNotNull( dataFieldA.getValueType() );
        assertEquals( ValueType.BOOLEAN, dataFieldA.getValueType() );
    }

    @Test
    public void testDeleteAndGetDataField()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );
        DataField dataFieldD = createDataField( 'D' );

        Long idA = dataFieldService.addDataField( dataFieldA );
        Long idB = dataFieldService.addDataField( dataFieldB );
        Long idC = dataFieldService.addDataField( dataFieldC );
        Long idD = dataFieldService.addDataField( dataFieldD );

        assertNotNull( dataFieldService.getDataField( idA ) );
        assertNotNull( dataFieldService.getDataField( idB ) );
        assertNotNull( dataFieldService.getDataField( idC ) );
        assertNotNull( dataFieldService.getDataField( idD ) );

        dataFieldService.deleteDataField( dataFieldB );

        assertNotNull( dataFieldService.getDataField( idA ) );
        assertNull( dataFieldService.getDataField( idB ) );
        assertNotNull( dataFieldService.getDataField( idC ) );
        assertNotNull( dataFieldService.getDataField( idD ) );

        dataFieldService.deleteDataField( dataFieldC );

        assertNotNull( dataFieldService.getDataField( idA ) );
        assertNull( dataFieldService.getDataField( idB ) );
        assertNull( dataFieldService.getDataField( idC ) );
        assertNotNull( dataFieldService.getDataField( idD ) );

        dataFieldService.deleteDataField( dataFieldD );

        assertNotNull( dataFieldService.getDataField( idA ) );
        assertNull( dataFieldService.getDataField( idB ) );
        assertNull( dataFieldService.getDataField( idC ) );
        assertNull( dataFieldService.getDataField( idD ) );
    }

    @Test
    public void testGetDataFieldByCode()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );

        dataFieldA.setCode( "codeA" );
        dataFieldB.setCode( "codeB" );
        dataFieldC.setCode( "codeC" );

        Long idA = dataFieldService.addDataField( dataFieldA );
        Long idB = dataFieldService.addDataField( dataFieldB );
        dataFieldService.addDataField( dataFieldC );

        dataFieldA = dataFieldService.getDataFieldByCode( "codeA" );
        assertNotNull( dataFieldA );
        assertEquals( idA, dataFieldA.getId() );
        assertEquals( "DataElementA", dataFieldA.getName() );

        dataFieldB = dataFieldService.getDataFieldByCode( "codeB" );
        assertNotNull( dataFieldB );
        assertEquals( idB, dataFieldB.getId() );
        assertEquals( "DataElementB", dataFieldB.getName() );

        DataField dataFieldE = dataFieldService.getDataFieldByCode( "codeE" );
        assertNull( dataFieldE );
    }

    @Test
    public void testGetAllDataFields()
    {
        assertEquals( 0, dataFieldService.getAllDataFields().size() );

        DataField dataElementA = createDataField( 'A' );
        DataField dataElementB = createDataField( 'B' );
        DataField dataElementC = createDataField( 'C' );
        DataField dataElementD = createDataField( 'D' );

        dataFieldService.addDataField( dataElementA );
        dataFieldService.addDataField( dataElementB );
        dataFieldService.addDataField( dataElementC );
        dataFieldService.addDataField( dataElementD );

        List<DataField> dataElementsRef = new ArrayList<>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementC );
        dataElementsRef.add( dataElementD );

        List<DataField> dataElements = dataFieldService.getAllDataFields();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetAllDataFieldsByType()
    {
        assertEquals( 0, dataFieldService.getAllDataFields().size() );

        DataField dataElementA = createDataField( 'A' );
        DataField dataElementB = createDataField( 'B' );
        DataField dataElementC = createDataField( 'C' );
        DataField dataElementD = createDataField( 'D' );

        dataElementA.setValueType( ValueType.FILE_RESOURCE );
        dataElementB.setValueType( ValueType.EMAIL );
        dataElementC.setValueType( ValueType.BOOLEAN );
        dataElementD.setValueType( ValueType.FILE_RESOURCE );

        dataFieldService.addDataField( dataElementA );
        dataFieldService.addDataField( dataElementB );
        dataFieldService.addDataField( dataElementC );
        dataFieldService.addDataField( dataElementD );

        List<DataField> dataElementsRef = new ArrayList<>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementD );

        List<DataField> dataElements = dataFieldService.getAllDataFieldsByValueType( ValueType.FILE_RESOURCE );
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    // -------------------------------------------------------------------------
    // DataFieldGroup
    // -------------------------------------------------------------------------

//    @Test
//    public void testAddDataFieldGroup()
//    {
//        DataFieldGroup dataElementGroupA = new DataFieldGroup( "DataFieldGroupA" );
//        DataFieldGroup dataElementGroupB = new DataFieldGroup( "DataFieldGroupB" );
//        DataFieldGroup dataElementGroupC = new DataFieldGroup( "DataFieldGroupC" );
//
//        Long idA = dataFieldService.addDataFieldGroup( dataElementGroupA );
//        Long idB = dataFieldService.addDataFieldGroup( dataElementGroupB );
//        Long idC = dataFieldService.addDataFieldGroup( dataElementGroupC );
//
//        dataElementGroupA = dataFieldService.getDataElementGroup( idA );
//        assertNotNull( dataElementGroupA );
//        assertEquals( idA, dataElementGroupA.getId() );
//        assertEquals( "DataElementGroupA", dataElementGroupA.getName() );
//
//        dataElementGroupB = dataFieldService.getDataFieldGroup( idB );
//        assertNotNull( dataElementGroupB );
//        assertEquals( idB, dataElementGroupB.getId() );
//        assertEquals( "DataElementGroupB", dataElementGroupB.getName() );
//
//        dataElementGroupC = dataFieldService.getDataFieldGroup( idC );
//        assertNotNull( dataElementGroupC );
//        assertEquals( idC, dataElementGroupC.getId() );
//        assertEquals( "DataElementGroupC", dataElementGroupC.getName() );
//    }
//
//    @Test
//    public void testUpdateDataElementGroup()
//    {
//        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
//        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
//        DataElementGroup dataElementGroupC = new DataElementGroup( "DataElementGroupC" );
//
//        Long idA = dataFieldService.addDataElementGroup( dataElementGroupA );
//        Long idB = dataFieldService.addDataElementGroup( dataElementGroupB );
//        Long idC = dataFieldService.addDataElementGroup( dataElementGroupC );
//
//        dataElementGroupA = dataFieldService.getDataElementGroup( idA );
//        assertNotNull( dataElementGroupA );
//        assertEquals( idA, dataElementGroupA.getId() );
//        assertEquals( "DataElementGroupA", dataElementGroupA.getName() );
//
//        dataElementGroupA.setName( "DataElementGroupAA" );
//        dataFieldService.updateDataElementGroup( dataElementGroupA );
//
//        dataElementGroupA = dataFieldService.getDataElementGroup( idA );
//        assertNotNull( dataElementGroupA );
//        assertEquals( idA, dataElementGroupA.getId() );
//        assertEquals( "DataElementGroupAA", dataElementGroupA.getName() );
//
//        dataElementGroupB = dataFieldService.getDataElementGroup( idB );
//        assertNotNull( dataElementGroupB );
//        assertEquals( idB, dataElementGroupB.getId() );
//        assertEquals( "DataElementGroupB", dataElementGroupB.getName() );
//
//        dataElementGroupC = dataFieldService.getDataElementGroup( idC );
//        assertNotNull( dataElementGroupC );
//        assertEquals( idC, dataElementGroupC.getId() );
//        assertEquals( "DataElementGroupC", dataElementGroupC.getName() );
//    }
//
//    @Test
//    public void testDeleteAndGetDataElementGroup()
//    {
//        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
//        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
//        DataElementGroup dataElementGroupC = new DataElementGroup( "DataElementGroupC" );
//        DataElementGroup dataElementGroupD = new DataElementGroup( "DataElementGroupD" );
//
//        Long idA = dataFieldService.addDataElementGroup( dataElementGroupA );
//        Long idB = dataFieldService.addDataElementGroup( dataElementGroupB );
//        Long idC = dataFieldService.addDataElementGroup( dataElementGroupC );
//        Long idD = dataFieldService.addDataElementGroup( dataElementGroupD );
//
//        assertNotNull( dataFieldService.getDataElementGroup( idA ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idB ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idC ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idD ) );
//
//        dataFieldService.deleteDataElementGroup( dataElementGroupA );
//        assertNull( dataFieldService.getDataElementGroup( idA ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idB ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idC ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idD ) );
//
//        dataFieldService.deleteDataElementGroup( dataElementGroupB );
//        assertNull( dataFieldService.getDataElementGroup( idA ) );
//        assertNull( dataFieldService.getDataElementGroup( idB ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idC ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idD ) );
//
//        dataFieldService.deleteDataElementGroup( dataElementGroupC );
//        assertNull( dataFieldService.getDataElementGroup( idA ) );
//        assertNull( dataFieldService.getDataElementGroup( idB ) );
//        assertNull( dataFieldService.getDataElementGroup( idC ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idD ) );
//
//        dataFieldService.deleteDataElementGroup( dataElementGroupD );
//        assertNull( dataFieldService.getDataElementGroup( idA ) );
//        assertNull( dataFieldService.getDataElementGroup( idB ) );
//        assertNull( dataFieldService.getDataElementGroup( idC ) );
//        assertNull( dataFieldService.getDataElementGroup( idD ) );
//    }
//
//    @Test
//    public void testGetDataElementGroupByName()
//    {
//        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
//        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
//        Long idA = dataFieldService.addDataElementGroup( dataElementGroupA );
//        Long idB = dataFieldService.addDataElementGroup( dataElementGroupB );
//
//        assertNotNull( dataFieldService.getDataElementGroup( idA ) );
//        assertNotNull( dataFieldService.getDataElementGroup( idB ) );
//
//        dataElementGroupA = dataFieldService.getDataElementGroupByName( "DataElementGroupA" );
//        assertNotNull( dataElementGroupA );
//        assertEquals( idA, dataElementGroupA.getId() );
//        assertEquals( "DataElementGroupA", dataElementGroupA.getName() );
//
//        dataElementGroupB = dataFieldService.getDataElementGroupByName( "DataElementGroupB" );
//        assertNotNull( dataElementGroupB );
//        assertEquals( idB, dataElementGroupB.getId() );
//        assertEquals( "DataElementGroupB", dataElementGroupB.getName() );
//
//        DataElementGroup dataElementGroupC = dataFieldService.getDataElementGroupByName( "DataElementGroupC" );
//        assertNull( dataElementGroupC );
//    }
//
//    @Test
//    public void testAndAndGetDataElementGroupSet()
//    {
//        DataElementGroup degA = createDataElementGroup( 'A' );
//        DataElementGroup degB = createDataElementGroup( 'B' );
//        DataElementGroup degC = createDataElementGroup( 'C' );
//
//        dataFieldService.addDataElementGroup( degA );
//        dataFieldService.addDataElementGroup( degB );
//        dataFieldService.addDataElementGroup( degC );
//
//        DataElementGroupSet degsA = createDataElementGroupSet( 'A' );
//        degsA.addDataElementGroup( degA );
//        degsA.addDataElementGroup( degB );
//
//        DataElementGroupSet degsB = createDataElementGroupSet( 'B' );
//        degsB.addDataElementGroup( degB );
//        degsB.addDataElementGroup( degC );
//
//        dataFieldService.addDataElementGroupSet( degsA );
//        dataFieldService.addDataElementGroupSet( degsB );
//
//        assertTrue( degsA.getMembers().contains( degA ) );
//        assertTrue( degsA.getMembers().contains( degB ) );
//        assertTrue( degA.getGroupSets().contains( degsA ) );
//        assertTrue( degB.getGroupSets().contains( degsA ) );
//
//        assertTrue( degsB.getMembers().contains( degB ) );
//        assertTrue( degsB.getMembers().contains( degC ) );
//        assertTrue( degB.getGroupSets().contains( degsB ) );
//        assertTrue( degC.getGroupSets().contains( degsB ) );
//    }
//
//    @Test
//    public void testDataElementUrl()
//    {
//        DataField de = createDataField( 'A', ValueType.URL, AggregationType.SUM );
//
//        Long id = dataFieldService.addDataField( de );
//
//        assertNotNull( dataFieldService.getDataField( id ) );
//
//    }
}
