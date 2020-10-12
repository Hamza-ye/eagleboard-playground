package com.mass3d.indicator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import com.mass3d.dataelement.DataElement;
import com.mass3d.dataset.DataSet;
import com.mass3d.expression.ExpressionService;
import com.mass3d.system.deletion.DeletionHandler;
import org.springframework.stereotype.Component;

/**
 * @version $Id$
 */
@Component( "com.mass3d.indicator.IndicatorDeletionHandler" )
public class IndicatorDeletionHandler
    extends DeletionHandler {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private final IndicatorService indicatorService;

  private final ExpressionService expressionService;

  public IndicatorDeletionHandler( IndicatorService indicatorService, ExpressionService expressionService )
  {
    checkNotNull( indicatorService );
    checkNotNull( expressionService );

    this.indicatorService = indicatorService;
    this.expressionService = expressionService;
  }

  // -------------------------------------------------------------------------
  // DeletionHandler implementation
  // -------------------------------------------------------------------------

  @Override
  public String getClassName() {
    return Indicator.class.getSimpleName();
  }

  @Override
  public String allowDeleteIndicatorType(IndicatorType indicatorType) {
    for (Indicator indicator : indicatorService.getAllIndicators()) {
      if (indicator.getIndicatorType().equals(indicatorType)) {
        return indicator.getName();
      }
    }

    return null;
  }

  @Override
  public void deleteIndicatorGroup(IndicatorGroup group) {
    for (Indicator indicator : group.getMembers()) {
      indicator.getGroups().remove(group);
      indicatorService.updateIndicator(indicator);
    }
  }

  @Override
  public void deleteFieldSet(DataSet fieldSet) {
    for (Indicator indicator : fieldSet.getIndicators()) {
      indicator.getDataSets().remove(fieldSet);
      indicatorService.updateIndicator(indicator);
    }
  }

  @Override
  public String allowDeleteDataField(DataElement dataElement) {
    for (Indicator indicator : indicatorService.getAllIndicators()) {
      // Todo Eagle modified form DataElement to DataField
      Set<DataElement> daels = expressionService.getDataFieldsInExpression(indicator.getNumerator());

      if (daels != null && daels.contains(dataElement)) {
        return indicator.getName();
      }
      // Todo Eagle modified form DataElement to DataField
      daels = expressionService.getDataFieldsInExpression(indicator.getDenominator());

      if (daels != null && daels.contains(dataElement)) {
        return indicator.getName();
      }
    }

    return null;
  }
}
