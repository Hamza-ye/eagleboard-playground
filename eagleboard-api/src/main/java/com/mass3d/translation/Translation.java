package com.mass3d.translation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;
import com.mass3d.common.DxfNamespaces;

@JacksonXmlRootElement(localName = "translations", namespace = DxfNamespaces.DXF_2_0)
public class Translation implements Serializable {

  private String locale;

  private TranslationProperty property;

  private String value;

  public Translation() {
  }

  public Translation(String locale, TranslationProperty property, String value) {
    this.locale = locale;
    this.property = property;
    this.value = value;
  }

  /**
   * Creates a cache key.
   *
   * @param locale the locale string, i.e. Locale.toString().
   * @param property the translation property.
   * @return a unique cache key valid for a given translated objects, or null if either locale or
   * property is null.
   */
  public static String getCacheKey(String locale, TranslationProperty property) {
    return locale != null && property != null ? (locale + property.name()) : null;
  }

  @Override
  public int hashCode() {
    return Objects.hash(locale, property, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final Translation other = (Translation) obj;

    return Objects.equals(this.locale, other.locale)
        && Objects.equals(this.property, other.property)
        && Objects.equals(this.value, other.value);
  }

  //-------------------------------------------------------------------------------
  // Accessors
  //-------------------------------------------------------------------------------

  @JsonProperty
  @JacksonXmlProperty(isAttribute = true)
  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  @JsonProperty
  @JacksonXmlProperty(isAttribute = true)
  public TranslationProperty getProperty() {
    return property;
  }

  public void setProperty(TranslationProperty property) {
    this.property = property;
  }

  @JsonProperty
  @JacksonXmlProperty(isAttribute = true)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("locale", locale)
        .add("property", property)
        .add("value", value)
        .toString();
  }
}
