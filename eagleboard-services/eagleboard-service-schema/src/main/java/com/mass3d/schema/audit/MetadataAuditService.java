package com.mass3d.schema.audit;

import java.util.List;

public interface MetadataAuditService
{
    /**
     * Persists the given MetadataAudit instance.
     *
     * @param audit Instance to add
     */
    void addMetadataAudit(MetadataAudit audit);

    int count(MetadataAuditQuery query);

    List<MetadataAudit> query(MetadataAuditQuery query);
}
