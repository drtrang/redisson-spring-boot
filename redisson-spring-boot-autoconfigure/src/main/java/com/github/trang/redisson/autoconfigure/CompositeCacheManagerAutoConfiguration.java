package com.github.trang.redisson.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * CompositeCacheManager 自动配置
 *
 * @author trang
 */
@Configuration
@ConditionalOnBean({CacheAspectSupport.class, CacheManager.class})
@ConditionalOnMissingBean({CompositeCacheManager.class, NoOpCacheManager.class})
@ConditionalOnProperty(prefix = "redisson.spring.cache-manager", name = "fallback-to-no-op-cache", havingValue = "true")
@AutoConfigureAfter({CacheAutoConfiguration.class, RedissonSpringAutoConfiguration.class})
@Slf4j
public class CompositeCacheManagerAutoConfiguration {

    private List<CacheManager> cacheManagers;

    public CompositeCacheManagerAutoConfiguration(ObjectProvider<List<CacheManager>> cacheManagers) {
        this.cacheManagers = cacheManagers.getIfAvailable();
    }

    /**
     * 构造 CacheManager，只有在配置 fallbackToNoOpCache=true 后才会创建
     *
     * @return CompositeCacheManager cacheManager
     */
    @Bean
    @Primary
    public CompositeCacheManager cacheManager() {
        log.info("composite cache-manager init...");
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        if (cacheManagers != null && !cacheManagers.isEmpty()) {
            compositeCacheManager.setCacheManagers(cacheManagers);
        }
        // 设置 NoOpCacheManager，判断当获取不存在的 Cache 时是否会抛出异常
        compositeCacheManager.setFallbackToNoOpCache(true);
        return compositeCacheManager;
    }

}