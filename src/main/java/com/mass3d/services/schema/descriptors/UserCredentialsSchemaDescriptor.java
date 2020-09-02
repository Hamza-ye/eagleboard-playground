package com.mass3d.services.schema.descriptors;

import com.mass3d.api.user.UserCredentials;
import com.mass3d.services.schema.Schema;
import com.mass3d.services.schema.SchemaDescriptor;

public class UserCredentialsSchemaDescriptor implements SchemaDescriptor {

  public static final String SINGULAR = "userCredentials";

  public static final String PLURAL = "userCredentials";

  public static final String API_ENDPOINT = "/" + PLURAL;

  @Override
  public Schema getSchema() {
    return new Schema(UserCredentials.class, SINGULAR, PLURAL);
  }
}
