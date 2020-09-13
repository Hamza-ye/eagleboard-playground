package com.mass3d.support.hibernate.exception;

import org.springframework.security.access.AccessDeniedException;

public class CreateAccessDeniedException extends AccessDeniedException {

  public CreateAccessDeniedException(String msg) {
    super(msg);
  }
}
