package com.mass3d.startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.system.startup.AbstractStartupRoutine;
import org.springframework.jdbc.core.JdbcTemplate;

public class TableCreator
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( TableCreator.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // StartupRoutine implementation
    // -------------------------------------------------------------------------

    @Override
    public void execute()
    {
        createSilently( "create unique index dataapproval_unique on dataapproval(datasetid,periodid,organisationunitid,attributeoptioncomboid,dataapprovallevelid)", "dataapproval_unique" );
        createSilently( "create index in_datavalueaudit on datavalueaudit(dataelementid,periodid,organisationunitid,categoryoptioncomboid,attributeoptioncomboid)", "in_datavalueaudit" );
        createSilently( "create index in_trackedentityattributevalue_attributeid on trackedentityattributevalue(trackedentityattributeid)", "in_trackedentityattributevalue_attributeid" );
    }

    private void createSilently( final String sql, final String name )
    {
        try
        {
            jdbcTemplate.execute( sql );

            log.info( "Created table/index " + name );
        }
        catch ( Exception ex )
        {
            log.debug( "Table/index " + name + " exists" );
        }
    }
}
