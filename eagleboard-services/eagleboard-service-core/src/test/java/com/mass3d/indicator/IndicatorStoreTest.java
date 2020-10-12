package com.mass3d.indicator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mass3d.EagleboardSpringTest;
import com.mass3d.common.IdentifiableObjectManager;
import com.mass3d.common.IdentifiableObjectStore;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @version $Id: IndicatorStoreTest.java 3286 2007-05-07 18:05:21Z larshelg $
 */
public class IndicatorStoreTest
    extends EagleboardSpringTest {

  @Autowired
  private IndicatorStore indicatorStore;

  @Autowired
  private IdentifiableObjectManager idObjectManager;

  //    @Resource(name="com.mass3d.indicator.IndicatorTypeStore")
//    private IdentifiableObjectStore<IndicatorType> indicatorTypeStore;
  @Autowired
  @Qualifier("com.mass3d.indicator.IndicatorTypeStore")
  private IdentifiableObjectStore<IndicatorType> indicatorTypeStore;

  // -------------------------------------------------------------------------
  // Support methods
  // -------------------------------------------------------------------------

  private void assertEq(char uniqueCharacter, Indicator indicator) {
    assertEquals("Indicator" + uniqueCharacter, indicator.getName());
    assertEquals("IndicatorShort" + uniqueCharacter, indicator.getShortName());
    assertEquals("IndicatorCode" + uniqueCharacter, indicator.getCode());
    assertEquals("IndicatorDescription" + uniqueCharacter, indicator.getDescription());
  }

  // -------------------------------------------------------------------------
  // IndicatorType
  // -------------------------------------------------------------------------

  @Test
  public void testAddIndicatorType() {
    IndicatorType typeA = new IndicatorType("IndicatorTypeA", 100, false);
    IndicatorType typeB = new IndicatorType("IndicatorTypeB", 1, false);

    indicatorTypeStore.save(typeA);
    long idA = typeA.getId();
    indicatorTypeStore.save(typeB);
    long idB = typeB.getId();

    typeA = indicatorTypeStore.get(idA);
    assertNotNull(typeA);
    assertEquals(idA, typeA.getId());

    typeB = indicatorTypeStore.get(idB);
    assertNotNull(typeB);
    assertEquals(idB, typeB.getId());
  }

  @Test
  public void testUpdateIndicatorType() {
    IndicatorType typeA = new IndicatorType("IndicatorTypeA", 100, false);
    indicatorTypeStore.save(typeA);
    long idA = typeA.getId();
    typeA = indicatorTypeStore.get(idA);
    assertEquals(typeA.getName(), "IndicatorTypeA");

    typeA.setName("IndicatorTypeB");
    indicatorTypeStore.update(typeA);
    typeA = indicatorTypeStore.get(idA);
    assertNotNull(typeA);
    assertEquals(typeA.getName(), "IndicatorTypeB");
  }

  @Test
  public void testGetAndDeleteIndicatorType() {
    IndicatorType typeA = new IndicatorType("IndicatorTypeA", 100, false);
    IndicatorType typeB = new IndicatorType("IndicatorTypeB", 1, false);

    indicatorTypeStore.save(typeA);
    long idA = typeA.getId();
    indicatorTypeStore.save(typeB);
    long idB = typeB.getId();

    assertNotNull(indicatorTypeStore.get(idA));
    assertNotNull(indicatorTypeStore.get(idB));

    indicatorTypeStore.delete(typeA);

    assertNull(indicatorTypeStore.get(idA));
    assertNotNull(indicatorTypeStore.get(idB));

    indicatorTypeStore.delete(typeB);

    assertNull(indicatorTypeStore.get(idA));
    assertNull(indicatorTypeStore.get(idB));
  }

  @Test
  public void testGetAllIndicatorTypes() {
    IndicatorType typeA = new IndicatorType("IndicatorTypeA", 100, false);
    IndicatorType typeB = new IndicatorType("IndicatorTypeB", 1, false);

    indicatorTypeStore.save(typeA);
    indicatorTypeStore.save(typeB);

    List<IndicatorType> types = indicatorTypeStore.getAll();

    assertEquals(types.size(), 2);
    assertTrue(types.contains(typeA));
    assertTrue(types.contains(typeB));
  }

  @Test
  public void testGetIndicatorTypeByName() {
    IndicatorType typeA = new IndicatorType("IndicatorTypeA", 100, false);
    IndicatorType typeB = new IndicatorType("IndicatorTypeB", 1, false);

    indicatorTypeStore.save(typeA);
    long idA = typeA.getId();
    indicatorTypeStore.save(typeB);
    long idB = typeB.getId();

    assertNotNull(indicatorTypeStore.get(idA));
    assertNotNull(indicatorTypeStore.get(idB));

    typeA = indicatorTypeStore.getByName("IndicatorTypeA");
    assertNotNull(typeA);
    assertEquals(typeA.getId(), idA);

    IndicatorType typeC = indicatorTypeStore.getByName("IndicatorTypeC");
    assertNull(typeC);
  }

  // -------------------------------------------------------------------------
  // Indicator
  // -------------------------------------------------------------------------

  @Test
  public void testAddIndicator() {
    IndicatorType type = new IndicatorType("IndicatorType", 100, false);

    indicatorTypeStore.save(type);

    Indicator indicatorA = createIndicator('A', type);
    Indicator indicatorB = createIndicator('B', type);

    indicatorStore.save(indicatorA);
    long idA = indicatorA.getId();
    indicatorStore.save(indicatorB);
    long idB = indicatorB.getId();

    indicatorA = indicatorStore.get(idA);
    assertNotNull(indicatorA);
    assertEq('A', indicatorA);

    indicatorB = indicatorStore.get(idB);
    assertNotNull(indicatorB);
    assertEq('B', indicatorB);
  }

  @Test
  public void testUpdateIndicator() {
    IndicatorType type = new IndicatorType("IndicatorType", 100, false);

    indicatorTypeStore.save(type);

    Indicator indicatorA = createIndicator('A', type);
    indicatorStore.save(indicatorA);
    long idA = indicatorA.getId();
    indicatorA = indicatorStore.get(idA);
    assertEq('A', indicatorA);

    indicatorA.setName("IndicatorB");
    indicatorStore.update(indicatorA);
    indicatorA = indicatorStore.get(idA);
    assertNotNull(indicatorA);
    assertEquals(indicatorA.getName(), "IndicatorB");
  }

  @Test
  public void testGetAndDeleteIndicator() {
    IndicatorType type = new IndicatorType("IndicatorType", 100, false);

    indicatorTypeStore.save(type);

    Indicator indicatorA = createIndicator('A', type);
    Indicator indicatorB = createIndicator('B', type);
    Indicator indicatorC = createIndicator('C', type);

    indicatorStore.save(indicatorA);
    long idA = indicatorA.getId();
    indicatorStore.save(indicatorB);
    long idB = indicatorB.getId();
    indicatorStore.save(indicatorC);
    long idC = indicatorC.getId();

    assertNotNull(indicatorStore.get(idA));
    assertNotNull(indicatorStore.get(idB));
    assertNotNull(indicatorStore.get(idC));

    indicatorStore.delete(indicatorA);
    indicatorStore.delete(indicatorC);

    assertNull(indicatorStore.get(idA));
    assertNotNull(indicatorStore.get(idB));
    assertNull(indicatorStore.get(idC));
  }

  @Test
  public void testGetAllIndicators() {
    IndicatorType type = new IndicatorType("IndicatorType", 100, false);

    indicatorTypeStore.save(type);

    Indicator indicatorA = createIndicator('A', type);
    Indicator indicatorB = createIndicator('B', type);

    indicatorStore.save(indicatorA);
    indicatorStore.save(indicatorB);

    List<Indicator> indicators = indicatorStore.getAll();

    assertEquals(indicators.size(), 2);
    assertTrue(indicators.contains(indicatorA));
    assertTrue(indicators.contains(indicatorB));
  }

  @Test
  public void testGetIndicatorByName() {
    IndicatorType type = new IndicatorType("IndicatorType", 100, false);

    indicatorTypeStore.save(type);

    Indicator indicatorA = createIndicator('A', type);
    Indicator indicatorB = createIndicator('B', type);

    indicatorStore.save(indicatorA);
    long idA = indicatorA.getId();
    indicatorStore.save(indicatorB);
    long idB = indicatorB.getId();

    assertNotNull(indicatorStore.get(idA));
    assertNotNull(indicatorStore.get(idB));

    indicatorA = indicatorStore.getByName("IndicatorA");
    assertNotNull(indicatorA);
    assertEq('A', indicatorA);

    Indicator indicatorC = indicatorStore.getByName("IndicatorC");
    assertNull(indicatorC);
  }

  @Test
  public void testGetIndicatorsWithoutGroups() {
    IndicatorType type = new IndicatorType("IndicatorType", 100, false);

    indicatorTypeStore.save(type);

    Indicator indicatorA = createIndicator('A', type);
    Indicator indicatorB = createIndicator('B', type);
    Indicator indicatorC = createIndicator('C', type);

    indicatorStore.save(indicatorA);
    indicatorStore.save(indicatorB);
    indicatorStore.save(indicatorC);

    IndicatorGroup igA = createIndicatorGroup('A');

    igA.addIndicator(indicatorA);
    igA.addIndicator(indicatorB);

    IndicatorGroupSet indicatorGroupSet1 = igA.getGroupSet();

    idObjectManager.save( igA );

    IndicatorGroupSet indicatorGroupSet2 = igA.getGroupSet();

    List<Indicator> indicators = indicatorStore.getIndicatorsWithoutGroups();

    assertEquals(1, indicators.size());
    assertTrue(indicators.contains(indicatorC));
  }
}
