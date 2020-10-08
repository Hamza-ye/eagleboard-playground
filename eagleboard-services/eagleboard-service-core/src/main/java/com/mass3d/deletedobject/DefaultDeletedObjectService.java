package com.mass3d.deletedobject;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service( "com.mass3d.deletedobject.DeletedObjectService" )
@Transactional
public class DefaultDeletedObjectService
    implements DeletedObjectService {

  private final DeletedObjectStore deletedObjectStore;

  public DefaultDeletedObjectService(DeletedObjectStore deletedObjectStore) {
    checkNotNull( deletedObjectStore );
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
