package com.mass3d.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.common.BaseNameableObject;
import com.mass3d.common.DxfNamespaces;
import com.mass3d.common.MetadataObject;

@JacksonXmlRootElement(localName = "constant", namespace = DxfNamespaces.DXF_2_0)
//@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Constant
    extends BaseNameableObject implements MetadataObject {
  // -------------------------------------------------------------------------
  // Variables
  // -------------------------------------------------------------------------

  private double value;

  // -------------------------------------------------------------------------
  // Overridden methods
  // -------------------------------------------------------------------------

//  @Override
//  @Id
//  @GeneratedValue(strategy = GenerationType.AUTO
//  )
//  @Column(name = "constantid")
//  @JsonIgnore
//  public int getId() {
//    return super.getId();
//  }
//
//  @Override
//  @Column(name = "uid", unique = true, length = 11, nullable = false)
//  public String getUid() {
//    return super.getUid();
//  }
//
//  @Override
//  @Column(name = "code", unique = true, length = 50)
//  public String getCode() {
//    return super.getCode();
//  }
//
//  @Override
//  @ManyToOne
//  @JoinColumn(name = "lastupdatedby")
//  public User getLastUpdatedBy() {
//    return super.getLastUpdatedBy();
//  }
//
//  @Override
//  @ManyToOne
//  @JoinColumn(name = "userid")
//  public User getUser() {
//    return super.getUser();
//  }
//
//  @Override
//  @ManyToMany(cascade = CascadeType.ALL)
//  @JoinTable(name = "constantusergroupaccesses",
//      joinColumns = @JoinColumn(name = "constantid", referencedColumnName = "id"),
//      inverseJoinColumns = @JoinColumn(name = "usergroupaccessid", referencedColumnName = "id")
//  )
//  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//  public Set<UserGroupAccess> getUserGroupAccesses() {
//    return super.getUserGroupAccesses();
//  }
//
//  @Override
//  @ManyToMany(cascade = CascadeType.ALL)
//  @JoinTable(name = "constantuseraccesses",
//      joinColumns = @JoinColumn(name = "constantid", referencedColumnName = "id"),
//      inverseJoinColumns = @JoinColumn(name = "useraccessid", referencedColumnName = "id")
//  )
//  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//  public Set<UserAccess> getUserAccesses() {
//    return super.getUserAccesses();
//  }
//
  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  public Constant() {
  }

  public Constant(String name) {
    this.name = name;
  }

  public Constant(String name, double value) {
    this.name = name;
    this.value = value;
  }

  // -------------------------------------------------------------------------
  // Getter & Setter
  // -------------------------------------------------------------------------

  @JsonProperty
  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
