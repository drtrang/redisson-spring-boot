package com.github.trang.redisson.autoconfigure;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.transaction.RedissonTransactionManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import com.github.trang.autoconfigure.Customizer;
import com.github.trang.redisson.autoconfigure.RedissonSpringProperties.RedissonCacheManagerProperties;
import com.github.trang.redisson.autoconfigure.enums.CodecType;

import lombok.extern.slf4j.Slf4j;

/**
 * Redisson Spring 自动配置
 * <p>
 * 在 #{@link CacheAutoConfiguration} 之后加载的原因是 CacheAutoConfiguration 中有一些功能是我们需要的，
 * 如果先加载本类，那么 RedissonSpringCacheManager注册成功后，CacheAutoConfiguration 将不会加载，
 * 因为其加载条件是不存在 CacheManager
 *
 * @author trang
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnBean(RedissonClient.class)
@AutoConfigureAfter({CacheAutoConfiguration.class, TransactionAutoConfiguration.class})
@EnableConfigurationProperties(RedissonSpringProperties.class)
@Slf4j
public class RedissonSpringAutoConfiguration {

    private final RedissonSpringProperties redissonSpringProperties;
    private final List<Customizer<RedissonSpringCacheManager>> redissonSpringCacheManagerCustomizers;

    public RedissonSpringAutoConfiguration(RedissonSpringProperties redissonSpringProperties,
                                           ObjectProvider<List<Customizer<RedissonSpringCacheManager>>> customizersProvider) {
        this.redissonSpringProperties = redissonSpringProperties;
        this.redissonSpringCacheManagerCustomizers = customizersProvider.getIfAvailable(Collections::emptyList);
    }

    /**
     * 声明 RedissonSpringCacheManager
     *
     * @param redisson redisson 客户端
     * @return RedissonSpringCacheManager redissonSpringCacheManager
     */
    @Bean
    @ConditionalOnClass(CacheManager.class)
    @ConditionalOnBean(CacheAspectSupport.class)
    @ConditionalOnMissingBean(RedissonSpringCacheManager.class)
    @ConditionalOnProperty(prefix = "spring.redisson.cache-manager", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RedissonSpringCacheManager redissonSpringCacheManager(RedissonClient redisson) {
        logger.info("redisson cache-manager init...");
        RedissonCacheManagerProperties redissonCacheManagerProperties = redissonSpringProperties.getCacheManager();
        // 获取 ConfigMap
        // CacheConfig:
        //   ttl         过期时间，key 写入一定时间后删除，相当于 GuavaCache 的 expireAfterWrite
        //   maxIdleTime 最大空闲时间，key 一定时间内没有被访问后删除，相当于 GuavaCache 的 expireAfterAccess
        //   maxSize     最大数量，达到一定数量后删除一部分 key，基于 LRU 算法
        Map<String, CacheConfig> configs = redissonCacheManagerProperties.getConfigs();
        // 创建 CacheManager，ConfigMap 会转换为 Cache
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redisson, configs);
        cacheManager.setAllowNullValues(redissonCacheManagerProperties.isAllowNullValues());
        Optional.of(redissonCacheManagerProperties)
                .map(RedissonCacheManagerProperties::getCodec)
                .map(CodecType::getInstance)
                .ifPresent(cacheManager::setCodec);
        Optional.of(redissonCacheManagerProperties)
                .map(RedissonCacheManagerProperties::getConfigLocation)
                .filter(configLocation -> !StringUtils.isEmpty(configLocation))
                .ifPresent(cacheManager::setConfigLocation);
        // RedissonSpringCacheManager 中的 dynamic 属性默认为 true，即获取不存在的 Cache 时，Redisson 创建一个永不过期的 Cache 以供使用
        // 个人认为这样不合理，会导致缓存滥用，所以 starter 中 dynamic 的默认值为 false，当获取不存在的 Cache 时会抛出异常
        // 当然，你也可以手动开启 dynamic 功能
        Optional.of(redissonCacheManagerProperties)
                .filter(properties -> !properties.isDynamic())
                .map(RedissonCacheManagerProperties::getConfigs)
                .filter(map -> !map.isEmpty())
                .map(Map::keySet)
                .ifPresent(cacheManager::setCacheNames);
        // 定制化配置，拥有最高优先级，会覆盖之前已有的配置
        redissonSpringCacheManagerCustomizers.forEach(customizer -> customizer.customize(cacheManager));
        return cacheManager;
    }

    /**
     * 由于 #{@link CacheAutoConfiguration} 的加载顺序在本类之前，并且若不指定 spring.cache.type=none，
     * 默认会注册一个 #{@link ConcurrentMapCacheManager}，见 #{@link org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration}
     * <p>
     * 而本类又会注册一个 RedissonSpringCacheManager，两个 CacheManager 之间没有主次关系，这时 Spring 并不知道要用哪一个，
     * 所以会抛出一个 NoUniqueBeanDefinitionException 异常，见 #{@link CacheAspectSupport#afterSingletonsInstantiated()}
     * <p>
     * 之前的解决方式是将 RedissonSpringCacheManager 的 beanName 设置为 'cacheManager'，根据 Spring 的 bean 注册规则
     * RedissonSpringCacheManager 会将 ConcurrentMapCacheManager 覆盖掉，从而成为了唯一的 CacheManager，
     * 见 #{@link DefaultListableBeanFactory#registerBeanDefinition(String, BeanDefinition)}
     * <p>
     * 但是有一点点的不完美
     * 1. 通过 beanName='redissonSpringCacheManager' 找不到相应的 bean，只能通过 'cacheManager' 获取
     * 2. 结合 CompositeCacheManager 使用时，通过 beanName='cacheManager' 获取到的仍然是 RedissonSpringCacheManager
     * <p>
     * 所以为了解决这些小问题，让逻辑看起来更合理，祭出了这招
     * <p>
     * RedissonSpringCacheManager 按正常方式声明，beanName 为 'redissonSpringCacheManager'
     * <p>
     * 若不需要 CompositeCacheManager，则另外给 RedissonSpringCacheManager 一个 beanName 为 'cacheManager'，并且声明为 Primary，
     * 既覆盖了默认注册的 ConcurrentMapCacheManager 又声明了主 CacheManager，第一个问题迎刃而解
     * <p>
     * 若需要 CompositeCacheManager，则不执行上述步骤，而是将 CompositeCacheManager 声明为 'cacheManager'，并且给一个别名
     * 'compositeCacheManager'，这样第二个问题也解决了，并且更近一步，通过 beanName='compositeCacheManager' 也可获取到期望的 bean
     * <p>
     * 完美
     *
     * @param redissonSpringCacheManager redissonSpringCacheManager
     * @return RedissonSpringCacheManager cacheManager
     */
    @Primary
    @Bean("cacheManager")
    @ConditionalOnBean(RedissonSpringCacheManager.class)
    @ConditionalOnMissingBean(CompositeCacheManager.class)
    @ConditionalOnProperty(prefix = "spring.redisson.cache-manager", name = "fallback-to-no-op-cache", havingValue = "false")
    public RedissonSpringCacheManager primaryCacheManager(RedissonSpringCacheManager redissonSpringCacheManager) {
        return redissonSpringCacheManager;
    }

    /**
     * 声明 CompositeCacheManager
     * <p>
     * 1. 因为上面已经有 redissonSpringCacheManager 了，所以这里用 @Primary 修饰
     * 2. 注入 RedissonSpringCacheManager 而不是 CacheManager 的原因是此方法只为 RedissonSpringCacheManager 服务 :)
     *
     * @param redissonSpringCacheManager redissonSpringCacheManager
     * @return CompositeCacheManager cacheManager
     */
    @Primary
    @Bean({"cacheManager", "compositeCacheManager"})
    @ConditionalOnBean(RedissonSpringCacheManager.class)
    @ConditionalOnMissingBean(CompositeCacheManager.class)
    @ConditionalOnProperty(prefix = "spring.redisson.cache-manager", name = "fallback-to-no-op-cache", havingValue = "true", matchIfMissing = true)
    public CompositeCacheManager compositeCacheManager(RedissonSpringCacheManager redissonSpringCacheManager) {
        logger.info("composite cache-manager init...");
        // 设置 NoOpCacheManager，当获取不存在的 Cache 时不会抛出异常，而是穿透缓存
        CompositeCacheManager cacheManager = new CompositeCacheManager(redissonSpringCacheManager);
        cacheManager.setFallbackToNoOpCache(true);
        return cacheManager;
    }

    /**
     * 声明 RedissonTransactionManager
     * <p>
     * 加载顺序放到了 #{@link TransactionAutoConfiguration} 之后，避免影响
     * #{@link DataSourceTransactionManagerAutoConfiguration.DataSourceTransactionManagerConfiguration} 的加载
     *
     * @param redisson redisson 客户端
     * @return RedissonTransactionManager redissonTransactionManager
     */
    @Bean
    @ConditionalOnMissingBean(RedissonTransactionManager.class)
    @ConditionalOnClass(name = "org.springframework.transaction.PlatformTransactionManager")
    @ConditionalOnProperty(prefix = "spring.redisson.transaction", name = "enabled", havingValue = "true")
    public RedissonTransactionManager redissonTransactionManager(RedissonClient redisson) {
        return new RedissonTransactionManager(redisson);
    }

}