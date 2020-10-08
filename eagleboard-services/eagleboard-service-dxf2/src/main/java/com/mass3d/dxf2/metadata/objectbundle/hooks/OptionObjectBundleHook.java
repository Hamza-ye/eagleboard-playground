package com.mass3d.dxf2.metadata.objectbundle.hooks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.dxf2.metadata.objectbundle.ObjectBundle;
import com.mass3d.option.Option;
import com.mass3d.option.OptionSet;
import org.springframework.stereotype.Component;

@Component
public class OptionObjectBundleHook
    extends AbstractObjectBundleHook
{
    private static final Log log = LogFactory.getLog( OptionObjectBundleHook.class );

    @Override
    public <T extends IdentifiableObject> void preCreate( T object, ObjectBundle bundle )
    {
        if ( !( object instanceof Option) )
        {
            return;
        }

        final Option option = (Option) object;
        // if the bundle contains also the option set there is no need to add the option here
        // (will be done automatically later and option set may contain raw value already)
        if ( option.getOptionSet() != null && !bundle.containsObject( option.getOptionSet() ) )
        {
            OptionSet optionSet = bundle.getPreheat().get( bundle.getPreheatIdentifier(), OptionSet.class, option.getOptionSet() );
            if ( optionSet != null )
            {
                optionSet.addOption( option );
            }
        }
    }
}