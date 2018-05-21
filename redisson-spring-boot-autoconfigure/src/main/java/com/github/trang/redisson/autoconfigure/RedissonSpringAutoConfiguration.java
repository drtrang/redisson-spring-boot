package com.github.trang.redisson.autoconfigure;

import com.github.trang.redisson.autoconfigure.RedissonSpringProperties.RedissonCacheManagerProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.transaction.RedissonTransactionManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * RedissonSpring 自动配置
 *
 * @author trang
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnBean(RedissonClient.class)
@AutoConfigureAfter({CacheAutoConfiguration.class, RedissonAutoConfiguration.class})
@EnableConfigurationProperties(RedissonSpringProperties.class)
@Slf4j
public class RedissonSpringAutoConfiguration {

    private RedissonSpringProperties springProperties;
    private CacheManagerCustomizers customizers;

    public RedissonSpringAutoConfiguration(RedissonSpringProperties springProperties,
                                           CacheManagerCustomizers customizers) {
        this.springProperties = springProperties;
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
    @ConditionalOnBean(CacheAspectSupport.class)
    @ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
    public RedissonSpringCacheManager redissonSpringCacheManager(RedissonClient redisson) {
        log.info("redisson cache-manager init...");
        RedissonCacheManagerProperties cacheManagerProperties = springProperties.getCacheManager();
        // 获取 ConfigMap
        // CacheConfig:
        //   ttl         过期时间，key 写入一定时间后删除，相当于 GuavaCache 的 expireAfterWrite
        //   maxIdleTime 最大空闲时间，key 一定时间内没有被访问后删除，相当于 GuavaCache 的 expireAfterAccess
        //   maxIdleTime 最大数量，达到一定数量后删除一部分 key，基于 LRU 算法
        Map<String, CacheConfig> config = cacheManagerProperties.getConfigs();
        // 创建 CacheManager，ConfigMap 会转换为 Cache
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redisson, config);
        // RedissonSpringCacheManager 中的 dynamic 属性默认为 true，个人认为这样不合理，会导致滥用缓存
        // 所以 starter 中 dynamic 的默认值为 false，要求获取的 Cache 必须预先配置，否则会抛出异常
        // 当然，你也可以手动开启 dynamic 功能
        if (!cacheManagerProperties.isDynamic()) {
            cacheManager.setCacheNames(cacheManagerProperties.getConfigs().keySet());
        }
        if (cacheManagerProperties.getCodec() != null) {
            cacheManager.setCodec(cacheManagerProperties.getCodec().getInstance());
        }
        if (cacheManagerProperties.getConfigLocation() != null && !cacheManagerProperties.getConfigLocation().isEmpty()) {
            cacheManager.setConfigLocation(cacheManagerProperties.getConfigLocation());
        }
        cacheManager.setAllowNullValues(cacheManagerProperties.isAllowNullValues());
        return customizers.customize(cacheManager);
    }

    /**
     * 构造 RedissonTransactionManager
     *
     * @param redisson redisson 客户端
     * @return RedissonTransactionManager redissonTransactionManager
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "redisson.spring.transaction", name = "enabled", havingValue = "true")
    public RedissonTransactionManager redissonTransactionManager(RedissonClient redisson) {
        return new RedissonTransactionManager(redisson);
    }

}