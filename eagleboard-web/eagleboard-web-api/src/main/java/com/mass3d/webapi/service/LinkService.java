package com.mass3d.webapi.service;

import java.util.List;
import com.mass3d.common.Pager;
import com.mass3d.schema.Schema;

public interface LinkService
{
    /**
     * Generate next/prev links for Pager class. Needs to know which class we are generating
     * the pager for, so it can fetch the endpoint.
     *
     * @param pager Pager instance to update with prev/next links
     * @param klass Class type which is paged
     * @see Pager
     */
    void generatePagerLinks(Pager pager, Class<?> klass);

    /**
     * Generate HREF and set it using reflection, required a setHref(String) method in your class.
     * <p/>
     * Uses hrefBase from ContextService.getServletPath().
     *
     * @param object   Object (can be collection) to set HREFs on
     * @param deepScan Generate links also on deeper levels (only one level down)
     * @see javax.servlet.http.HttpServletRequest
     * @see ContextService
     */
    <T> void generateLinks(T object, boolean deepScan);

    /**
     * Generate HREF and set it using reflection, required a setHref(String) method in your class.
     *
     * @param object   Object (can be collection) to set HREFs on
     * @param hrefBase Used as starting point of HREF
     * @param deepScan Generate links also on deeper levels (only one level down)
     * @see javax.servlet.http.HttpServletRequest
     */
    <T> void generateLinks(T object, String hrefBase, boolean deepScan);

    void generateSchemaLinks(List<Schema> schemas);

    void generateSchemaLinks(Schema schema);

    void generateSchemaLinks(Schema schema, String hrefBase);
}
