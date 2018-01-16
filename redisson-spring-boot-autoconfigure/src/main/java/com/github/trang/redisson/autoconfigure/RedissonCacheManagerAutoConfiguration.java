package com.github.trang.redisson.autoconfigure;

import com.github.trang.autoconfigure.condition.ConditionalOnBeans;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.HashMap;
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
    private CacheManagerCustomizers customizerInvoker;

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
        // 构造 ConfigMap
        Map<String, CacheConfig> config = new HashMap<>(cacheProperties.getConfigs());
        // 创建一个名称为 default 的缓存，过期时间为 2 小时，最长空闲时为 10 分钟。
        // 相当于 GuavaCache 的 expireAfterWrite 和 expireAfterAccess
        config.putIfAbsent("default", new CacheConfig(2 * 60 * 60 * 1000, 10 * 60 * 1000));
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redisson, config);
        // RedissonSpringCacheManager 中的 dynamic 属性默认为 true
        // 当获取不存在的 Cache 时会自动创建一个永不过期的 Cache，通过设置 CacheNames 禁用该功能
        // 设置 CacheNames 后，当 Redisson 中不存在以 CacheName 命名的 Cache 时，则会创建一个永不过期的 Cache
        if (!cacheProperties.isDynamic()) {
            cacheManager.setCacheNames(cacheProperties.getConfigs().keySet());
        }
        cacheManager.setAllowNullValues(cacheProperties.isAllowNullValues());
        cacheManager.setCodec(cacheProperties.getCodec());
        cacheManager.setConfigLocation(cacheProperties.getConfigLocation());
        return customizerInvoker.customize(cacheManager);
    }

    @Autowired
    public void setCacheProperties(RedissonCacheManagerProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Autowired(required = false)
    public void setCustomizerInvoker(CacheManagerCustomizers customizerInvoker) {
        this.customizerInvoker = customizerInvoker;
    }

}