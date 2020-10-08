package com.mass3d.dxf2.metadata.objectbundle.hooks;

import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.dxf2.metadata.objectbundle.ObjectBundle;
import com.mass3d.schema.Schema;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order( 0 )
public class IdentifiableObjectBundleHook extends AbstractObjectBundleHook
{
    @Override
    public void preCreate( IdentifiableObject identifiableObject, ObjectBundle bundle )
    {
        ((BaseIdentifiableObject) identifiableObject).setAutoFields();

        Schema schema = schemaService.getDynamicSchema( identifiableObject.getClass() );
//        handleAttributeValues( identifiableObject, bundle, schema );
    }

    @Override
    public void preUpdate( IdentifiableObject object, IdentifiableObject persistedObject, ObjectBundle bundle )
    {
        BaseIdentifiableObject identifiableObject = (BaseIdentifiableObject) object;
        identifiableObject.setAutoFields();
        identifiableObject.setLastUpdatedBy( bundle.getUser() );

        Schema schema = schemaService.getDynamicSchema( object.getClass() );
//        handleAttributeValues( object, bundle, schema );
    }

//    private void handleAttributeValues( IdentifiableObject identifiableObject, ObjectBundle bundle, Schema schema )
//    {
//        Session session = sessionFactory.getCurrentSession();
//
//        if ( !schema.havePersistedProperty( "attributeValues" ) ) return;
//
//        Iterator<AttributeValue> iterator = identifiableObject.getAttributeValues().iterator();
//
//        while ( iterator.hasNext() )
//        {
//            AttributeValue attributeValue = iterator.next();
//
//            // if value null or empty, just skip it
//            if ( StringUtils.isEmpty( attributeValue.getValue() ) )
//            {
//                iterator.remove();
//                continue;
//            }
//
//            Attribute attribute = bundle.getPreheat().get( bundle.getPreheatIdentifier(), attributeValue.getAttribute() );
//
//            if ( attribute == null )
//            {
//                iterator.remove();
//                continue;
//            }
//
//            attributeValue.setAttribute( attribute );
//            session.save( attributeValue );
//        }
//    }
}
