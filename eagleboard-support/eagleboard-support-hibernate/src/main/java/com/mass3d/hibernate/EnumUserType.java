package com.mass3d.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Template for storing enums. Borrowed from http://community.jboss.org/wiki/
 * UserTypeforpersistinganEnumwithaVARCHARcolumn
 */
public class EnumUserType<E extends Enum<E>>
    implements UserType {

  private static final int[] SQL_TYPES = {Types.VARCHAR};
  private Class<E> clazz = null;

  protected EnumUserType(Class<E> c) {
    this.clazz = c;
  }

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class<?> returnedClass() {
    return clazz;
  }

  @Override
  public Object nullSafeGet(ResultSet resultSet, String[] names,
      SharedSessionContractImplementor impl, Object owner)
      throws HibernateException, SQLException {
    String name = resultSet.getString(names[0]);
    E result = null;
    if (!resultSet.wasNull()) {
      result = Enum.valueOf(clazz, name);
    }
    return result;
  }

  @Override
  public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index,
      SharedSessionContractImplementor impl)
      throws HibernateException, SQLException {
    if (null == value) {
      preparedStatement.setNull(index, Types.VARCHAR);
    } else {
      preparedStatement.setString(index, ((Enum<?>) value).name());
    }
  }

  @Override
  public Object deepCopy(Object value)
      throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Object assemble(Serializable cached, Object owner)
      throws HibernateException {
    return cached;
  }

  @Override
  public Serializable disassemble(Object value)
      throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public Object replace(Object original, Object target, Object owner)
      throws HibernateException {
    return original;
  }

  @Override
  public int hashCode(Object x)
      throws HibernateException {
    return x.hashCode();
  }

  @Override
  public boolean equals(Object x, Object y)
      throws HibernateException {
    if (x == y) {
      return true;
    }
    if (null == x || null == y) {
      return false;
    }
    return x.equals(y);
  }
}