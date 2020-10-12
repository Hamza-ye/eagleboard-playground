package com.mass3d.option;

import java.util.ArrayList;
import java.util.List;
import com.mass3d.common.IdentifiableObjectStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Transactional
@Service( "com.mass3d.option.OptionService" )
public class DefaultOptionService
    implements OptionService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IdentifiableObjectStore<OptionSet> optionSetStore;

    public void setOptionSetStore( IdentifiableObjectStore<OptionSet> optionSetStore )
    {
        this.optionSetStore = optionSetStore;
    }

    private OptionStore optionStore;

    public void setOptionStore( OptionStore optionStore )
    {
        this.optionStore = optionStore;
    }

    @Autowired
    private OptionGroupStore optionGroupStore;

    @Autowired
    private OptionGroupSetStore optionGroupSetStore;

    // -------------------------------------------------------------------------
    // OptionService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Option Set
    // -------------------------------------------------------------------------

    @Override
    public long saveOptionSet( OptionSet optionSet )
    {
        optionSetStore.save( optionSet );

        return optionSet.getId();
    }

    @Override
    public void updateOptionSet( OptionSet optionSet )
    {
        optionSetStore.update( optionSet );
    }

    @Override
    public OptionSet getOptionSet( long id )
    {
        return optionSetStore.get( id );
    }

    @Override
    public OptionSet getOptionSet( String uid )
    {
        return optionSetStore.getByUid( uid );
    }

    @Override
    public OptionSet getOptionSetByName( String name )
    {
        return optionSetStore.getByName( name );
    }

    @Override
    public OptionSet getOptionSetByCode( String code )
    {
        return optionSetStore.getByCode( code );
    }

    @Override
    public void deleteOptionSet( OptionSet optionSet )
    {
        optionSetStore.delete( optionSet );
    }

    @Override
    public List<OptionSet> getAllOptionSets()
    {
        return optionSetStore.getAll();
    }

    // -------------------------------------------------------------------------
    // Option
    // -------------------------------------------------------------------------

    @Override
    public List<Option> getOptions( int optionSetId, String key, Integer max )
    {
        List<Option> options = null;

        if ( key != null || max != null )
        {
            // Use query as option set size might be very high

            options = optionStore.getOptions( optionSetId, key, max );
        }
        else
        {
            // Return all from object association to preserve custom order

            OptionSet optionSet = getOptionSet( optionSetId );

            options = new ArrayList<>( optionSet.getOptions() );
        }

        return options;
    }

    @Override
    public void updateOption( Option option )
    {
        optionStore.update( option );
    }

    @Override
    public Option getOption( long id )
    {
        return optionStore.get( id );
    }

    @Override
    public Option getOptionByCode( String code )
    {
        return optionStore.getByCode( code );
    }

    @Override
    public void deleteOption( Option option )
    {
        optionStore.delete( option );
    }

    // -------------------------------------------------------------------------
    // OptionGroup
    // -------------------------------------------------------------------------

    @Override
    public long saveOptionGroup( OptionGroup group )
    {
        optionGroupStore.save( group );

        return group.getId();
    }

    @Override
    public void updateOptionGroup( OptionGroup group )
    {
        optionGroupStore.update( group );
    }

    @Override
    public OptionGroup getOptionGroup( long id )
    {
        return optionGroupStore.get( id );
    }

    @Override
    public OptionGroup getOptionGroup( String uid )
    {
        return optionGroupStore.getByUid( uid );
    }

    @Override
    public void deleteOptionGroup( OptionGroup group )
    {
        optionGroupStore.delete( group );
    }

    @Override
    public List<OptionGroup> getAllOptionGroups()
    {
        return optionGroupStore.getAll();
    }

    // -------------------------------------------------------------------------
    // OptionGroupSet
    // -------------------------------------------------------------------------

    @Override
    public long saveOptionGroupSet( OptionGroupSet group )
    {
        optionGroupSetStore.save( group );

        return group.getId();
    }

    @Override
    public void updateOptionGroupSet( OptionGroupSet group )
    {
        optionGroupSetStore.update( group );
    }

    @Override
    public OptionGroupSet getOptionGroupSet( long id )
    {
        return optionGroupSetStore.get( id );
    }

    @Override
    public OptionGroupSet getOptionGroupSet( String uid )
    {
        return optionGroupSetStore.getByUid( uid );
    }

    @Override
    public void deleteOptionGroupSet( OptionGroupSet group )
    {
        optionGroupSetStore.delete( group );
    }

    @Override
    public List<OptionGroupSet> getAllOptionGroupSets()
    {
        return optionGroupSetStore.getAll();
    }
}
