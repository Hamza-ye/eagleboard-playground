package com.mass3d.dxf2.metadata.objectbundle.hooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.dxf2.metadata.objectbundle.ObjectBundle;
import com.mass3d.dataset.DataInputPeriod;
import com.mass3d.dataset.DataSet;
import com.mass3d.feedback.ErrorCode;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.util.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class FieldSetObjectBundleHook extends AbstractObjectBundleHook
{
    @Override
    public <T extends IdentifiableObject> List<ErrorReport> validate( T object, ObjectBundle bundle )
    {
        if ( object == null || !object.getClass().isAssignableFrom( DataSet.class ) )
        {
            return new ArrayList<>();
        }

        List<ErrorReport> errors = new ArrayList<>();

        DataSet dataSet = (DataSet) object;

        Set<DataInputPeriod> inputPeriods = dataSet.getDataInputPeriods();

        if ( inputPeriods.size() > 0 )
        {
            for ( DataInputPeriod period : inputPeriods )
            {
                if ( ObjectUtils.allNonNull( period.getOpeningDate(), period.getClosingDate() ) && period.getOpeningDate().after( period.getClosingDate() ) )
                {
                    errors.add( new ErrorReport( DataSet.class, ErrorCode.E4013, period.getClosingDate(), period.getOpeningDate() ) );
                }
            }
        }
        return errors;
    }
}