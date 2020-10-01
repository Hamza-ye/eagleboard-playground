package com.mass3d.schema.audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.BooleanUtils;
import com.mass3d.common.AuditType;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.Pager;
import com.mass3d.common.PagerUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class MetadataAuditQuery
{
    private List<String> klass = new ArrayList<>();

    private List<String> uid = new ArrayList<>();

    private List<String> code = new ArrayList<>();

    private Date createdAt;

    private String createdBy;

    private AuditType type;

    private Boolean skipPaging;

    private Boolean paging;

    private int page = 1;

    private int pageSize = Pager.DEFAULT_PAGE_SIZE;

    private int total;

    public MetadataAuditQuery()
    {
    }

    public MetadataAuditQuery( IdentifiableObject identifiableObject )
    {
        Assert.notNull( identifiableObject, "identifiableObject is a required parameter and can not be null." );

        klass.add( ClassUtils.getShortName( identifiableObject.getClass() ) );
        uid.add( identifiableObject.getUid() );

        if ( !StringUtils.isEmpty( identifiableObject.getCode() ) )
        {
            code.add( identifiableObject.getCode() );
        }
    }

    public List<String> getKlass()
    {
        return klass;
    }

    public void setKlass( List<String> klass )
    {
        this.klass = klass;
    }

    public List<String> getUid()
    {
        return uid;
    }

    public void setUid( List<String> uid )
    {
        this.uid = uid;
    }

    public List<String> getCode()
    {
        return code;
    }

    public void setCode( List<String> code )
    {
        this.code = code;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Date createdAt )
    {
        this.createdAt = createdAt;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }

    public AuditType getType()
    {
        return type;
    }

    public void setType( AuditType type )
    {
        this.type = type;
    }

    public boolean isSkipPaging()
    {
        return PagerUtils.isSkipPaging( skipPaging, paging );
    }

    public void setSkipPaging( Boolean skipPaging )
    {
        this.skipPaging = skipPaging;
    }

    public boolean isPaging()
    {
        return BooleanUtils.toBoolean( paging );
    }

    public void setPaging( Boolean paging )
    {
        this.paging = paging;
    }

    public int getPage()
    {
        return page;
    }

    public void setPage( int page )
    {
        this.page = page;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize( int pageSize )
    {
        this.pageSize = pageSize;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public Pager getPager()
    {
        return PagerUtils.isSkipPaging( skipPaging, paging ) ? null : new Pager( page, total, pageSize );
    }
}
