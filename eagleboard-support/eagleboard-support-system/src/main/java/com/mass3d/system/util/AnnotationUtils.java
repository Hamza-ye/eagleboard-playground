package com.mass3d.system.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtils {

  /**
   * Returns methods on the given target object which are annotated with the annotation of the given
   * class.
   *
   * @param target the target object.
   * @param annotationType the annotation class type.
   * @return a list of methods annotated with the given annotation.
   */
  public static List<Method> getAnnotatedMethods(Object target,
      Class<? extends Annotation> annotationType) {
    final List<Method> methods = new ArrayList<>();

    if (target == null || annotationType == null) {
      return methods;
    }

    for (Method method : target.getClass().getMethods()) {
      Annotation annotation = org.springframework.core.annotation.AnnotationUtils
          .findAnnotation(method, annotationType);

      if (annotation != null) {
        methods.add(method);
      }
    }

    return methods;
  }

  /**
   * Check to see if annotation is present on a given Class, take into account class hierarchy.
   *
   * @param klass Class
   * @param annotationType Annotation
   * @return true/false depending on if annotation is present
   */
  public static boolean isAnnotationPresent(Class<?> klass,
      Class<? extends Annotation> annotationType) {
    return org.springframework.core.annotation.AnnotationUtils.findAnnotation(klass, annotationType)
        != null;
  }

  /**
   * Check to see if annotation is present on a given Method, take into account class hierarchy.
   *
   * @param method Method
   * @param annotationType Annotation
   * @return true/false depending on if annotation is present
   */
  public static boolean isAnnotationPresent(Method method,
      Class<? extends Annotation> annotationType) {
    return
        org.springframework.core.annotation.AnnotationUtils.findAnnotation(method, annotationType)
            != null;
  }

  /**
   * Gets annotation on a given Class, takes into account class hierarchy.
   *
   * @param klass Class
   * @param annotationType Annotation
   * @return Annotation instance on Class
   */
  public static <A extends Annotation> A getAnnotation(Class<?> klass, Class<A> annotationType) {
    return org.springframework.core.annotation.AnnotationUtils
        .findAnnotation(klass, annotationType);
  }

  /**
   * Gets annotation on a given Method, takes into account class hierarchy.
   *
   * @param method Method
   * @param annotationType Annotation
   * @return Annotation instance on Method
   */
  public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
    return org.springframework.core.annotation.AnnotationUtils
        .findAnnotation(method, annotationType);
  }
}
