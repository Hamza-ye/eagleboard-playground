package com.mass3d.dxf2.metadata.objectbundle.hooks;

import java.util.Collection;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.dxf2.metadata.objectbundle.ObjectBundle;
import com.mass3d.period.PeriodType;
import com.mass3d.schema.Property;
import com.mass3d.schema.Schema;
import com.mass3d.system.util.ReflectionUtils;
import org.springframework.stereotype.Component;

@Component
public class EmbeddedObjectObjectBundleHook
    extends AbstractObjectBundleHook
{
    @Override
    public <T extends IdentifiableObject> void preCreate( T object, ObjectBundle bundle )
    {
        Schema schema = schemaService.getDynamicSchema( object.getClass() );

        if ( schema == null || schema.getEmbeddedObjectProperties().isEmpty() )
        {
            return;
        }

        handleEmbeddedObjects( object, bundle, schema.getEmbeddedObjectProperties().values() );
    }

    @Override
    public <T extends IdentifiableObject> void preUpdate( T object, T persistedObject, ObjectBundle bundle )
    {
        Schema schema = schemaService.getDynamicSchema( object.getClass() );

        if ( schema == null || schema.getEmbeddedObjectProperties().isEmpty() )
        {
            return;
        }

        Collection<Property> properties = schema.getEmbeddedObjectProperties().values();


        clearEmbeddedObjects( persistedObject, bundle, properties );
        handleEmbeddedObjects( object, bundle, properties );
    }

    private <T extends IdentifiableObject> void clearEmbeddedObjects( T object, ObjectBundle bundle, Collection<Property> properties )
    {
        for ( Property property : properties )
        {
            if ( property.isCollection() )
            {
                if ( ReflectionUtils.isSharingProperty( property ) && bundle.isSkipSharing() )
                {
                    continue;
                }

                ( ( Collection<?> ) ReflectionUtils.invokeMethod( object, property.getGetterMethod() ) ).clear();
            }
            else
            {
                ReflectionUtils.invokeMethod( object, property.getSetterMethod(), ( Object ) null );
            }
        }
    }

    private <T extends IdentifiableObject> void handleEmbeddedObjects( T object, ObjectBundle bundle, Collection<Property> properties )
    {
        for ( Property property : properties )
        {
            if ( property.isCollection() )
            {
                Collection<?> objects = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );
                objects.forEach( o ->
                {
                    handleProperty( o, bundle, property );
                } );
            }
            else
            {
                Object o = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );

                handleProperty( o, bundle, property );
            }
        }
    }
    
    private void handleProperty( Object o, ObjectBundle bundle, Property property )
    {
        if ( property.isIdentifiableObject() )
        {
            ((BaseIdentifiableObject) o).setAutoFields();
        }
        
        Schema embeddedSchema = schemaService.getDynamicSchema( o.getClass() );
        for ( Property embeddedProperty : embeddedSchema.getPropertyMap().values() )
        {
            if ( PeriodType.class.isAssignableFrom( embeddedProperty.getKlass() ) )
            {
                PeriodType periodType = ReflectionUtils.invokeMethod( o, embeddedProperty.getGetterMethod() );
    
                if ( periodType != null )
                {
                    periodType = bundle.getPreheat().getPeriodTypeMap().get( periodType.getName() );
                    ReflectionUtils.invokeMethod( o, embeddedProperty.getSetterMethod(), periodType );
                }
            }
        }

        preheatService.connectReferences( o, bundle.getPreheat(), bundle.getPreheatIdentifier() );
    }
}
