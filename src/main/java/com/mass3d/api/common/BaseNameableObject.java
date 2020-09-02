package com.mass3d.api.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.api.schema.annotation.PropertyRange;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JacksonXmlRootElement(localName = "nameableObject", namespace = DxfNamespaces.DXF_2_0)
public class BaseNameableObject
extends BaseIdentifiableObject implements NameableObject{

  /**
   * A short name representing this Object. Optional but unique.
   */
  protected String shortName;

  /**
   * Description of this Object.
   */
  protected String description;

  /**
   * The i18n variant of the short name. Should not be persisted.
   */
  protected transient String displayShortName;

  /**
   * The i18n variant of the description. Should not be persisted.
   */
  protected transient String displayDescription;

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public BaseNameableObject()
  {
  }

  public BaseNameableObject( String uid, String code, String name )
  {
    this.uid = uid;
    this.code = code;
    this.name = name;
  }

  public BaseNameableObject( long id, String uid, String name, String shortName, String code, String description )
  {
    super( id, uid, name );
    this.shortName = shortName;
    this.code = code;
    this.description = description;
  }

  public BaseNameableObject( NameableObject object )
  {
    super( object.getId(), object.getUid(), object.getName() );
    this.shortName = object.getShortName();
    this.code = object.getCode();
    this.description = object.getDescription();
  }

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------


  // -------------------------------------------------------------------------
  // hashCode, equals and toString
  // -------------------------------------------------------------------------

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (getShortName() != null ? getShortName().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    return result;
  }

  /**
   * Class check uses isAssignableFrom and get-methods to handle proxied objects.
   */
  @Override
  public boolean equals( Object o )
  {
    if ( this == o )
    {
      return true;
    }

    if ( o == null )
    {
      return false;
    }

    if ( !getClass().isAssignableFrom( o.getClass() ) )
    {
      return false;
    }

    if ( !super.equals( o ) )
    {
      return false;
    }

    final BaseNameableObject other = (BaseNameableObject) o;

    if ( getShortName() != null ? !getShortName().equals( other.getShortName() ) : other.getShortName() != null )
    {
      return false;
    }

    if ( getDescription() != null ? !getDescription().equals( other.getDescription() ) : other.getDescription() != null )
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return "{" +
        "\"class\":\"" + getClass() + "\", " +
        "\"hashCode\":\"" + hashCode() + "\", " +
        "\"id\":\"" + getId() + "\", " +
        "\"uid\":\"" + getUid() + "\", " +
        "\"code\":\"" + getCode() + "\", " +
        "\"name\":\"" + getName() + "\", " +
        "\"shortName\":\"" + getShortName() + "\", " +
        "\"description\":\"" + getDescription() + "\", " +
        "\"created\":\"" + getCreated() + "\", " +
        "\"lastUpdated\":\"" + getLastUpdated() + "\" " +
        "}";
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @Override
  @JsonProperty
  @JacksonXmlProperty(isAttribute = true)
  @PropertyRange(min = 1)
  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @Override
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getDisplayShortName() {
//    displayShortName = getTranslation(TranslationProperty.SHORT_NAME, displayShortName);
    return displayShortName != null ? displayShortName : getShortName();
  }

  public void setDisplayShortName(String displayShortName) {
    this.displayShortName = displayShortName;
  }

  @Override
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  @PropertyRange(min = 1)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public String getDisplayDescription() {
//    displayDescription = getTranslation(TranslationProperty.DESCRIPTION, displayDescription);
    return displayDescription != null ? displayDescription : getDescription();
  }

  public void setDisplayDescription(String displayDescription) {
    this.displayDescription = displayDescription;
  }
}
