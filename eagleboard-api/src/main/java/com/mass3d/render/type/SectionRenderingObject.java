package com.mass3d.render.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.mass3d.common.DxfNamespaces;

/**
 * This class represents the renderingType of a ProgramStageSection
 */
public class SectionRenderingObject {

  /**
   * The renderingType of the ProgramStageSection
   */
  private SectionRenderingType type;

  public SectionRenderingObject() {
    this.type = SectionRenderingType.LISTING;
  }

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public SectionRenderingType getType() {
    return type;
  }

  public void setType(SectionRenderingType type) {
    this.type = type;
  }
}
