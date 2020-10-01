package com.mass3d.cache;

/**
 * Provides cache builder to build instances.
 *
 */
public interface CacheProvider {

  /**
   * Creates a new {@link CacheBuilder} that can be used to build a cache that stores the valueType
   * specified.
   *
   * @param valueType The class type of values to be stored in cache.
   * @return A cache builder instance for the specified value type. Returns a {@link CacheBuilder}
   */
  public <V> CacheBuilder<V> newCacheBuilder(Class<V> valueType);
}