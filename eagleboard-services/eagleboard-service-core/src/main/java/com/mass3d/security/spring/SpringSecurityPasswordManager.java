package com.mass3d.security.spring;

import com.mass3d.security.PasswordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityPasswordManager
    implements PasswordManager {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  @Autowired
  private PasswordEncoder passwordEncoder;

  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  // -------------------------------------------------------------------------
  // PasswordManager implementation
  // -------------------------------------------------------------------------

  @Override
  public final String encode(String password) {
    return passwordEncoder.encode(password);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public String getPasswordEncoderClassName() {
    return passwordEncoder.getClass().getName();
  }
}
