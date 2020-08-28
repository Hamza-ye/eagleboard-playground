package com.mass3d.services.core.datafield;

import com.mass3d.api.common.IdentifiableObjectStore;
import com.mass3d.api.datafield.DataField;
import org.springframework.stereotype.Repository;

@Repository
public interface DataFieldStore extends IdentifiableObjectStore<DataField> {

}
