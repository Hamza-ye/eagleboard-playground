package com.mass3d.services.schema;

import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mass3d.api.schema.Property;
import com.mass3d.api.security.Authority;
import com.mass3d.services.schema.descriptors.ActivitySchemaDescriptor;
import com.mass3d.services.schema.descriptors.DataFieldSchemaDescriptor;
import com.mass3d.services.schema.descriptors.FieldSetSchemaDescriptor;
import com.mass3d.services.schema.descriptors.ProjectSchemaDescriptor;
import com.mass3d.services.schema.descriptors.TodoTaskSchemaDescriptor;
import com.mass3d.services.schema.descriptors.UserAccessSchemaDescriptor;
import com.mass3d.services.schema.descriptors.UserCredentialsSchemaDescriptor;
import com.mass3d.services.schema.descriptors.UserGroupAccessSchemaDescriptor;
import com.mass3d.services.schema.descriptors.UserGroupSchemaDescriptor;
import com.mass3d.services.schema.descriptors.UserRoleSchemaDescriptor;
import com.mass3d.services.schema.descriptors.UserSchemaDescriptor;
import com.mass3d.support.common.TextUtils;
import com.mass3d.support.system.util.AnnotationUtils;
import com.mass3d.support.system.util.ReflectionUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.OrderComparator;
import org.springframework.util.StringUtils;

public class DefaultSchemaService
    implements SchemaService {

  private ImmutableList<SchemaDescriptor> descriptors = new ImmutableList.Builder<SchemaDescriptor>().
          add(new DataFieldSchemaDescriptor()).
          add(new FieldSetSchemaDescriptor()).
          add(new TodoTaskSchemaDescriptor()).
          add(new ActivitySchemaDescriptor()).
          add(new ProjectSchemaDescriptor()).
    add(new UserCredentialsSchemaDescriptor()).
          add(new UserGroupSchemaDescriptor()).
          add(new UserRoleSchemaDescriptor()).
          add(new UserSchemaDescriptor()).
    add(new UserAccessSchemaDescriptor()).
          add(new UserGroupAccessSchemaDescriptor()).
    build();

  private Map<Class<?>, Schema> classSchemaMap = new HashMap<>();

  private Map<String, Schema> singularSchemaMap = new HashMap<>();

  private Map<String, Schema> pluralSchemaMap = new HashMap<>();

  private Map<Class<?>, Schema> dynamicClassSchemaMap = new HashMap<>();

  private PropertyIntrospectorService propertyIntrospectorService;

  private SessionFactory sessionFactory;

  public DefaultSchemaService(@Autowired PropertyIntrospectorService propertyIntrospectorService,
      @Autowired SessionFactory sessionFactory) {
    this.propertyIntrospectorService = propertyIntrospectorService;
    this.sessionFactory = sessionFactory;
  }

  @EventListener
  public void handleContextRefresh(ContextRefreshedEvent contextRefreshedEvent) {
    for (SchemaDescriptor descriptor : descriptors) {
      Schema schema = descriptor.getSchema();

      MetamodelImplementor metamodelImplementor = (MetamodelImplementor) sessionFactory
          .getMetamodel();

      try {
        metamodelImplementor.entityPersister(schema.getKlass());
        schema.setPersisted(true);
      } catch (MappingException e) {
        // class is not persisted with Hibernate
        schema.setPersisted(false);
      }

      schema.setDisplayName(TextUtils.getPrettyClassName(schema.getKlass()));

      if (schema.getProperties().isEmpty()) {
        schema.setPropertyMap(
            Maps.newHashMap(propertyIntrospectorService.getPropertiesMap(schema.getKlass())));
      }

      classSchemaMap.put(schema.getKlass(), schema);
      singularSchemaMap.put(schema.getSingular(), schema);
      pluralSchemaMap.put(schema.getPlural(), schema);

      updateSelf(schema);

      schema.getPersistedProperties();
      schema.getNonPersistedProperties();
      schema.getReadableProperties();
      schema.getEmbeddedObjectProperties();
    }
  }

  @Override
  public Schema getSchema(Class<?> klass) {
    if (klass == null) {
      return null;
    }

    klass = ReflectionUtils.getRealClass(klass);

    if (classSchemaMap.containsKey(klass)) {
      return classSchemaMap.get(klass);
    }

    if (dynamicClassSchemaMap.containsKey(klass)) {
      return dynamicClassSchemaMap.get(klass);
    }

    return null;
  }

  @Override
  public Schema getDynamicSchema(Class<?> klass) {
    if (klass == null) {
      return null;
    }

    Schema schema = getSchema(klass);

    if (schema != null) {
      return schema;
    }

    klass = propertyIntrospectorService.getConcreteClass(ReflectionUtils.getRealClass(klass));

    String name = getName(klass);

    schema = new Schema(klass, name, name + "s");
    schema.setDisplayName(beautify(schema));
    schema.setPropertyMap(
        new HashMap<>(propertyIntrospectorService.getPropertiesMap(schema.getKlass())));

    updateSelf(schema);

    dynamicClassSchemaMap.put(klass, schema);

    return schema;
  }

  private String getName(Class<?> klass) {
    if (AnnotationUtils.isAnnotationPresent(klass, JacksonXmlRootElement.class)) {
      JacksonXmlRootElement rootElement = AnnotationUtils
          .getAnnotation(klass, JacksonXmlRootElement.class);

      if (!StringUtils.isEmpty(rootElement.localName())) {
        return rootElement.localName();
      }
    }

    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, klass.getSimpleName());
  }

  @Override
  public Schema getSchemaBySingularName(String name) {
    return singularSchemaMap.get(name);
  }

  @Override
  public Schema getSchemaByPluralName(String name) {
    return pluralSchemaMap.get(name);
  }

  @Override
  public List<Schema> getSchemas() {
    return Lists.newArrayList(classSchemaMap.values());
  }

  @Override
  public List<Schema> getSortedSchemas() {
    List<Schema> schemas = Lists.newArrayList(classSchemaMap.values());
    schemas.sort(OrderComparator.INSTANCE);

    return schemas;
  }

  @Override
  public List<Schema> getMetadataSchemas() {
    List<Schema> schemas = getSchemas();

    schemas.removeIf(schema -> !schema.isMetadata());
    schemas.sort(OrderComparator.INSTANCE);

    return schemas;
  }

  @Override
  public Set<String> collectAuthorities() {
    return getSchemas().stream()
        .map(Schema::getAuthorities).flatMap(Collection::stream)
        .map(Authority::getAuthorities).flatMap(Collection::stream)
        .collect(toSet());
  }

  private void updateSelf(Schema schema) {
    if (schema.haveProperty("__self__")) {
      Property property = schema.getProperty("__self__");
      schema.setName(property.getName());
      schema.setCollectionName(schema.getPlural());
      schema.setNamespace(property.getNamespace());

      schema.getPropertyMap().remove("__self__");
    }
  }

  private String beautify(Schema schema) {
    String[] camelCaseWords = org.apache.commons.lang3.StringUtils.capitalize(schema.getPlural())
        .split("(?=[A-Z])");
    return org.apache.commons.lang3.StringUtils.join(camelCaseWords, " ").trim();
  }
}