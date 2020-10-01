package com.mass3d.schema.audit;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.schema.audit.MetadataAuditService")
@Transactional
public class DefaultMetadataAuditService implements MetadataAuditService
{
    private final MetadataAuditStore auditStore;

    public DefaultMetadataAuditService( MetadataAuditStore auditStore )
    {
        this.auditStore = auditStore;
    }

    @Override
    public void addMetadataAudit( MetadataAudit audit )
    {
        auditStore.save( audit );
    }

    @Override
    public int count( MetadataAuditQuery query )
    {
        return auditStore.count( query );
    }

    @Override
    public List<MetadataAudit> query( MetadataAuditQuery query )
    {
        return auditStore.query( query );
    }
}
