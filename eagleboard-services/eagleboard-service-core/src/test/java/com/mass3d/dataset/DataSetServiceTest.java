package com.mass3d.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mass3d.EagleboardSpringTest;
import com.mass3d.dataelement.DataElement;
import com.mass3d.dataelement.DataElementService;
import com.mass3d.mock.MockCurrentUserService;
import com.mass3d.security.acl.Access;
import com.mass3d.security.acl.AccessStringHelper;
import com.mass3d.security.acl.AclService;
import com.mass3d.todotask.TodoTask;
import com.mass3d.todotask.TodoTaskService;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserAccess;
import com.mass3d.user.UserAuthorityGroup;
import com.mass3d.user.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class DataSetServiceTest
    extends EagleboardSpringTest
{
    private DataElement dataElementA;
    private DataElement dataElementB;

    private TodoTask unitA;
    private TodoTask unitB;
    private TodoTask unitC;
    private TodoTask unitD;
    private TodoTask unitE;
    private TodoTask unitF;

    private CurrentUserService mockCurrentUserService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private TodoTaskService todoTaskService;

    @Autowired
    protected UserService _userService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private AclService aclService;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        userService = _userService;

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );

        dataElementService.addDataElement(dataElementA);
        dataElementService.addDataElement(dataElementB);

        unitA = createTodotask( 'A' );
        unitB = createTodotask( 'B' );
        unitC = createTodotask( 'C' );
        unitD = createTodotask( 'D' );
        unitE = createTodotask( 'E' );
        unitF = createTodotask( 'F' );

        todoTaskService.addTodoTask( unitA );
        todoTaskService.addTodoTask( unitB );
        todoTaskService.addTodoTask( unitC );
        todoTaskService.addTodoTask( unitD );
        todoTaskService.addTodoTask( unitE );
        todoTaskService.addTodoTask( unitF );

        mockCurrentUserService = new MockCurrentUserService( true, UserAuthorityGroup.AUTHORITY_ALL );

        User user = mockCurrentUserService.getCurrentUser();
        user.setFirstName( "John" );
        user.setSurname( "Doe" );

        userService.addUser( mockCurrentUserService.getCurrentUser() );
    }

    @Override
    public void tearDownTest()
    {
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void assertEq( char uniqueCharacter, DataSet dataSet)
    {
        assertEquals( "DataSet" + uniqueCharacter, dataSet.getName() );
        assertEquals( "DataSetShort" + uniqueCharacter, dataSet.getShortName() );
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataSet()
    {
        DataSet dataSetA = createDataSet( 'A');
        DataSet dataSetB = createDataSet( 'B');

        dataSetA.addDataSetElement(dataElementA);
        dataSetA.addDataSetElement(dataElementB);

        long idA = dataSetService.addDataSet( dataSetA );
        long idB = dataSetService.addDataSet( dataSetB );

        dataSetA = dataSetService.getDataSet( idA );
        dataSetB = dataSetService.getDataSet( idB );

        assertEquals( idA, dataSetA.getId() );
        assertEq( 'A', dataSetA );

        assertEquals( idB, dataSetB.getId() );
        assertEq( 'B', dataSetB );
    }

    @Test
    public void testUpdateDataSet()
    {
        DataSet dataSet = createDataSet( 'A');

        dataSet.addDataSetElement(dataElementA);
        dataSet.addDataSetElement(dataElementB);

        long id = dataSetService.addDataSet(dataSet);

        dataSet = dataSetService.getDataSet( id );

        assertEq( 'A', dataSet);

        dataSet.setName( "DataSetB" );

        dataSetService.updateDataSet(dataSet);

        dataSet = dataSetService.getDataSet( id );

        assertEquals( dataSet.getName(), "DataSetB" );
    }

    @Test
    public void testDeleteAndGetDataSet()
    {
        DataSet dataSetA = createDataSet( 'A');
        DataSet dataSetB = createDataSet( 'B');

        dataSetA.addDataSetElement(dataElementA);
        dataSetA.addDataSetElement(dataElementB);

        long idA = dataSetService.addDataSet(dataSetA);
        long idB = dataSetService.addDataSet(dataSetB);

        assertNotNull( dataSetService.getDataSet( idA ) );
        assertNotNull( dataSetService.getDataSet( idB ) );

        dataSetService.deleteDataSet( dataSetService.getDataSet( idA ) );

        assertNull( dataSetService.getDataSet( idA ) );
        assertNotNull( dataSetService.getDataSet( idB ) );

        dataSetService.deleteDataSet( dataSetService.getDataSet( idB ) );

        assertNull( dataSetService.getDataSet( idA ) );
        assertNull( dataSetService.getDataSet( idB ) );
    }

    @Test
    public void testUpdateRemoveDataSetElements()
    {
        DataSet dataSet = createDataSet( 'A');

        dataSet.addDataSetElement(dataElementA);
        dataSet.addDataSetElement(dataElementB);

        dataSetService.addDataSet( dataSet );

        dataSet = dataSetService.getDataSet( dataSet.getId() );
        assertNotNull( dataSet );
        List<DataSetElement> dataSetElements = new ArrayList<>( dataSet.getDataSetElements() );

        assertEquals( 2, dataSet.getDataSetElements().size() );
        assertEquals( 2, dataSetElements.size() );

        // Remove data element A

        dataSet.removeDataSetElement(dataElementA);

        dataSetService.updateDataSet( dataSet );

        dataSet = dataSetService.getDataSet( dataSet.getId() );
        assertNotNull( dataSet );
        dataSetElements = new ArrayList<>( dataSet.getDataSetElements() );

        assertEquals( 1, dataSet.getDataSetElements().size() );
        assertEquals( 1, dataSetElements.size() );

        // Remove data element B

        dataSet.removeDataSetElement(dataElementB);

        dataSetService.updateDataSet( dataSet );

        dataSet = dataSetService.getDataSet( dataSet.getId() );
        assertNotNull( dataSet );
        dataSetElements = new ArrayList<>( dataSet.getDataSetElements() );

        assertEquals( 0, dataSet.getDataSetElements().size() );
        assertEquals( 0, dataSetElements.size() );
    }

    @Test
    public void testDeleteRemoveDataSetElements()
    {
        DataSet dataSet = createDataSet( 'A');

        dataSet.addDataSetElement(dataElementA);
        dataSet.addDataSetElement(dataElementB);

        Long ds = dataSetService.addDataSet(dataSet);

        dataSet = dataSetService.getDataSet( dataSet.getId() );
        assertNotNull(dataSet);
        List<DataSetElement> dataSetElements = new ArrayList<>( dataSet.getDataSetElements() );

        assertEquals(dataSet, dataSetService.getDataSet( ds ) );
        assertEquals( 2, dataSet.getDataSetElements().size() );
        assertEquals( 2, dataSetElements.size() );

        dataSetService.deleteDataSet(dataSet);

        assertNull( dataSetService.getDataSet( ds ) );
    }

    @Test
    public void testGetAllDataSets()
    {
        DataSet dataSetA = createDataSet( 'A');
        DataSet dataSetB = createDataSet( 'B');

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        List<DataSet> dataSets = dataSetService.getAllDataSets();

        assertEquals( dataSets.size(), 2 );
        assertTrue( dataSets.contains( dataSetA ) );
        assertTrue( dataSets.contains( dataSetB ) );
    }

    @Test
    public void testAddDataSetElement()
    {
        DataSet dataSetA = createDataSet( 'A');
        dataSetA.addDataSetElement(dataElementA);
        dataSetA.addDataSetElement(dataElementB);
        dataSetService.addDataSet( dataSetA );

        assertEquals( 2, dataSetA.getDataElements().size() );
        assertEquals( 1, dataElementA.getDataSetElements().size() );
        assertEquals( dataSetA, dataElementA.getDataSetElements().iterator().next().getDataSet() );
        assertEquals( 1, dataElementB.getDataSetElements().size() );
        assertEquals( dataSetA, dataElementB.getDataSetElements().iterator().next().getDataSet() );
    }

    @Test
    public void testDataSharingDataSet()
    {
        User user = createUser( 'A' );
        injectSecurityContext( user );

        DataSet dataSet = createDataSet( 'A');

        UserAccess userAccess = new UserAccess( );
        userAccess.setUser( user );
        userAccess.setAccess( AccessStringHelper.DATA_READ_WRITE  );

        dataSet.getUserAccesses().add( userAccess );

        Access access = aclService.getAccess(dataSet, user );
        assertTrue( access.getData().isRead() );
        assertTrue( access.getData().isWrite() );
    }
}
