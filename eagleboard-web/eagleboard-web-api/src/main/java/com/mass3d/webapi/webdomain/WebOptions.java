package com.mass3d.webapi.webdomain;

import java.util.Map;
import com.mass3d.common.Pager;
import com.mass3d.dxf2.common.Options;
import com.mass3d.query.Junction;

public class WebOptions
    extends Options
{
    public WebOptions( Map<String, String> options )
    {
        super( options );
    }

    //--------------------------------------------------------------------------
    // Getters for standard web options
    //--------------------------------------------------------------------------

    public boolean hasPaging()
    {
        return stringAsBoolean( options.get( "paging" ), true );
    }

    public int getPage()
    {
        return stringAsInt( options.get( "page" ), 1 );
    }

    public String getViewClass()
    {
        return stringAsString( options.get( "viewClass" ), null );
    }

    public String getViewClass( String defaultValue )
    {
        return stringAsString( options.get( "viewClass" ), defaultValue );
    }

    public int getPageSize()
    {
        return stringAsInt( options.get( "pageSize" ), Pager.DEFAULT_PAGE_SIZE );
    }

    public boolean isManage()
    {
        return stringAsBoolean( options.get( "manage" ), false );
    }

    public Junction.Type getRootJunction()
    {
        String rootJunction = options.get( "rootJunction" );
        return "OR".equals( rootJunction ) ? Junction.Type.OR : Junction.Type.AND;
    }
}
