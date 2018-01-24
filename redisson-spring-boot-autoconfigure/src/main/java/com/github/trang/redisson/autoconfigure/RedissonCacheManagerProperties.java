package com.github.trang.redisson.autoconfigure;

import com.github.trang.redisson.autoconfigure.enums.CodecType;
import lombok.Getter;
import lombok.Setter;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * RedissonCacheManager 自动配置属性
 *
 * @author trang
 */
@ConfigurationProperties(prefix = "spring.cache")
@Getter
@Setter
public class RedissonCacheManagerProperties {

    /** 是否缓存 null 值 */
    private boolean allowNullValues = true;
    /** 序列化类型 */
    private CodecType codec;
    /** RedissonCache 配置 */
    private Map<String, CacheConfig> configs = new HashMap<>();
    /** 是否开启动态缓存 */
    private boolean dynamic = true;
    /** 缓存配置路径 */
    private String configLocation;
    /** 是否回滚到 NoOpCacheManager */
    private boolean fallbackToNoOpCache = false;

}