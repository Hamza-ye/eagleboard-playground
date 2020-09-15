package com.mass3d.services.core.deletedobject;

import com.mass3d.api.deletedobject.DeletedObject;
import com.mass3d.api.deletedobject.DeletedObjectQuery;
import com.mass3d.api.deletedobject.DeletedObjectService;
import com.mass3d.api.deletedobject.DeletedObjectStore;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultDeletedObjectService
    implements DeletedObjectService {

  private final DeletedObjectStore deletedObjectStore;

  public DefaultDeletedObjectService(DeletedObjectStore deletedObjectStore) {
    this.deletedObjectStore = deletedObjectStore;
  }

  @Override
  public void addDeletedObject(DeletedObject deletedObject) {
    deletedObjectStore.save(deletedObject);
  }

  @Override
  public void deleteDeletedObject(DeletedObject deletedObject) {
    deletedObjectStore.delete(deletedObject);
  }

  @Override
  public void deleteDeletedObjects(DeletedObjectQuery query) {
    deletedObjectStore.delete(query);
  }

  @Override
  public List<DeletedObject> getDeletedObjectsByKlass(String klass) {
    return deletedObjectStore.getByKlass(klass);
  }

  @Override
  public List<DeletedObject> getDeletedObjects() {
    return deletedObjectStore.query(DeletedObjectQuery.EMPTY);
  }

  @Override
  public int countDeletedObjects() {
    return deletedObjectStore.count(DeletedObjectQuery.EMPTY);
  }

  @Override
  public List<DeletedObject> getDeletedObjects(DeletedObjectQuery query) {
    return deletedObjectStore.query(query);
  }

  @Override
  public int countDeletedObjects(DeletedObjectQuery query) {
    return deletedObjectStore.count(query);
  }
}
