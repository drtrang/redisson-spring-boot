package com.github.trang.redisson.autoconfigure;

import com.github.trang.autoconfigure.condition.ConditionalOnBeans;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * RedissonCacheManager 自动配置
 *
 * @author trang
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnBeans({CacheAspectSupport.class, RedissonClient.class})
@ConditionalOnMissingBean(CacheManager.class)
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
@AutoConfigureAfter({CacheAutoConfiguration.class, RedissonAutoConfiguration.class})
@EnableConfigurationProperties(RedissonCacheManagerProperties.class)
@Slf4j
public class RedissonCacheManagerAutoConfiguration {

    private RedissonCacheManagerProperties cacheProperties;
    private CacheManagerCustomizers customizers;

    public RedissonCacheManagerAutoConfiguration(RedissonCacheManagerProperties cacheProperties,
                                                 CacheManagerCustomizers customizers) {
        this.cacheProperties = cacheProperties;
        this.customizers = customizers;
    }

    /**
     * 构造 RedissonSpringCacheManager
     *
     * @param redisson redisson 客户端
     * @return RedissonSpringCacheManager redissonSpringCacheManager
     */
    @Bean
    @ConditionalOnMissingBean
    public RedissonSpringCacheManager redissonCacheManager(RedissonClient redisson) {
        log.info("redisson cache-manager init...");
        // 获取 ConfigMap
        // CacheConfig:
        //   ttl         过期时间，key 写入一定时间后删除，相当于 GuavaCache 的 expireAfterWrite
        //   maxIdleTime 最大空闲时间，key 一定时间内没有被访问后删除，相当于 GuavaCache 的 expireAfterAccess
        //   maxIdleTime 最大数量，达到一定数量后删除一部分 key，基于 LRU 算法
        Map<String, CacheConfig> config = cacheProperties.getConfigs();
        // 创建 CacheManager，ConfigMap 会转换为 Cache
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redisson, config);
        // RedissonSpringCacheManager 中的 dynamic 属性默认为 true
        // 当获取不存在的 Cache 时会自动创建一个永不过期的 Cache，通过设置 CacheNames 禁用该功能
        // 设置 CacheNames 后，当 Redisson 中不存在以 CacheName 命名的 Cache 时，则会创建一个永不过期的 Cache
        if (!cacheProperties.isDynamic()) {
            cacheManager.setCacheNames(cacheProperties.getConfigs().keySet());
        }
        if (cacheProperties.getCodec() != null) {
            cacheManager.setCodec(cacheProperties.getCodec().getInstance());
        }
        if (cacheProperties.getConfigLocation() != null && !cacheProperties.getConfigLocation().isEmpty()) {
            cacheManager.setConfigLocation(cacheProperties.getConfigLocation());
        }
        cacheManager.setAllowNullValues(cacheProperties.isAllowNullValues());
        return customizers.customize(cacheManager);
    }

}