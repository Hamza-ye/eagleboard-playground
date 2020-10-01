package com.mass3d.common.cache;

/**
 * CacheStrategies express web request caching settings. Note that {@link #RESPECT_SYSTEM_SETTING}
 * should only be used on a per-object-basis (i.e. never as a system wide setting).
 *
 */
public enum CacheStrategy {
  NO_CACHE,
  CACHE_15_MINUTES,
  CACHE_30_MINUTES,
  CACHE_1_HOUR,
  CACHE_6AM_TOMORROW,
  CACHE_TWO_WEEKS,
  RESPECT_SYSTEM_SETTING
}
