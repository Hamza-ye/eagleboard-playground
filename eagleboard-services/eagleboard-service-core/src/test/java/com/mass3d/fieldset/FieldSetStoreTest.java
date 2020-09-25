package com.mass3d.fieldset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mass3d.EagleboardSpringTest;
import com.mass3d.todotask.TodoTask;
import com.mass3d.todotask.TodoTaskService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FieldSetStoreTest
    extends EagleboardSpringTest
{
    @Autowired
    private FieldSetStore fieldSetStore;

    @Autowired
    private TodoTaskService todoTaskService;

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void assertEq( char uniqueCharacter, FieldSet fieldSet )
    {
        assertEquals( "DataSet" + uniqueCharacter, fieldSet.getName() );
        assertEquals( "DataSetShort" + uniqueCharacter, fieldSet.getShortName() );
    }
    
    // -------------------------------------------------------------------------
    // FieldSet
    // -------------------------------------------------------------------------
    
    @Test
    public void testAddFieldSet()
    {
        FieldSet dataSetA = createFieldSet( 'A');
        FieldSet dataSetB = createFieldSet( 'B');

        fieldSetStore.save( dataSetA );
        Long idA = dataSetA.getId();
        fieldSetStore.save( dataSetB );
        Long idB = dataSetB.getId();

        dataSetA = fieldSetStore.get( idA );
        dataSetB = fieldSetStore.get( idB );

        assertEquals( idA, dataSetA.getId() );
        assertEq( 'A', dataSetA );

        assertEquals( idB, dataSetB.getId() );
        assertEq( 'B', dataSetB );
    }

    @Test
    public void testUpdateFieldSet()
    {
        FieldSet fieldSet = createFieldSet( 'A');

        fieldSetStore.save( fieldSet );
        Long id = fieldSet.getId();

        fieldSet = fieldSetStore.get( id );

        assertEq( 'A', fieldSet );

        fieldSet.setName( "DataSetB" );

        fieldSetStore.update( fieldSet );

        fieldSet = fieldSetStore.get( id );

        assertEquals( fieldSet.getName(), "DataSetB" );
    }

    @Test
    public void testDeleteAndGetFieldSet()
    {
        FieldSet fieldSetA = createFieldSet( 'A');
        FieldSet fieldSetB = createFieldSet( 'B');

        fieldSetStore.save( fieldSetA );
        Long idA = fieldSetA.getId();
        fieldSetStore.save( fieldSetB );
        Long idB = fieldSetB.getId();

        assertNotNull( fieldSetStore.get( idA ) );
        assertNotNull( fieldSetStore.get( idB ) );

        fieldSetStore.delete( fieldSetStore.get( idA ) );

        assertNull( fieldSetStore.get( idA ) );
        assertNotNull( fieldSetStore.get( idB ) );
    }

    @Test
    public void testGetFieldSetByName()
    {
        FieldSet fieldSetA = createFieldSet( 'A');
        FieldSet fieldSetB = createFieldSet( 'B');

        fieldSetStore.save( fieldSetA );
        Long idA = fieldSetA.getId();
        fieldSetStore.save( fieldSetB );
        Long idB = fieldSetB.getId();

        assertEquals( fieldSetStore.getByName( "DataSetA" ).getId(), idA );
        assertEquals( fieldSetStore.getByName( "DataSetB" ).getId(), idB );
        assertNull( fieldSetStore.getByName( "DataSetC" ) );
    }

    @Test
    public void testGetAllFieldSets()
    {
        FieldSet fieldSetA = createFieldSet( 'A');
        FieldSet fieldSetB = createFieldSet( 'B');

        fieldSetStore.save( fieldSetA );
        fieldSetStore.save( fieldSetB );

        List<FieldSet> fieldSets = fieldSetStore.getAll();

        assertEquals( fieldSets.size(), 2 );
        assertTrue( fieldSets.contains( fieldSetA ) );
        assertTrue( fieldSets.contains( fieldSetB ) );
    }

    @Test
    public void testGetWithTodoTask()
    {
        TodoTask todoTaskX = createTodotask( 'X' );
        TodoTask todoTaskY = createTodotask( 'Y' );

        todoTaskService.addTodoTask( todoTaskX );
        todoTaskService.addTodoTask( todoTaskY );

        Set<TodoTask> todoTasks = new HashSet<>();
        todoTasks.add(todoTaskX);
        todoTasks.add(todoTaskY);
        FieldSet dataSetA = createFieldSet( 'A');
        FieldSet dataSetB = createFieldSet( 'B');
        FieldSet dataSetC = createFieldSet( 'C');

        dataSetA.setSources(todoTasks);

        fieldSetStore.save( dataSetA );
        fieldSetStore.save( dataSetB );
        fieldSetStore.save( dataSetC );

        List<FieldSet> fieldSetsWithTodoTasks = fieldSetStore.getFieldSetsWithTodoTasks();

        assertEquals( 1, fieldSetsWithTodoTasks.size() );
        assertEquals( dataSetA, fieldSetsWithTodoTasks.get( 0 ) );

        dataSetC.setSources( todoTasks );

        fieldSetStore.update( dataSetC );

        fieldSetsWithTodoTasks = fieldSetStore.getFieldSetsWithTodoTasks();

        assertEquals( 2, fieldSetsWithTodoTasks.size() );
        assertTrue( fieldSetsWithTodoTasks.contains( dataSetA ) );
        assertTrue( fieldSetsWithTodoTasks.contains( dataSetC ) );
    }
}
