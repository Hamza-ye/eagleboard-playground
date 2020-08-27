package com.mass3d.api.common;

public class BaseLinkableObject
    implements LinkableObject {

  /**
   * As part of the serializing process, this field can be set to indicate a link to this
   * identifiable object (will be used on the web layer for navigating the REST API)
   */
  private transient String href;

  @Override
  public String getHref() {
    return href;
  }

  @Override
  public void setHref(String href) {
    this.href = href;
  }
}
