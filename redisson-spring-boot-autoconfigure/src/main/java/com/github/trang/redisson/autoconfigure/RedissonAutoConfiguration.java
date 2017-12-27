package com.github.trang.redisson.autoconfigure;

import com.github.trang.redisson.autoconfigure.RedissonProperties.Cluster;
import com.github.trang.redisson.autoconfigure.RedissonProperties.MasterSlave;
import com.github.trang.redisson.autoconfigure.RedissonProperties.Sentinel;
import com.github.trang.redisson.autoconfigure.RedissonProperties.Single;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 自动配置
 *
 * @author trang
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnProperty(prefix = "spring.redisson", name = "type")
@AutoConfigureBefore(CacheAutoConfiguration.class)
@EnableConfigurationProperties(RedissonProperties.class)
@Slf4j
public class RedissonAutoConfiguration {

    private final RedissonProperties redissonProperties;

    public RedissonAutoConfiguration(RedissonProperties redissonProperties) {
        this.redissonProperties = redissonProperties;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redisson() {
        log.debug("redisson-client init...");
        Config config = new Config();
        configGlobal(config);
        switch (redissonProperties.getType()) {
            case SINGLE:
                configSingle(config);
                break;
            case CLUSTER:
                configCluster(config);
                break;
            case SENTINEL:
                configSentinel(config);
                break;
            case MASTERSLAVE:
                configMasterSlave(config);
                break;
            default:
                throw new IllegalArgumentException("illegal parameter: " + redissonProperties.getType());
        }
        return Redisson.create(config);
    }

    private void configGlobal(Config config) {
        config.setCodec(redissonProperties.getCodec())
                .setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setExecutor(redissonProperties.getExecutor())
                .setKeepPubSubOrder(redissonProperties.isKeepPubSubOrder())
                .setUseLinuxNativeEpoll(redissonProperties.isUseLinuxNativeEpoll())
                .setEventLoopGroup(redissonProperties.getEventLoopGroup())
                .setCodecProvider(redissonProperties.getCodecProvider())
                .setResolverProvider(redissonProperties.getResolverProvider())
                .setLockWatchdogTimeout(redissonProperties.getLockWatchdogTimeout())
                .setRedissonReferenceEnabled(redissonProperties.isRedissonReferenceEnabled());
    }

    private void configSingle(Config config) {
        Single properties = redissonProperties.getSingle();
        config.useSingleServer()
                .setAddress(properties.getAddress())
                .setDatabase(properties.getDatabase())
                .setConnectionMinimumIdleSize(properties.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(properties.getConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize());
    }

    private void configCluster(Config config) {
        Cluster properties = redissonProperties.getCluster();
        config.useClusterServers()
                .addNodeAddress(properties.getNodeAddresses())
                .setReadMode(properties.getReadMode())
                .setScanInterval(properties.getScanInterval())
                .setLoadBalancer(properties.getLoadBalancer())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSlaveSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSlaveSubscriptionConnectionPoolSize());
    }

    private void configMasterSlave(Config config) {
        MasterSlave properties = redissonProperties.getMasterSlave();
        config.useMasterSlaveServers()
                .setMasterAddress(properties.getMasterAddress())
                .addSlaveAddress(properties.getSlaveAddresses())
                .setDatabase(properties.getDatabase())
                .setReadMode(properties.getReadMode())
                .setLoadBalancer(properties.getLoadBalancer())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSlaveSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSlaveSubscriptionConnectionPoolSize());
    }

    private void configSentinel(Config config) {
        Sentinel properties = redissonProperties.getSentinel();
        config.useSentinelServers()
                .setMasterName(properties.getMasterName())
                .addSentinelAddress(properties.getSentinelAddresses())
                .setReadMode(properties.getReadMode())
                .setDatabase(properties.getDatabase())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setLoadBalancer(properties.getLoadBalancer())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSlaveSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSlaveSubscriptionConnectionPoolSize());
    }

}