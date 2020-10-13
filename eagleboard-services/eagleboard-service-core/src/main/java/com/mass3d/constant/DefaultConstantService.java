package com.mass3d.constant;

import com.mass3d.common.IdentifiableObjectStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.constant.ConstantService")
//@Transactional
public class DefaultConstantService
    implements ConstantService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private IdentifiableObjectStore<Constant> constantStore;

  @Autowired
  public void setConstantStore(IdentifiableObjectStore<Constant> constantStore) {
    this.constantStore = constantStore;
  }

  // -------------------------------------------------------------------------
  // Constant
  // -------------------------------------------------------------------------

  @Override
  @Transactional
  public Long saveConstant(Constant constant) {
    constantStore.save(constant);
    return constant.getId();
  }

  @Override
  @Transactional
  public void updateConstant(Constant constant) {
    constantStore.update(constant);
  }

  @Override
  @Transactional
  public void deleteConstant(Constant constant) {
    constantStore.delete(constant);
  }

  @Override
  @Transactional(readOnly = true)
  public Constant getConstant(Long constantId) {
    return constantStore.get(constantId);
  }

  @Override
  @Transactional(readOnly = true)
  public Constant getConstant(String uid) {
    return constantStore.getByUid(uid);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Constant> getAllConstants() {
    return constantStore.getAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Double> getConstantMap() {
    Map<String, Double> map = new HashMap<>();

    for (Constant constant : getAllConstants()) {
      map.put(constant.getUid(), constant.getValue());
    }

    return map;
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Double> getConstantParameterMap() {
    Map<String, Double> map = new HashMap<>();

    for (Constant constant : getAllConstants()) {
      map.put(constant.getName(), constant.getValue());
    }

    return map;
  }

  // -------------------------------------------------------------------------
  // Constant expanding
  // -------------------------------------------------------------------------

  @Override
  @Transactional(readOnly = true)
  public int getConstantCount() {
    return constantStore.getCount();
  }

  @Override
  @Transactional(readOnly = true)
  public int getConstantCountByName(String name) {
    return constantStore.getCountLikeName(name);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Constant> getConstantsBetween(int first, int max) {
    return constantStore.getAllOrderedName(first, max);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Constant> getConstantsBetweenByName(String name, int first, int max) {
    return constantStore.getAllLikeName(name, first, max);
  }
}