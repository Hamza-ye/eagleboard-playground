package com.mass3d.hibernate.dialect;

import java.sql.Types;
import org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect;

public class DhisPostgresDialect
    extends PostgisPG95Dialect
{
    public DhisPostgresDialect()
    {
        registerColumnType( Types.JAVA_OBJECT, "jsonb" );
        registerHibernateType( Types.OTHER, "pg-uuid" );
    }
}