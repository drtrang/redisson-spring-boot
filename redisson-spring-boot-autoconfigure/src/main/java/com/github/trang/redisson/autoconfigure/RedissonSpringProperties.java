package com.github.trang.redisson.autoconfigure;

import java.util.HashMap;
import java.util.Map;

import org.redisson.spring.cache.CacheConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.github.trang.redisson.autoconfigure.enums.CodecType;

import lombok.Getter;
import lombok.Setter;

/**
 * Redisson Spring 自动配置属性
 *
 * @author trang
 */
@ConfigurationProperties(prefix = "spring.redisson")
@Getter
@Setter
public class RedissonSpringProperties {

    /** Redisson CacheManager 配置 */
    @NestedConfigurationProperty
    private RedissonCacheManagerProperties cacheManager = new RedissonCacheManagerProperties();

    /** Redisson TransactionManager 配置 */
    @NestedConfigurationProperty
    private RedissonTransactionManagerProperties transaction = new RedissonTransactionManagerProperties();

    @Getter
    @Setter
    public static class RedissonCacheManagerProperties {
        /** 是否开启 RedissonSpringCacheManager，默认值：true */
        private boolean enabled = true;
        /** 是否缓存 null 值，默认值：true */
        private boolean allowNullValues = true;
        /** 序列化类型 */
        private CodecType codec;
        /** RedissonCache 配置 */
        private Map<String, CacheConfig> configs = new HashMap<>();
        /** 是否开启动态缓存，默认值：false */
        private boolean dynamic = false;
        /** 缓存配置路径 */
        private String configLocation;
        /** 是否回滚到 NoOpCacheManager，默认值：true */
        private boolean fallbackToNoOpCache = true;
    }

    @Getter
    @Setter
    public static class RedissonTransactionManagerProperties {
        /** 是否开启 RedissonTransactionManager，默认值：false */
        private boolean enabled = false;
    }

}