package com.mass3d.dataset;

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

public class DataSetStoreTest
    extends EagleboardSpringTest
{
    @Autowired
    private DataSetStore dataSetStore;

    @Autowired
    private TodoTaskService todoTaskService;

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

        dataSetStore.save( dataSetA );
        long idA = dataSetA.getId();
        dataSetStore.save( dataSetB );
        long idB = dataSetB.getId();

        dataSetA = dataSetStore.get( idA );
        dataSetB = dataSetStore.get( idB );

        assertEquals( idA, dataSetA.getId() );
        assertEq( 'A', dataSetA );

        assertEquals( idB, dataSetB.getId() );
        assertEq( 'B', dataSetB );
    }

    @Test
    public void testUpdateDataSet()
    {
        DataSet dataSet = createDataSet( 'A');

        dataSetStore.save(dataSet);
        long id = dataSet.getId();

        dataSet = dataSetStore.get( id );

        assertEq( 'A', dataSet);

        dataSet.setName( "DataSetB" );

        dataSetStore.update(dataSet);

        dataSet = dataSetStore.get( id );

        assertEquals( dataSet.getName(), "DataSetB" );
    }

    @Test
    public void testDeleteAndGetDataSet()
    {
        DataSet dataSetA = createDataSet( 'A');
        DataSet dataSetB = createDataSet( 'B');

        dataSetStore.save(dataSetA);
        long idA = dataSetA.getId();
        dataSetStore.save(dataSetB);
        long idB = dataSetB.getId();

        assertNotNull( dataSetStore.get( idA ) );
        assertNotNull( dataSetStore.get( idB ) );

        dataSetStore.delete( dataSetStore.get( idA ) );

        assertNull( dataSetStore.get( idA ) );
        assertNotNull( dataSetStore.get( idB ) );
    }

    @Test
    public void testGetDataSetByName()
    {
        DataSet dataSetA = createDataSet( 'A');
        DataSet dataSetB = createDataSet( 'B');

        dataSetStore.save(dataSetA);
        long idA = dataSetA.getId();
        dataSetStore.save(dataSetB);
        long idB = dataSetB.getId();

        assertEquals( dataSetStore.getByName( "DataSetA" ).getId(), idA );
        assertEquals( dataSetStore.getByName( "DataSetB" ).getId(), idB );
        assertNull( dataSetStore.getByName( "DataSetC" ) );
    }

    @Test
    public void testGetAllDataSets()
    {
        DataSet dataSetA = createDataSet( 'A');
        DataSet dataSetB = createDataSet( 'B');

        dataSetStore.save(dataSetA);
        dataSetStore.save(dataSetB);

        List<DataSet> dataSets = dataSetStore.getAll();

        assertEquals( dataSets.size(), 2 );
        assertTrue( dataSets.contains(dataSetA) );
        assertTrue( dataSets.contains(dataSetB) );
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
        DataSet dataSetA = createDataSet( 'A');
        DataSet dataSetB = createDataSet( 'B');
        DataSet dataSetC = createDataSet( 'C');

        dataSetA.setSources(todoTasks);

        dataSetStore.save( dataSetA );
        dataSetStore.save( dataSetB );
        dataSetStore.save( dataSetC );

        List<DataSet> dataSetsWithTodoTasks = dataSetStore.getDataSetsWithTodoTasks();

        assertEquals( 1, dataSetsWithTodoTasks.size() );
        assertEquals( dataSetA, dataSetsWithTodoTasks.get( 0 ) );

        dataSetC.setSources( todoTasks );

        dataSetStore.update( dataSetC );

        dataSetsWithTodoTasks = dataSetStore.getDataSetsWithTodoTasks();

        assertEquals( 2, dataSetsWithTodoTasks.size() );
        assertTrue( dataSetsWithTodoTasks.contains( dataSetA ) );
        assertTrue( dataSetsWithTodoTasks.contains( dataSetC ) );
    }
}
