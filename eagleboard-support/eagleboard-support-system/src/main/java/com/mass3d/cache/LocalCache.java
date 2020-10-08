package com.mass3d.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Local cache implementation of {@link Cache}. This implementation is backed by Caffeine library
 * which uses an in memory Map implementation.
 *
 */
public class LocalCache<V> implements Cache<V> {

  private com.github.benmanes.caffeine.cache.Cache<String, V> caffeineCache;

  private V defaultValue;

  /**
   * Constructor to instantiate LocalCache object.
   *
   * @param cacheBuilder CacheBuilder instance
   */
  public LocalCache(final CacheBuilder<V> cacheBuilder) {
    Caffeine<Object, Object> builder = Caffeine.newBuilder();

    if (cacheBuilder.isExpiryEnabled()) {
      if (cacheBuilder.isRefreshExpiryOnAccess()) {
        builder.expireAfterAccess(cacheBuilder.getExpiryInSeconds(), TimeUnit.SECONDS);
      } else {
        builder.expireAfterWrite(cacheBuilder.getExpiryInSeconds(), TimeUnit.SECONDS);
      }
    }
    if (cacheBuilder.getMaximumSize() > 0) {
      builder.maximumSize(cacheBuilder.getMaximumSize());
    }

    this.caffeineCache = builder.build();
    this.defaultValue = cacheBuilder.getDefaultValue();
  }

  @Override
  public Optional<V> getIfPresent(String key) {
    return Optional.ofNullable(caffeineCache.getIfPresent(key));
  }

  @Override
  public Optional<V> get(String key) {
    return Optional
        .ofNullable(Optional.ofNullable(caffeineCache.getIfPresent(key)).orElse(defaultValue));
  }

  @Override
  public Optional<V> get(String key, Function<String, V> mappingFunction) {
    if (null == mappingFunction) {
      throw new IllegalArgumentException("MappingFunction cannot be null");
    }
    return Optional
        .ofNullable(
            Optional.ofNullable(caffeineCache.get(key, mappingFunction)).orElse(defaultValue));
  }

  @Override
  public Collection<V> getAll() {
    return new ArrayList<V>(caffeineCache.asMap().values());
  }

  @Override
  public void put(String key, V value) {
    if (null == value) {
      throw new IllegalArgumentException("Value cannot be null");
    }
    caffeineCache.put(key, value);
  }


  @Override
  public void invalidate(String key) {
    caffeineCache.invalidate(key);
  }

  @Override
  public void invalidateAll() {
    caffeineCache.invalidateAll();
  }
}
