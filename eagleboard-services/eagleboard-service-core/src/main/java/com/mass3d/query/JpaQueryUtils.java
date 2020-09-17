package com.mass3d.query;

import com.mass3d.schema.Property;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

public class JpaQueryUtils
{
  public static final String HIBERNATE_CACHEABLE_HINT = "org.hibernate.cacheable";

  public static Function<Root<?>, Order> getOrders( CriteriaBuilder builder, String field )
  {
    Function<Root<?>, Order> order = root -> builder.asc( root.get( field ) );

    return order;
  }

  /**
   * Generate a String comparison Predicate base on input parameters.
   *
   * Example:  JpaUtils.stringPredicateCaseSensitive( builder, root.get( "name" ),key , JpaUtils.StringSearchMode.ANYWHERE ) )
   *
   * @param builder CriteriaBuilder
   * @param path Property Path for query
   * @param attrValue Value to check
   * @param searchMode JpaQueryUtils.StringSearchMode
   * @return
   */
  public static Predicate stringPredicateCaseSensitive( CriteriaBuilder builder, Expression<String> expressionPath, Object objectValue, StringSearchMode searchMode )
  {
    return  stringPredicate(  builder,  expressionPath, objectValue, searchMode, true );
  }

  /**
   * Generate a String comparison Predicate base on input parameters.
   *
   * Example:  JpaUtils.stringPredicateIgnoreCase( builder, root.get( "name" ),key , JpaUtils.StringSearchMode.ANYWHERE ) )
   *
   * @param builder CriteriaBuilder
   * @param path Property Path for query
   * @param attrValue Value to check
   * @param searchMode JpaQueryUtils.StringSearchMode
   * @return
   */
  public static Predicate stringPredicateIgnoreCase( CriteriaBuilder builder, Expression<String> expressionPath, Object objectValue, StringSearchMode searchMode )
  {
    return  stringPredicate(  builder,  expressionPath, objectValue, searchMode, false );
  }

  /**
   * Generate a String comparison Predicate base on input parameters.
   *
   * Example:  JpaUtils.stringPredicate( builder, root.get( "name" ), "%" + key + "%", JpaUtils.StringSearchMode.LIKE, false ) )
   *
   * @param builder CriteriaBuilder
   * @param path Property Path for query
   * @param attrValue Value to check
   * @param searchMode JpaQueryUtils.StringSearchMode
   * @param caseSesnitive is case sensitive
   * @return
   */
  private static Predicate stringPredicate( CriteriaBuilder builder, Expression<String> expressionPath, Object objectValue, StringSearchMode searchMode, boolean caseSesnitive )
  {
    Expression<String> path = expressionPath;
    Object attrValue = objectValue;

    if ( !caseSesnitive )
    {
      path = builder.lower( path );
      attrValue = ((String) attrValue).toLowerCase( LocaleContextHolder.getLocale() );
    }

    switch ( searchMode )
    {
      case EQUALS:
        return builder.equal( path, attrValue );
      case ENDING_LIKE:
        return builder.like( path, "%" + attrValue );
      case STARTING_LIKE:
        return builder.like( path, attrValue + "%" );
      case ANYWHERE:
        return builder.like( path, "%" + attrValue + "%" );
      case LIKE:
        return builder.like( path, (String) attrValue ); // assume user provide the wild cards
      default:
        throw new IllegalStateException( "expecting a search mode!" );
    }
  }

  /**
   * Use for generating search String predicate in JPA criteria query
   */
  public enum StringSearchMode
  {

    EQUALS( "eq" ), // Match exactly
    ANYWHERE( "any" ), // Like search with '%' prefix and suffix
    STARTING_LIKE( "sl" ), // Like search and add a '%' prefix before searching
    LIKE( "li" ), // User provides the wild card
    ENDING_LIKE( "el" ); // LIKE search and add a '%' suffix before searching

    private final String code;

    StringSearchMode( String code )
    {
      this.code = code;
    }

    public String getCode()
    {
      return code;
    }

    public static final StringSearchMode convert( String code )
    {
      for ( StringSearchMode searchMode : StringSearchMode.values() )
      {
        if ( searchMode.getCode().equals( code ) )
        {
          return searchMode;
        }
      }

      return EQUALS;
    }
  }

  /**
   * Use for parsing filter parameter for Object which doesn't extend IdentifiableObject.
   */
  public static Predicate getPredicate( CriteriaBuilder builder, Property property,  Path path, String operator, String value )
  {
    switch ( operator )
    {
      case "in" :
        return path.in( QueryUtils.parseValue( Collection.class, property.getKlass(), value ) );
      case "eq" :
        return  builder.equal( path, QueryUtils.parseValue( property.getKlass(), value )  );
      default:
        throw new QueryParserException( "Query operator is not supported : " + operator );
    }
  }

  /**
   * Creates the query language order expression without the leading <code>ORDER BY</code>.
   *
   * @param orders the orders that should be created to a string.
   * @param alias the entity alias that will be used for prefixing.
   * @return the string order expression or <code>null</code> if none should be used.
   */
  @Nullable
  public static String createOrderExpression( @Nullable List<com.mass3d.query.Order> orders, @Nullable String alias )
  {
    if ( orders == null )
    {
      return null;
    }

    return StringUtils
        .defaultIfEmpty( orders.stream().filter(com.mass3d.query.Order::isPersisted ).map( o -> {
      final StringBuilder sb = new StringBuilder();
      final boolean ignoreCase = isIgnoreCase( o );

      if ( ignoreCase )
      {
        sb.append( "lower(" );
      }

      if ( alias != null )
      {
        sb.append( alias ).append( '.' );
      }

      sb.append( o.getProperty().getName() );

      if ( ignoreCase )
      {
        sb.append( ")" );
      }

      sb.append( ' ' );
      sb.append( o.isAscending() ? "asc" : "desc" );

      return sb.toString();
    } ).collect( Collectors.joining( "," ) ), null );
  }

  /**
   * Creates the query language order expression for selects that must be selected in order to
   * be able to order by these expressions. This is required for ordering on case insensitive
   * expressions since
   *
   * @param orders the orders that should be created to a string.
   * @param alias  the entity alias that will be used for prefixing.
   * @return the string order expression selects or <code>null</code> if none should be used.
   */
  @Nullable
  public static String createSelectOrderExpression( @Nullable List<com.mass3d.query.Order> orders, @Nullable String alias )
  {
    if ( orders == null )
    {
      return null;
    }

    return StringUtils.defaultIfEmpty( orders.stream().filter( o -> o.isPersisted() && isIgnoreCase( o ) ).map( o -> {
      final StringBuilder sb = new StringBuilder( "lower(" );

      if ( alias != null )
      {
        sb.append( alias ).append( '.' );
      }

      sb.append( o.getProperty().getName() ).append( ')' );

      return sb.toString();
    } ).collect( Collectors.joining( "," ) ), null );
  }

  private static boolean isIgnoreCase(com.mass3d.query.Order o )
  {
    return o.isIgnoreCase() && String.class == o.getProperty().getKlass();
  }
}
