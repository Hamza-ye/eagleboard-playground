package com.mass3d.schema.audit;

import java.util.List;

public interface MetadataAuditStore
{
    int save(MetadataAudit audit);

    void delete(MetadataAudit audit);

    int count(MetadataAuditQuery query);

    List<MetadataAudit> query(MetadataAuditQuery query);
}
