package com.mass3d.deletedobject.hibernate;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.MetadataObject;
import com.mass3d.common.UserContext;
import com.mass3d.deletedobject.DeletedObject;

public class DeletedObjectPostDeleteEventListener implements PostDeleteEventListener {

  @Override
  public void onPostDelete(PostDeleteEvent event) {
    if (MetadataObject.class.isInstance(event.getEntity())) {
      IdentifiableObject identifiableObject = (IdentifiableObject) event.getEntity();
      DeletedObject deletedObject = new DeletedObject(identifiableObject);
      deletedObject.setDeletedBy(getUsername());

      event.getSession().persist(deletedObject);
    }
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return false;
  }

  private String getUsername() {
    return UserContext.haveUser() ? UserContext.getUser().getUsername() : "system-process";
  }
}
