package com.mass3d.fieldfilter;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.EmbeddedObject;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.node.AbstractNode;
import com.mass3d.node.Node;
import com.mass3d.node.NodeTransformer;
import com.mass3d.node.Preset;
import com.mass3d.node.types.CollectionNode;
import com.mass3d.node.types.ComplexNode;
import com.mass3d.node.types.SimpleNode;
import com.mass3d.period.PeriodType;
import com.mass3d.preheat.Preheat;
import com.mass3d.schema.Property;
import com.mass3d.schema.Schema;
import com.mass3d.schema.SchemaService;
import com.mass3d.security.acl.AclService;
import com.mass3d.system.util.ReflectionUtils;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component( "com.mass3d.fieldfilter.FieldFilterService" )
public class DefaultFieldFilterService implements FieldFilterService {

  private static final Log log = LogFactory.getLog(DefaultFieldFilterService.class);

  private final static Pattern FIELD_PATTERN = Pattern.compile("^(?<field>\\w+)");

  private final static Pattern TRANSFORMER_PATTERN = Pattern
      .compile("(?<type>\\||::|~)(?<name>\\w+)(?:\\((?<args>[\\w;]+)\\))?");

  private final FieldParser fieldParser;

  private final SchemaService schemaService;

  private final AclService aclService;

  private final CurrentUserService currentUserService;

  private Set<NodeTransformer> nodeTransformers;

  private ImmutableMap<String, Preset> presets = ImmutableMap.of();

  private ImmutableMap<String, NodeTransformer> transformers = ImmutableMap.of();

  private Property baseIdentifiableIdProperty;

  public DefaultFieldFilterService(FieldParser fieldParser, SchemaService schemaService,
      AclService aclService,
      CurrentUserService currentUserService,
      @Autowired(required = false) Set<NodeTransformer> nodeTransformers) {
    this.fieldParser = fieldParser;
    this.schemaService = schemaService;
    this.aclService = aclService;
    this.currentUserService = currentUserService;
    this.nodeTransformers = nodeTransformers == null ? new HashSet<>() : nodeTransformers;
  }

  @PostConstruct
  public void init() {
    ImmutableMap.Builder<String, Preset> presetBuilder = ImmutableMap.builder();

    for (Preset preset : Preset.values()) {
      presetBuilder.put(preset.getName(), preset);
    }

    presets = presetBuilder.build();

    ImmutableMap.Builder<String, NodeTransformer> transformerBuilder = ImmutableMap.builder();

    for (NodeTransformer transformer : nodeTransformers) {
      transformerBuilder.put(transformer.name(), transformer);
    }

    transformers = transformerBuilder.build();

    baseIdentifiableIdProperty = schemaService.getDynamicSchema(BaseIdentifiableObject.class)
        .getProperty("id");
  }

  @Override
  public ComplexNode toComplexNode(FieldFilterParams params) {
    if (params.getObjects().isEmpty()) {
      return null;
    }

    Object object = params.getObjects().get(0);
    CollectionNode collectionNode = toCollectionNode(object.getClass(), params);

    if (!collectionNode.getChildren().isEmpty()) {
      return (ComplexNode) collectionNode.getChildren().get(0);
    }

    return null;
  }

  @Override
  public CollectionNode toCollectionNode(Class<?> wrapper, FieldFilterParams params) {
    String fields = params.getFields() == null ? "" : Joiner.on(",").join(params.getFields());

    Schema rootSchema = schemaService.getDynamicSchema(wrapper);

    CollectionNode collectionNode = new CollectionNode(rootSchema.getCollectionName());
    collectionNode.setNamespace(rootSchema.getNamespace());

    List<?> objects = params.getObjects();

    if (params.getSkipSharing()) {
      final List<String> fieldList =
          CollectionUtils.isEmpty(params.getFields()) ? Collections.singletonList("*")
              : params.getFields();
      // excludes must be preserved (e.g. when field collections like :owner are used, which is not expanded by modify filter)
      fields = Stream.concat(fieldParser.modifyFilter(fieldList, SHARING_FIELDS).stream(),
          SHARING_FIELDS.stream())
          .filter(org.apache.commons.lang3.StringUtils::isNotBlank).distinct()
          .collect(Collectors.joining(","));
    }

    if (params.getObjects().isEmpty()) {
      return collectionNode;
    }

    FieldMap fieldMap = new FieldMap();
    Schema schema = schemaService.getDynamicSchema(objects.get(0).getClass());

    if (StringUtils.isEmpty(fields)) {
      for (Property property : schema.getProperties()) {
        fieldMap.put(property.getName(), new FieldMap());
      }
    } else {
      fieldMap = fieldParser.parse(fields);
    }

    final FieldMap finalFieldMap = fieldMap;

    if (params.getUser() == null) {
      params.setUser(currentUserService.getCurrentUser());
    }

    objects.forEach(object -> {
      AbstractNode node = buildNode(finalFieldMap, wrapper, object, params.getUser(),
          params.getDefaults());

      if (node != null) {
        collectionNode.addChild(node);
      }
    });

    return collectionNode;
  }

  private AbstractNode buildNode(FieldMap fieldMap, Class<?> klass, Object object, User user,
      Defaults defaults) {
    Schema schema = schemaService.getDynamicSchema(klass);
    return buildNode(fieldMap, klass, object, user, schema.getName(), defaults);
  }

  private boolean mayExclude(Class<?> klass, Defaults defaults) {
    return Defaults.EXCLUDE == defaults && IdentifiableObject.class.isAssignableFrom(klass) &&
        (Preheat.isDefaultClass(klass) || klass.isInterface()
            || (klass.getModifiers() & Modifier.ABSTRACT) != 0);
  }

  private boolean shouldExclude(Object object, Defaults defaults) {
    return Defaults.EXCLUDE == defaults && IdentifiableObject.class.isInstance(object) &&
        Preheat.isDefaultClass((IdentifiableObject) object) && "default"
        .equals(((IdentifiableObject) object).getName());
  }

  private AbstractNode buildNode(FieldMap fieldMap, Class<?> klass, Object object, User user,
      String nodeName, Defaults defaults) {
    Schema schema = schemaService.getDynamicSchema(klass);

    ComplexNode complexNode = new ComplexNode(nodeName);
    complexNode.setNamespace(schema.getNamespace());

    if (object == null) {
      return new SimpleNode(schema.getName(), null);
    }

    if (shouldExclude(object, defaults)) {
      return null;
    }

    updateFields(fieldMap, schema.getKlass());

    if (fieldMap.containsKey("access") && schema.isIdentifiableObject()) {
      ((BaseIdentifiableObject) object)
          .setAccess(aclService.getAccess((IdentifiableObject) object, user));
    }

    for (String fieldKey : fieldMap.keySet()) {
      AbstractNode child = null;
      Property property = schema.getProperty(fieldKey);

      if (property == null || !property.isReadable()) {
        // throw new FieldFilterException( fieldKey, schema );
        log.debug("Unknown field property `" + fieldKey + "`, available fields are " + schema
            .getPropertyMap().keySet());
        continue;
      }

      Object returnValue = ReflectionUtils.invokeMethod(object, property.getGetterMethod());
      Schema propertySchema = schemaService.getDynamicSchema(property.getKlass());

      FieldMap fieldValue = fieldMap.get(fieldKey);

      if (returnValue == null && property.isCollection()) {
        continue;
      }

      if (property.isCollection()) {
        updateFields(fieldValue, property.getItemKlass());
      } else {
        updateFields(fieldValue, property.getKlass());
      }

      if (fieldValue.isEmpty()) {
        List<String> fields = Preset.defaultAssociationPreset().getFields();

        if (property.isCollection()) {
          Collection<?> collection = (Collection<?>) returnValue;

          child = new CollectionNode(property.getCollectionName(), collection.size());
          child.setNamespace(property.getNamespace());

          if (property.isIdentifiableObject() && isProperIdObject(property.getItemKlass())) {
            final boolean mayExclude =
                collection.isEmpty() || mayExclude(property.getItemKlass(), defaults);

            for (Object collectionObject : collection) {
              if (!mayExclude || !shouldExclude(collectionObject, defaults)) {
                child.addChild(getProperties(property, collectionObject, fields));
              }
            }
          } else if (!property.isSimple()) {
            FieldMap map = getFullFieldMap(schemaService.getDynamicSchema(property.getItemKlass()));

            for (Object collectionObject : collection) {
              Node node = buildNode(map, property.getItemKlass(), collectionObject, user, defaults);

              if (node != null && !node.getChildren().isEmpty()) {
                child.addChild(node);
              }
            }
          } else {
            if (collection != null) {
              for (Object collectionObject : collection) {
                SimpleNode simpleNode = child
                    .addChild(new SimpleNode(property.getName(), collectionObject));
                simpleNode.setProperty(property);
              }
            }
          }
        } else if (property.isIdentifiableObject() && isProperIdObject(property.getKlass())) {
          if (!shouldExclude(returnValue, defaults)) {
            child = getProperties(property, returnValue, fields);
          }
        } else {
          if (propertySchema.getProperties().isEmpty()) {
            SimpleNode simpleNode = new SimpleNode(fieldKey, returnValue);
            simpleNode.setAttribute(property.isAttribute());
            simpleNode.setNamespace(property.getNamespace());

            child = simpleNode;
          } else {
            child = buildNode(getFullFieldMap(propertySchema), property.getKlass(), returnValue,
                user, defaults);
          }
        }
      } else {
        if (property.isCollection()) {
          child = new CollectionNode(property.getCollectionName());
          child.setNamespace(property.getNamespace());

          for (Object collectionObject : (Collection<?>) returnValue) {
            Node node = buildNode(fieldValue, property.getItemKlass(), collectionObject, user,
                property.getName(), defaults);

            if (!node.getChildren().isEmpty()) {
              child.addChild(node);
            }
          }
        } else {
          child = buildNode(fieldValue, property.getKlass(), returnValue, user, defaults);
        }
      }

      if (child != null) {
        child.setName(fieldKey);
        child.setProperty(property);

        // TODO fix ugly hack, will be replaced by custom field serializer/deserializer
        if (child.isSimple() && PeriodType.class.isInstance((((SimpleNode) child).getValue()))) {
          child = new SimpleNode(child.getName(),
              ((PeriodType) ((SimpleNode) child).getValue()).getName());
        }

        complexNode.addChild(fieldValue.getPipeline().process(child));
      }
    }

    return complexNode;
  }

  private void updateFields(FieldMap fieldMap, Class<?> klass) {
    if (fieldMap.isEmpty()) {
      return;
    }

    // we need two run this (at least) two times, since some of the presets might contain other presets
    updateFields(fieldMap, klass, true);
    updateFields(fieldMap, klass, false);
  }

  private void updateFields(FieldMap fieldMap, Class<?> klass, boolean expandOnly) {
    if (fieldMap.isEmpty()) {
      return;
    }

    Schema schema = schemaService.getDynamicSchema(klass);
    List<String> cleanupFields = Lists.newArrayList();

    for (String fieldKey : Sets.newHashSet(fieldMap.keySet())) {
      Collection<Property> properties = schema.getReadableProperties().values();

      if ("*".equals(fieldKey)) {
        properties.stream()
            .filter(property -> !fieldMap.containsKey(property.key()))
            .forEach(property -> fieldMap.put(property.key(), new FieldMap()));

        cleanupFields.add(fieldKey);
      } else if (":persisted".equals(fieldKey)) {
        properties.stream()
            .filter(property -> !fieldMap.containsKey(property.key()) && property.isPersisted())
            .forEach(property -> fieldMap.put(property.key(), new FieldMap()));

        cleanupFields.add(fieldKey);
      } else if (":owner".equals(fieldKey)) {
        properties.stream()
            .filter(property -> !fieldMap.containsKey(property.key()) && property.isPersisted()
                && property.isOwner())
            .forEach(property -> fieldMap.put(property.key(), new FieldMap()));

        cleanupFields.add(fieldKey);
      } else if (fieldKey.startsWith(":")) {
        Preset preset = presets.get(fieldKey.substring(1));

        if (preset == null) {
          continue;
        }

        List<String> fields = preset.getFields();

        fields.stream()
            .filter(field -> !fieldMap.containsKey(field))
            .forEach(field -> fieldMap.put(field, new FieldMap()));

        cleanupFields.add(fieldKey);
      } else if (fieldKey.startsWith("!") && !expandOnly) {
        cleanupFields.add(fieldKey);
      } else if (fieldKey.contains("::") || fieldKey.contains("|") || fieldKey.contains("~")) {
        Matcher matcher = FIELD_PATTERN.matcher(fieldKey);

        if (!matcher.find()) {
          continue;
        }

        String fieldName = matcher.group("field");

        FieldMap value = new FieldMap();
        value.putAll(fieldMap.get(fieldKey));

        matcher = TRANSFORMER_PATTERN.matcher(fieldKey);

        while (matcher.find()) {
          String nameMatch = matcher.group("name");
          String argsMatch = matcher.group("args");

          if (transformers.containsKey(nameMatch)) {
            NodeTransformer transformer = transformers.get(nameMatch);
            List<String> args =
                argsMatch == null ? new ArrayList<>() : Lists.newArrayList(argsMatch.split(";"));
            value.getPipeline().addTransformer(transformer, args);
          }
        }

        fieldMap.put(fieldName, value);

        cleanupFields.add(fieldKey);
      }
    }

    for (String field : cleanupFields) {
      fieldMap.remove(field);

      if (!expandOnly) {
        fieldMap.remove(field.substring(1));
      }
    }
  }

  private FieldMap getFullFieldMap(Schema schema) {
    FieldMap fieldMap = new FieldMap();

    for (Property property : schema.getReadableProperties().values()) {
      fieldMap.put(property.getName(), new FieldMap());
    }

    for (String mapKey : schema.getPropertyMap().keySet()) {
      if (schema.getProperty(mapKey).isReadable()) {
        fieldMap.put(mapKey, new FieldMap());
      }
    }

    return fieldMap;
  }

  private ComplexNode getProperties(Property currentProperty, Object object, List<String> fields) {
    if (object == null) {
      return null;
    }

    // performance optimization for ID only queries on base identifiable objects
    if (isBaseIdentifiableObjectIdOnly(object, fields)) {
      return createBaseIdentifiableObjectIdNode(currentProperty, object);
    }

    ComplexNode complexNode = new ComplexNode(currentProperty.getName());
    complexNode.setNamespace(currentProperty.getNamespace());
    complexNode.setProperty(currentProperty);

    Schema schema;

    if (currentProperty.isCollection()) {
      schema = schemaService.getDynamicSchema(currentProperty.getItemKlass());

    } else {
      schema = schemaService.getDynamicSchema(currentProperty.getKlass());
    }

    for (String field : fields) {
      Property property = schema.getProperty(field);

      if (property == null) {
        continue;
      }

      Object returnValue = ReflectionUtils.invokeMethod(object, property.getGetterMethod());

      SimpleNode simpleNode = new SimpleNode(field, returnValue);
      simpleNode.setAttribute(property.isAttribute());
      simpleNode.setNamespace(property.getNamespace());
      simpleNode.setProperty(property);

      complexNode.addChild(simpleNode);
    }

    return complexNode;
  }

  private boolean isBaseIdentifiableObjectIdOnly(@Nonnull Object object,
      @Nonnull List<String> fields) {
    return fields.size() == 1 && fields.get(0).equals("id")
        && object instanceof BaseIdentifiableObject;
  }

  private ComplexNode createBaseIdentifiableObjectIdNode(@Nonnull Property currentProperty,
      @Nonnull Object object) {
    return new ComplexNode(currentProperty, new SimpleNode(
        "id", baseIdentifiableIdProperty, ((BaseIdentifiableObject) object).getUid()));
  }

  private boolean isProperIdObject(Class<?> klass) {
    return !(UserCredentials.class.isAssignableFrom(klass) || EmbeddedObject.class
        .isAssignableFrom(klass));
  }
}
