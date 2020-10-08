package com.mass3d.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mass3d.common.IdentifiableObjectStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.constant.ConstantService")
@Transactional
public class DefaultConstantService
    implements ConstantService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IdentifiableObjectStore<Constant> constantStore;

    public void setConstantStore( IdentifiableObjectStore<Constant> constantStore )
    {
        this.constantStore = constantStore;
    }

    // -------------------------------------------------------------------------
    // Constant
    // -------------------------------------------------------------------------

    @Override
    public Long saveConstant( Constant constant )
    {
        constantStore.save( constant );
        return constant.getId();
    }

    @Override
    public void updateConstant( Constant constant )
    {
        constantStore.update( constant );
    }

    @Override
    public void deleteConstant( Constant constant )
    {
        constantStore.delete( constant );
    }

    @Override
    public Constant getConstant( Long constantId )
    {
        return constantStore.get( constantId );
    }

    @Override
    public Constant getConstant( String uid )
    {
        return constantStore.getByUid( uid );
    }
    
    @Override
    public List<Constant> getAllConstants()
    {
        return constantStore.getAll();
    }
    
    @Override
    public Map<String, Double> getConstantMap()
    {
        Map<String, Double> map = new HashMap<>();
        
        for ( Constant constant : getAllConstants() )
        {
            map.put( constant.getUid(), constant.getValue() );
        }
        
        return map;
    }
    
    @Override
    public Map<String, Double> getConstantParameterMap()
    {
        Map<String, Double> map = new HashMap<>();
        
        for ( Constant constant : getAllConstants() )
        {
            map.put( constant.getName(), constant.getValue() );
        }
        
        return map;
    }

    // -------------------------------------------------------------------------
    // Constant expanding
    // -------------------------------------------------------------------------
    
    @Override
    public int getConstantCount()
    {
        return constantStore.getCount();
    }

    @Override
    public int getConstantCountByName( String name )
    {
        return constantStore.getCountLikeName( name );
    }

    @Override
    public List<Constant> getConstantsBetween( int first, int max )
    {
        return constantStore.getAllOrderedName( first, max );
    }

    @Override
    public List<Constant> getConstantsBetweenByName( String name, int first, int max )
    {
        return constantStore.getAllLikeName( name, first, max );
    }
}