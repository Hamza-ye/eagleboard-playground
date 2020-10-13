package com.mass3d.period;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.system.startup.TransactionContextStartupRoutine;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.period.PeriodTypePopulator" )
public class PeriodTypePopulator
    extends TransactionContextStartupRoutine {

  private static final Log LOG = LogFactory.getLog(PeriodTypePopulator.class);

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private PeriodStore periodStore;

  public PeriodTypePopulator( PeriodStore periodStore )
  {
    checkNotNull( periodStore );
//    checkNotNull( sessionFactory );

    this.periodStore = periodStore;
//    this.sessionFactory = sessionFactory;
  }
  // -------------------------------------------------------------------------
  // Execute
  // -------------------------------------------------------------------------

  @Override
  public void executeInTransaction() {
    List<PeriodType> types = PeriodType.getAvailablePeriodTypes();

    Collection<PeriodType> storedTypes = periodStore.getAllPeriodTypes();

    types.removeAll(storedTypes);

    // ---------------------------------------------------------------------
    // Populate missing
    // ---------------------------------------------------------------------
    for (PeriodType type : types) {
      periodStore.addPeriodType(type);

      LOG.debug("Added PeriodType: " + type.getName());
    }
  }
}
