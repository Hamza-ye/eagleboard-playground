package com.mass3d.cache;

import com.mass3d.external.conf.DhisConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Provides cache builder to build instances.
 *
 */
@Component( "cacheProvider" )
public class DefaultCacheProvider implements CacheProvider {

  private DhisConfigurationProvider configurationProvider;

  private RedisTemplate<String, ?> redisTemplate;

  /**
   * Creates a new {@link CacheBuilder} that can be used to build a cache that stores the valueType
   * specified.
   *
   * @param valueType The class type of values to be stored in cache.
   * @return A cache builder instance for the specified value type. Returns a {@link CacheBuilder}
   */
  public <V> CacheBuilder<V> newCacheBuilder(Class<V> valueType) {
    return new CacheBuilder<V>(redisTemplate, configurationProvider);
  }

  @Autowired
  public void setConfigurationProvider(DhisConfigurationProvider configurationProvider) {
    this.configurationProvider = configurationProvider;
  }

  @Autowired(required = false)
  public void setRedisTemplate(RedisTemplate<String, ?> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

}