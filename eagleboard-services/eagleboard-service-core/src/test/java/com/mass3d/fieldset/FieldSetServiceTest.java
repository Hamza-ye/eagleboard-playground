package com.mass3d.fieldset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mass3d.EagleboardSpringTest;
import com.mass3d.datafield.DataField;
import com.mass3d.datafield.DataFieldService;
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


public class FieldSetServiceTest
    extends EagleboardSpringTest
{
    private DataField dataFieldA;
    private DataField dataFieldB;

    private TodoTask unitA;
    private TodoTask unitB;
    private TodoTask unitC;
    private TodoTask unitD;
    private TodoTask unitE;
    private TodoTask unitF;

    private CurrentUserService mockCurrentUserService;

    @Autowired
    private FieldSetService fieldSetService;

    @Autowired
    private DataFieldService dataFieldService;

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

        dataFieldA = createDataField( 'A' );
        dataFieldB = createDataField( 'B' );

        dataFieldService.addDataField(dataFieldA);
        dataFieldService.addDataField(dataFieldB);

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

    private void assertEq( char uniqueCharacter, FieldSet fieldSet )
    {
        assertEquals( "DataSet" + uniqueCharacter, fieldSet.getName() );
        assertEquals( "DataSetShort" + uniqueCharacter, fieldSet.getShortName() );
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataSet()
    {
        FieldSet dataSetA = createFieldSet( 'A');
        FieldSet dataSetB = createFieldSet( 'B');

        dataSetA.addFieldSetField(dataFieldA);
        dataSetA.addFieldSetField(dataFieldB);

        Long idA = fieldSetService.addFieldSet( dataSetA );
        Long idB = fieldSetService.addFieldSet( dataSetB );

        dataSetA = fieldSetService.getFieldSet( idA );
        dataSetB = fieldSetService.getFieldSet( idB );

        assertEquals( idA, dataSetA.getId() );
        assertEq( 'A', dataSetA );

        assertEquals( idB, dataSetB.getId() );
        assertEq( 'B', dataSetB );
    }

    @Test
    public void testUpdateFieldSet()
    {
        FieldSet fieldSet = createFieldSet( 'A');

        fieldSet.addFieldSetField(dataFieldA);
        fieldSet.addFieldSetField(dataFieldB);

        Long id = fieldSetService.addFieldSet( fieldSet );

        fieldSet = fieldSetService.getFieldSet( id );

        assertEq( 'A', fieldSet );

        fieldSet.setName( "DataSetB" );

        fieldSetService.updateFieldSet( fieldSet );

        fieldSet = fieldSetService.getFieldSet( id );

        assertEquals( fieldSet.getName(), "DataSetB" );
    }

    @Test
    public void testDeleteAndGetDataSet()
    {
        FieldSet fieldSetA = createFieldSet( 'A');
        FieldSet fieldSetB = createFieldSet( 'B');

        fieldSetA.addFieldSetField(dataFieldA);
        fieldSetA.addFieldSetField(dataFieldB);

        Long idA = fieldSetService.addFieldSet( fieldSetA );
        Long idB = fieldSetService.addFieldSet( fieldSetB );

        assertNotNull( fieldSetService.getFieldSet( idA ) );
        assertNotNull( fieldSetService.getFieldSet( idB ) );

        fieldSetService.deleteFieldSet( fieldSetService.getFieldSet( idA ) );

        assertNull( fieldSetService.getFieldSet( idA ) );
        assertNotNull( fieldSetService.getFieldSet( idB ) );

        fieldSetService.deleteFieldSet( fieldSetService.getFieldSet( idB ) );

        assertNull( fieldSetService.getFieldSet( idA ) );
        assertNull( fieldSetService.getFieldSet( idB ) );
    }

    @Test
    public void testUpdateRemoveDataSetElements()
    {
        FieldSet dataSet = createFieldSet( 'A');

        dataSet.addFieldSetField(dataFieldA);
        dataSet.addFieldSetField(dataFieldB);

        fieldSetService.addFieldSet( dataSet );

        dataSet = fieldSetService.getFieldSet( dataSet.getId() );
        assertNotNull( dataSet );
        List<FieldSetField> dataSetElements = new ArrayList<>( dataSet.getFieldSetFields() );

        assertEquals( 2, dataSet.getFieldSetFields().size() );
        assertEquals( 2, dataSetElements.size() );

        // Remove data element A

        dataSet.removeFieldSetField(dataFieldA);

        fieldSetService.updateFieldSet( dataSet );

        dataSet = fieldSetService.getFieldSet( dataSet.getId() );
        assertNotNull( dataSet );
        dataSetElements = new ArrayList<>( dataSet.getFieldSetFields() );

        assertEquals( 1, dataSet.getFieldSetFields().size() );
        assertEquals( 1, dataSetElements.size() );

        // Remove data element B

        dataSet.removeFieldSetField(dataFieldB);

        fieldSetService.updateFieldSet( dataSet );

        dataSet = fieldSetService.getFieldSet( dataSet.getId() );
        assertNotNull( dataSet );
        dataSetElements = new ArrayList<>( dataSet.getFieldSetFields() );

        assertEquals( 0, dataSet.getFieldSetFields().size() );
        assertEquals( 0, dataSetElements.size() );
    }

    @Test
    public void testDeleteRemoveFieldSetFields()
    {
        FieldSet fieldSet = createFieldSet( 'A');

        fieldSet.addFieldSetField(dataFieldA);
        fieldSet.addFieldSetField(dataFieldB);

        Long ds = fieldSetService.addFieldSet( fieldSet );

        fieldSet = fieldSetService.getFieldSet( fieldSet.getId() );
        assertNotNull( fieldSet );
        List<FieldSetField> dataSetElements = new ArrayList<>( fieldSet.getFieldSetFields() );

        assertEquals( fieldSet, fieldSetService.getFieldSet( ds ) );
        assertEquals( 2, fieldSet.getFieldSetFields().size() );
        assertEquals( 2, dataSetElements.size() );

        fieldSetService.deleteFieldSet( fieldSet );

        assertNull( fieldSetService.getFieldSet( ds ) );
    }

    @Test
    public void testGetAllDataSets()
    {
        FieldSet dataSetA = createFieldSet( 'A');
        FieldSet dataSetB = createFieldSet( 'B');

        fieldSetService.addFieldSet( dataSetA );
        fieldSetService.addFieldSet( dataSetB );

        List<FieldSet> dataSets = fieldSetService.getAllFieldSets();

        assertEquals( dataSets.size(), 2 );
        assertTrue( dataSets.contains( dataSetA ) );
        assertTrue( dataSets.contains( dataSetB ) );
    }

    @Test
    public void testAddFieldSetField()
    {
        FieldSet dataSetA = createFieldSet( 'A');
        dataSetA.addFieldSetField(dataFieldA);
        dataSetA.addFieldSetField(dataFieldB);
        fieldSetService.addFieldSet( dataSetA );

        assertEquals( 2, dataSetA.getFieldSetFields().size() );
        assertEquals( 1, dataFieldA.getFieldSetFields().size() );
        assertEquals( dataSetA, dataFieldA.getFieldSetFields().iterator().next().getFieldSet() );
        assertEquals( 1, dataFieldB.getFieldSetFields().size() );
        assertEquals( dataSetA, dataFieldB.getFieldSetFields().iterator().next().getFieldSet() );
    }

    @Test
    public void testDataSharingDataSet()
    {
        User user = createUser( 'A' );
        injectSecurityContext( user );

        FieldSet fieldSet = createFieldSet( 'A');

        UserAccess userAccess = new UserAccess( );
        userAccess.setUser( user );
        userAccess.setAccess( AccessStringHelper.DATA_READ_WRITE  );

        fieldSet.getUserAccesses().add( userAccess );

        Access access = aclService.getAccess( fieldSet, user );
        assertTrue( access.getData().isRead() );
        assertTrue( access.getData().isWrite() );
    }
}
