package com.mass3d.schema.descriptors;

import com.google.common.collect.Lists;
import com.mass3d.datafield.DataField;
import com.mass3d.security.Authority;
import com.mass3d.security.AuthorityType;
import com.mass3d.schema.Schema;
import com.mass3d.schema.SchemaDescriptor;

public class DataFieldSchemaDescriptor implements SchemaDescriptor {

  public static final String SINGULAR = "datafield";

  public static final String PLURAL = "dataFields";

  public static final String API_ENDPOINT = "/" + PLURAL;

  @Override
  public Schema getSchema() {
    Schema schema = new Schema(DataField.class, SINGULAR, PLURAL);
    schema.setRelativeApiEndpoint(API_ENDPOINT);
    schema.setOrder(1200);

    schema.getAuthorities().add(
        new Authority(AuthorityType.CREATE_PUBLIC, Lists.newArrayList("F_DATAFIELD_PUBLIC_ADD")));
    schema.getAuthorities().add(
        new Authority(AuthorityType.CREATE_PRIVATE, Lists.newArrayList("F_DATAFIELD_PRIVATE_ADD")));
    schema.getAuthorities()
        .add(new Authority(AuthorityType.DELETE, Lists.newArrayList("F_DATAFIELD_DELETE")));

    return schema;
  }
}
