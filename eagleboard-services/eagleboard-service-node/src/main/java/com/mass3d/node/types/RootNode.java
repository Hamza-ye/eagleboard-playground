package com.mass3d.node.types;

import com.mass3d.node.Node;
import com.mass3d.node.config.Config;

public class RootNode extends ComplexNode {

  private final Config config = new Config();
  private String defaultNamespace;

  public RootNode(String name) {
    super(name);
  }

  public RootNode(Node node) {
    super(node.getName());
    setNamespace(node.getNamespace());
    setComment(node.getComment());
    addChildren(node.getChildren());
  }

  public String getDefaultNamespace() {
    return defaultNamespace;
  }

  public void setDefaultNamespace(String defaultNamespace) {
    this.defaultNamespace = defaultNamespace;
  }

  public Config getConfig() {
    return config;
  }
}
