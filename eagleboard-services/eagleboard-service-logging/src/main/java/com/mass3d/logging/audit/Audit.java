package com.mass3d.logging.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mass3d.logging.Log;

public class Audit extends Log
{
    protected AuditType auditType = AuditType.READ;

    public Audit()
    {
    }

    public Audit( AuditType auditType )
    {
        this.auditType = auditType;
    }

    @JsonProperty
    public AuditType getAuditType()
    {
        return auditType;
    }

    public void setAuditType( AuditType auditType )
    {
        this.auditType = auditType;
    }
}
