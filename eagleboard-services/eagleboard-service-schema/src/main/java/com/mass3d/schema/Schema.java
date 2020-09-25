package com.mass3d.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.EmbeddedObject;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.MetadataObject;
import com.mass3d.common.NameableObject;
import com.mass3d.security.Authority;
import com.mass3d.security.AuthorityType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

@JacksonXmlRootElement(localName = "schema", namespace = DxfNamespaces.DXF_2_0)
public class Schema implements Ordered, Klass {

  /**
   * Class that is described in this schema.
   */
  private final Class<?> klass;

  /**
   * Is this class a sub-class of IdentifiableObject
   *
   * @see com.mass3d.common.IdentifiableObject
   */
  private final boolean identifiableObject;

  /**
   * Is this class a sub-class of NameableObject
   *
   * @see com.mass3d.common.NameableObject
   */
  private final boolean nameableObject;

  /**
   * Does this class implement {@link EmbeddedObject} ?
   */
  private final boolean embeddedObject;

  /**
   * Singular name.
   */
  private final String singular;

  /**
   * Plural name.
   */
  private final String plural;

  /**
   * Is this class considered metadata, this is mainly used for our metadata importer/exporter.
   */
  private final boolean metadata;

  /**
   * Namespace URI to be used for this class.
   */
  private String namespace;

  /**
   * This will normally be set to equal singular, but in certain cases it might be useful to have
   * another name for when this class is used as an item inside a collection.
   */
  private String name;

  /**
   * A beautified (and possibly translated) name that can be used in UI.
   */
  private String displayName;

  /**
   * This will normally be set to equal plural, and is normally used as a wrapper for a collection
   * of instances of this klass type.
   */
  private String collectionName;

  /**
   * Is sharing supported for instances of this class.
   */
  private Boolean shareable;

  /**
   * Is data sharing supported for instances of this class.
   */
  private boolean dataShareable;

  /**
   * Points to relative Web-API endpoint (if exposed).
   */
  private String relativeApiEndpoint;

  /**
   * Used by LinkService to link to the API endpoint containing this type.
   */
  private String apiEndpoint;

  /**
   * Used by LinkService to link to the Schema describing this type (if reference).
   */
  private String href;

  /**
   * Are any properties on this class being persisted, if false, this file does not have any hbm
   * file attached to it.
   */
  private boolean persisted;

  /**
   * Should new instances always be default private, even if the user can create public instances.
   */
  private boolean defaultPrivate;

  /**
   * If this is true, do not require private authority for create/update of instances of this type.
   */
  private boolean implicitPrivateAuthority;

  /**
   * List of authorities required for doing operations on this class.
   */
  private List<Authority> authorities = Lists.newArrayList();

  /**
   * Map of all exposed properties on this class, where key is property name, and value is instance
   * of Property class.
   *
   * @see com.mass3d.schema.Property
   */
  private Map<String, Property> propertyMap = Maps.newHashMap();

  /**
   * Map of all readable properties, cached on first request.
   */
  private Map<String, Property> readableProperties = new HashMap<>();

  /**
   * Map of all persisted properties, cached on first request.
   */
  private Map<String, Property> persistedProperties = new HashMap<>();

  /**
   * Map of all persisted properties, cached on first request.
   */
  private Map<String, Property> nonPersistedProperties = new HashMap<>();

  /**
   * Map of all link object properties, cached on first request.
   */
  private Map<String, Property> embeddedObjectProperties;

  /**
   * Map containing cached authorities by their type.
   */
  @JsonIgnore
  private transient volatile Map<AuthorityType, List<String>> cachedAuthoritiesByType;

  /**
   * Used for sorting of schema list when doing metadata import/export.
   */
  private int order = Ordered.LOWEST_PRECEDENCE;
  @SuppressWarnings("rawtypes")
  private Set<Class> references;

  public Schema(Class<?> klass, String singular, String plural) {
    this.klass = klass;
    this.embeddedObject = EmbeddedObject.class.isAssignableFrom(klass);
    this.identifiableObject = IdentifiableObject.class.isAssignableFrom(klass);
    this.nameableObject = NameableObject.class.isAssignableFrom(klass);
    this.singular = singular;
    this.plural = plural;
    this.metadata = MetadataObject.class.isAssignableFrom(klass);
  }

  @Override
  @JsonProperty
  @JacksonXmlProperty(isAttribute = true)
  public Class<?> getKlass() {
    return klass;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isIdentifiableObject() {
    return identifiableObject;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isNameableObject() {
    return nameableObject;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isEmbeddedObject() {
    return embeddedObject;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getSingular() {
    return singular;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getPlural() {
    return plural;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isMetadata() {
    return metadata;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getCollectionName() {
    return collectionName == null ? plural : collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getName() {
    return name == null ? singular : name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getDisplayName() {
    return displayName != null ? displayName : getName();
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isShareable() {
    return shareable != null ? shareable :
        (havePersistedProperty("user") && havePersistedProperty("userGroupAccesses")
            && havePersistedProperty("publicAccess"));
  }

  public void setShareable(boolean shareable) {
    this.shareable = shareable;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isDataShareable() {
    return dataShareable;
  }

  public void setDataShareable(boolean dataShareable) {
    this.dataShareable = dataShareable;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getRelativeApiEndpoint() {
    return relativeApiEndpoint;
  }

  public void setRelativeApiEndpoint(String relativeApiEndpoint) {
    this.relativeApiEndpoint = relativeApiEndpoint;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getApiEndpoint() {
    return apiEndpoint;
  }

  public void setApiEndpoint(String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
  }

  public boolean haveApiEndpoint() {
    return getRelativeApiEndpoint() != null || getApiEndpoint() != null;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isPersisted() {
    return persisted;
  }

  public void setPersisted(boolean persisted) {
    this.persisted = persisted;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isTranslatable() {
    return isIdentifiableObject() && havePersistedProperty("translations");
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isFavoritable() {
    return isIdentifiableObject() && havePersistedProperty("favorites");
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isDefaultPrivate() {
    return defaultPrivate;
  }

  public void setDefaultPrivate(boolean defaultPrivate) {
    this.defaultPrivate = defaultPrivate;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public boolean isImplicitPrivateAuthority() {
    return implicitPrivateAuthority;
  }

  public void setImplicitPrivateAuthority(boolean implicitPrivateAuthority) {
    this.implicitPrivateAuthority = implicitPrivateAuthority;
  }

  @JsonProperty
  @JacksonXmlElementWrapper(localName = "authorities", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "authority", namespace = DxfNamespaces.DXF_2_0)
  public List<Authority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(List<Authority> authorities) {
    this.authorities = authorities;
    this.cachedAuthoritiesByType = null;
  }

  @JsonProperty
  @JacksonXmlElementWrapper(localName = "properties", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "property", namespace = DxfNamespaces.DXF_2_0)
  public List<Property> getProperties() {
    return Lists.newArrayList(propertyMap.values());
  }

  public boolean haveProperty(String propertyName) {
    return getPropertyMap().containsKey(propertyName);
  }

  public boolean havePersistedProperty(String propertyName) {
    return haveProperty(propertyName) && getProperty(propertyName).isPersisted();
  }

  public Property propertyByRole(String role) {
    if (!StringUtils.isEmpty(role)) {
      for (Property property : propertyMap.values()) {
        if (property.isCollection() && property.isManyToMany() && (
            role.equals(property.getOwningRole()) || role.equals(property.getInverseRole()))) {
          return property;
        }
      }
    }

    return null;
  }

  @JsonIgnore
  public Map<String, Property> getPropertyMap() {
    return propertyMap;
  }

  public void setPropertyMap(Map<String, Property> propertyMap) {
    this.propertyMap = propertyMap;
  }

  @JsonProperty
  @JacksonXmlElementWrapper(localName = "references", namespace = DxfNamespaces.DXF_2_0)
  @JacksonXmlProperty(localName = "reference", namespace = DxfNamespaces.DXF_2_0)
  @SuppressWarnings("rawtypes")
  public Set<Class> getReferences() {
    if (references == null) {
      references = getProperties().stream()
          .filter(p -> p.isCollection() ? PropertyType.REFERENCE == p.getItemPropertyType()
              : PropertyType.REFERENCE == p.getPropertyType())
          .map(p -> p.isCollection() ? p.getItemKlass() : p.getKlass()).collect(Collectors.toSet());
    }

    return references;
  }

  public Map<String, Property> getReadableProperties() {
    if (readableProperties.isEmpty()) {
      getPropertyMap().entrySet().stream()
          .filter(entry -> entry.getValue().isReadable())
          .forEach(entry -> readableProperties.put(entry.getKey(), entry.getValue()));
    }

    return readableProperties;
  }

  public Map<String, Property> getPersistedProperties() {
    if (persistedProperties.isEmpty()) {
      getPropertyMap().entrySet().stream()
          .filter(entry -> entry.getValue().isPersisted())
          .forEach(entry -> persistedProperties.put(entry.getKey(), entry.getValue()));
    }

    return persistedProperties;
  }

  public Map<String, Property> getNonPersistedProperties() {
    if (nonPersistedProperties.isEmpty()) {
      getPropertyMap().entrySet().stream()
          .filter(entry -> !entry.getValue().isPersisted())
          .forEach(entry -> nonPersistedProperties.put(entry.getKey(), entry.getValue()));
    }

    return nonPersistedProperties;
  }

  public Map<String, Property> getEmbeddedObjectProperties() {
    if (embeddedObjectProperties == null) {
      embeddedObjectProperties = new HashMap<>();

      getPropertyMap().entrySet().stream()
          .filter(entry -> entry.getValue().isEmbeddedObject())
          .forEach(entry -> embeddedObjectProperties.put(entry.getKey(), entry.getValue()));
    }

    return embeddedObjectProperties;
  }

  public void addProperty(Property property) {
    if (property == null || property.getName() == null || propertyMap
        .containsKey(property.getName())) {
      return;
    }

    propertyMap.put(property.getName(), property);
  }

  @JsonIgnore
  public Property getProperty(String name) {
    return propertyMap.get(name);
  }

  @JsonIgnore
  public Property getPersistedProperty(String name) {
    Property property = getProperty(name);

    if (property != null && property.isPersisted()) {
      return property;
    }

    return null;
  }

  public List<String> getAuthorityByType(AuthorityType type) {
    if (cachedAuthoritiesByType == null) {
      cachedAuthoritiesByType = new HashMap<>();
    }

    List<String> authorityList = cachedAuthoritiesByType.get(type);

    if (authorityList != null) {
      return authorityList;
    }

    final List<String> list = new ArrayList<>();
    authorities.stream()
        .filter(authority -> type.equals(authority.getType()))
        .forEach(authority -> list.addAll(authority.getAuthorities()));
    authorityList = Collections.unmodifiableList(list);

    final Map<AuthorityType, List<String>> authoritiesByType = new HashMap<>();
    authoritiesByType.put(type, authorityList);

    final Map<AuthorityType, List<String>> currentCachedAuthoritiesByType = cachedAuthoritiesByType;

    if (currentCachedAuthoritiesByType != null) {
      authoritiesByType.putAll(currentCachedAuthoritiesByType);
    }

    cachedAuthoritiesByType = authoritiesByType;

    return authorityList;
  }

  @Override
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }


  @Override
  public int hashCode() {
    return Objects
        .hash(klass, identifiableObject, nameableObject, singular, plural, namespace, name,
            collectionName, shareable, relativeApiEndpoint, metadata, authorities, propertyMap,
            order);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final Schema other = (Schema) obj;

    return Objects.equals(this.klass, other.klass) && Objects
        .equals(this.identifiableObject, other.identifiableObject)
        && Objects.equals(this.nameableObject, other.nameableObject) && Objects
        .equals(this.singular, other.singular)
        && Objects.equals(this.plural, other.plural) && Objects
        .equals(this.namespace, other.namespace)
        && Objects.equals(this.name, other.name) && Objects
        .equals(this.collectionName, other.collectionName)
        && Objects.equals(this.shareable, other.shareable) && Objects
        .equals(this.relativeApiEndpoint, other.relativeApiEndpoint)
        && Objects.equals(this.metadata, other.metadata) && Objects
        .equals(this.authorities, other.authorities)
        && Objects.equals(this.propertyMap, other.propertyMap) && Objects
        .equals(this.order, other.order);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("klass", klass)
        .add("identifiableObject", identifiableObject)
        .add("nameableObject", nameableObject)
        .add("singular", singular)
        .add("plural", plural)
        .add("namespace", namespace)
        .add("name", name)
        .add("collectionName", collectionName)
        .add("shareable", shareable)
        .add("relativeApiEndpoint", relativeApiEndpoint)
        .add("metadata", metadata)
        .add("authorities", authorities)
        .add("propertyMap", propertyMap)
        .toString();
  }
}
