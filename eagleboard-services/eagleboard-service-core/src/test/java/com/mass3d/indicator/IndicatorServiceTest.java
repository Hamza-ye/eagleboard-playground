package com.mass3d.indicator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import com.mass3d.EagleboardSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @version $Id$
 */
public class IndicatorServiceTest
    extends EagleboardSpringTest
{
    @Autowired
    private IndicatorService indicatorService;

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private void assertEq( char uniqueCharacter, Indicator indicator )
    {
        assertEquals( "Indicator" + uniqueCharacter, indicator.getName() );
        assertEquals( "IndicatorShort" + uniqueCharacter, indicator.getShortName() );
        assertEquals( "IndicatorCode" + uniqueCharacter, indicator.getCode() );
        assertEquals( "IndicatorDescription" + uniqueCharacter, indicator.getDescription() );
    }

    // -------------------------------------------------------------------------
    // IndicatorType
    // -------------------------------------------------------------------------

    @Test
    public void testAddIndicatorType()
    {
        IndicatorType typeA = new IndicatorType( "IndicatorTypeA", 100, false );
        IndicatorType typeB = new IndicatorType( "IndicatorTypeB", 1, false );

        long idA = indicatorService.addIndicatorType( typeA );
        long idB = indicatorService.addIndicatorType( typeB );

        typeA = indicatorService.getIndicatorType( idA );
        assertNotNull( typeA );
        assertEquals( idA, typeA.getId() );

        typeB = indicatorService.getIndicatorType( idB );
        assertNotNull( typeB );
        assertEquals( idB, typeB.getId() );
    }

    @Test
    public void testUpdateIndicatorType()
        throws Exception
    {
        IndicatorType typeA = new IndicatorType( "IndicatorTypeA", 100, false );
        long idA = indicatorService.addIndicatorType( typeA );
        typeA = indicatorService.getIndicatorType( idA );
        assertEquals( typeA.getName(), "IndicatorTypeA" );

        typeA.setName( "IndicatorTypeB" );
        indicatorService.updateIndicatorType( typeA );
        typeA = indicatorService.getIndicatorType( idA );
        assertNotNull( typeA );
        assertEquals( typeA.getName(), "IndicatorTypeB" );
    }

    @Test
    public void testGetAndDeleteIndicatorType()
    {
        IndicatorType typeA = new IndicatorType( "IndicatorTypeA", 100, false );
        IndicatorType typeB = new IndicatorType( "IndicatorTypeB", 1, false );

        long idA = indicatorService.addIndicatorType( typeA );
        long idB = indicatorService.addIndicatorType( typeB );

        assertNotNull( indicatorService.getIndicatorType( idA ) );
        assertNotNull( indicatorService.getIndicatorType( idB ) );

        indicatorService.deleteIndicatorType( typeA );

        assertNull( indicatorService.getIndicatorType( idA ) );
        assertNotNull( indicatorService.getIndicatorType( idB ) );

        indicatorService.deleteIndicatorType( typeB );

        assertNull( indicatorService.getIndicatorType( idA ) );
        assertNull( indicatorService.getIndicatorType( idB ) );
    }

    @Test
    public void testGetAllIndicatorTypes()
    {
        IndicatorType typeA = new IndicatorType( "IndicatorTypeA", 100, false );
        IndicatorType typeB = new IndicatorType( "IndicatorTypeB", 1, false );

        indicatorService.addIndicatorType( typeA );
        indicatorService.addIndicatorType( typeB );

        List<IndicatorType> types = indicatorService.getAllIndicatorTypes();

        assertEquals( types.size(), 2 );
        assertTrue( types.contains( typeA ) );
        assertTrue( types.contains( typeB ) );
    }

    // -------------------------------------------------------------------------
    // IndicatorGroup
    // -------------------------------------------------------------------------

    @Test
    public void testAddIndicatorGroup()
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        IndicatorGroup groupB = new IndicatorGroup( "IndicatorGroupB" );

        long idA = indicatorService.addIndicatorGroup( groupA );
        long idB = indicatorService.addIndicatorGroup( groupB );

        groupA = indicatorService.getIndicatorGroup( idA );
        assertNotNull( groupA );
        assertEquals( idA, groupA.getId() );

        groupB = indicatorService.getIndicatorGroup( idB );
        assertNotNull( groupB );
        assertEquals( idB, groupB.getId() );
    }

    @Test
    public void testUpdateIndicatorGroup()
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        long idA = indicatorService.addIndicatorGroup( groupA );
        groupA = indicatorService.getIndicatorGroup( idA );
        assertEquals( groupA.getName(), "IndicatorGroupA" );

        groupA.setName( "IndicatorGroupB" );
        indicatorService.updateIndicatorGroup( groupA );
        groupA = indicatorService.getIndicatorGroup( idA );
        assertNotNull( groupA );
        assertEquals( groupA.getName(), "IndicatorGroupB" );
    }

    @Test
    public void testGetAndDeleteIndicatorGroup()
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        IndicatorGroup groupB = new IndicatorGroup( "IndicatorGroupB" );

        long idA = indicatorService.addIndicatorGroup( groupA );
        long idB = indicatorService.addIndicatorGroup( groupB );

        assertNotNull( indicatorService.getIndicatorGroup( idA ) );
        assertNotNull( indicatorService.getIndicatorGroup( idB ) );

        indicatorService.deleteIndicatorGroup( groupA );

        assertNull( indicatorService.getIndicatorGroup( idA ) );
        assertNotNull( indicatorService.getIndicatorGroup( idB ) );

        indicatorService.deleteIndicatorGroup( groupB );

        assertNull( indicatorService.getIndicatorGroup( idA ) );
        assertNull( indicatorService.getIndicatorGroup( idB ) );
    }

    @Test
    public void testGetAllIndicatorGroups()
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        IndicatorGroup groupB = new IndicatorGroup( "IndicatorGroupB" );

        indicatorService.addIndicatorGroup( groupA );
        indicatorService.addIndicatorGroup( groupB );

        List<IndicatorGroup> groups = indicatorService.getAllIndicatorGroups();

        assertEquals( groups.size(), 2 );
        assertTrue( groups.contains( groupA ) );
        assertTrue( groups.contains( groupB ) );
    }

    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    @Test
    public void testAddIndicator()
    {
        IndicatorType type = new IndicatorType( "IndicatorType", 100, false );

        indicatorService.addIndicatorType( type );

        Indicator indicatorA = createIndicator( 'A', type );
        Indicator indicatorB = createIndicator( 'B', type );

        long idA = indicatorService.addIndicator( indicatorA );
        long idB = indicatorService.addIndicator( indicatorB );

        indicatorA = indicatorService.getIndicator( idA );
        assertNotNull( indicatorA );
        assertEq( 'A', indicatorA );

        indicatorB = indicatorService.getIndicator( idB );
        assertNotNull( indicatorB );
        assertEq( 'B', indicatorB );
    }

    @Test
    public void testUpdateIndicator()
    {
        IndicatorType type = new IndicatorType( "IndicatorType", 100, false );

        indicatorService.addIndicatorType( type );

        Indicator indicatorA = createIndicator( 'A', type );
        long idA = indicatorService.addIndicator( indicatorA );
        indicatorA = indicatorService.getIndicator( idA );
        assertEq( 'A', indicatorA );

        indicatorA.setName( "IndicatorB" );
        indicatorService.updateIndicator( indicatorA );
        indicatorA = indicatorService.getIndicator( idA );
        assertNotNull( indicatorA );
        assertEquals( indicatorA.getName(), "IndicatorB" );
    }

    @Test
    public void testGetAndDeleteIndicator()
    {
        IndicatorType type = new IndicatorType( "IndicatorType", 100, false );

        indicatorService.addIndicatorType( type );

        Indicator indicatorA = createIndicator( 'A', type );
        Indicator indicatorB = createIndicator( 'B', type );

        long idA = indicatorService.addIndicator( indicatorA );
        long idB = indicatorService.addIndicator( indicatorB );

        assertNotNull( indicatorService.getIndicator( idA ) );
        assertNotNull( indicatorService.getIndicator( idB ) );

        indicatorService.deleteIndicator( indicatorA );

        assertNull( indicatorService.getIndicator( idA ) );
        assertNotNull( indicatorService.getIndicator( idB ) );

        indicatorService.deleteIndicator( indicatorB );

        assertNull( indicatorService.getIndicator( idA ) );
        assertNull( indicatorService.getIndicator( idB ) );
    }

    @Test
    public void testGetAllIndicators()
    {
        IndicatorType type = new IndicatorType( "IndicatorType", 100, false );

        indicatorService.addIndicatorType( type );

        Indicator indicatorA = createIndicator( 'A', type );
        Indicator indicatorB = createIndicator( 'B', type );

        indicatorService.addIndicator( indicatorA );
        indicatorService.addIndicator( indicatorB );

        List<Indicator> indicators = indicatorService.getAllIndicators();

        assertEquals( indicators.size(), 2 );
        assertTrue( indicators.contains( indicatorA ) );
        assertTrue( indicators.contains( indicatorB ) );
    }
}
