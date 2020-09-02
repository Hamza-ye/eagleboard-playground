package com.mass3d.services.schema.descriptors;

import com.google.common.collect.Lists;
import com.mass3d.api.fieldset.FieldSet;
import com.mass3d.api.security.Authority;
import com.mass3d.api.security.AuthorityType;
import com.mass3d.services.schema.Schema;
import com.mass3d.services.schema.SchemaDescriptor;

public class FieldSetSchemaDescriptor implements SchemaDescriptor {

  public static final String SINGULAR = "fieldSet";

  public static final String PLURAL = "fieldSets";

  public static final String API_ENDPOINT = "/" + PLURAL;

  @Override
  public Schema getSchema() {
    Schema schema = new Schema(FieldSet.class, SINGULAR, PLURAL);
    schema.setRelativeApiEndpoint(API_ENDPOINT);
    schema.setOrder(1310);
    schema.setDataShareable(true);

    schema.getAuthorities().add(
        new Authority(AuthorityType.CREATE_PUBLIC, Lists.newArrayList("F_FIELDSET_PUBLIC_ADD")));
    schema.getAuthorities().add(
        new Authority(AuthorityType.CREATE_PRIVATE, Lists.newArrayList("F_FIELDSET_PRIVATE_ADD")));
    schema.getAuthorities()
        .add(new Authority(AuthorityType.DELETE, Lists.newArrayList("F_FIELDSET_DELETE")));

    return schema;
  }
}
