package com.mass3d.deletedobject.hibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class DeletedObjectIntegrator implements Integrator {

  @Override
  public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
      SessionFactoryServiceRegistry serviceRegistry) {
    final EventListenerRegistry registry = serviceRegistry.getService(EventListenerRegistry.class);

    DeletedObjectPostDeleteEventListener listener = new DeletedObjectPostDeleteEventListener();
    registry.appendListeners(EventType.POST_DELETE, listener);
  }

  @Override
  public void disintegrate(SessionFactoryImplementor sessionFactory,
      SessionFactoryServiceRegistry serviceRegistry) {

  }
}
