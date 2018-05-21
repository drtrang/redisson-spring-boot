package com.github.trang.redisson.autoconfigure;

<<<<<<< HEAD:redisson-spring-boot-autoconfigure/src/main/java/com/github/trang/redisson/autoconfigure/CompositeCacheManagerAutoConfiguration.java
import com.github.trang.autoconfigure.condition.ConditionalOnBeans;
=======
>>>>>>> 1.1.1:redisson-spring-boot2-autoconfigure/src/main/java/com/github/trang/redisson/autoconfigure/CompositeCacheManagerAutoConfiguration.java
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
<<<<<<< HEAD:redisson-spring-boot-autoconfigure/src/main/java/com/github/trang/redisson/autoconfigure/CompositeCacheManagerAutoConfiguration.java
=======
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
>>>>>>> 1.1.1:redisson-spring-boot2-autoconfigure/src/main/java/com/github/trang/redisson/autoconfigure/CompositeCacheManagerAutoConfiguration.java
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
<<<<<<< HEAD:redisson-spring-boot-autoconfigure/src/main/java/com/github/trang/redisson/autoconfigure/CompositeCacheManagerAutoConfiguration.java
@ConditionalOnBeans({CacheAspectSupport.class, CacheManager.class})
@ConditionalOnMissingBean({CompositeCacheManager.class, NoOpCacheManager.class})
@ConditionalOnProperty(prefix = "redisson.spring.cache-manager", name = "fallback-to-no-op-cache", havingValue = "true")
@AutoConfigureAfter({CacheAutoConfiguration.class, RedissonSpringAutoConfiguration.class})
=======
@ConditionalOnBean({CacheAspectSupport.class, CacheManager.class})
@ConditionalOnMissingBean({CompositeCacheManager.class, NoOpCacheManager.class})
<<<<<<< HEAD
@ConditionalOnProperty(prefix = "spring.cache", name = "fallbackToNoOpCache", havingValue = "true")
@AutoConfigureAfter({CacheAutoConfiguration.class, RedissonCacheManagerAutoConfiguration.class})
>>>>>>> 1.1.1:redisson-spring-boot2-autoconfigure/src/main/java/com/github/trang/redisson/autoconfigure/CompositeCacheManagerAutoConfiguration.java
=======
@ConditionalOnProperty(prefix = "redisson.spring.cache-manager", name = "fallback-to-no-op-cache", havingValue = "true")
@AutoConfigureAfter({CacheAutoConfiguration.class, RedissonSpringAutoConfiguration.class})
>>>>>>> 1.2.0
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