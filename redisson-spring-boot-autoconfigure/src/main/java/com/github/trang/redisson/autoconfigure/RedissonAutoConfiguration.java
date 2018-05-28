package com.github.trang.redisson.autoconfigure;

import java.util.List;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.trang.autoconfigure.Customizer;
import com.github.trang.redisson.autoconfigure.RedissonProperties.ClusterServersConfig;
import com.github.trang.redisson.autoconfigure.RedissonProperties.MasterSlaveServersConfig;
import com.github.trang.redisson.autoconfigure.RedissonProperties.ReplicatedServersConfig;
import com.github.trang.redisson.autoconfigure.RedissonProperties.SentinelServersConfig;
import com.github.trang.redisson.autoconfigure.RedissonProperties.SingleServerConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * Redisson 自动配置
 *
 * @author trang
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnProperty(prefix = "redisson", name = "type")
@AutoConfigureBefore(CacheAutoConfiguration.class)
@EnableConfigurationProperties(RedissonProperties.class)
@Slf4j
public class RedissonAutoConfiguration {

    private RedissonProperties redissonProperties;
    private List<Customizer<Config>> redissonCustomizers;

    public RedissonAutoConfiguration(RedissonProperties redissonProperties,
                                     ObjectProvider<List<Customizer<Config>>> customizersProvider) {
        this.redissonProperties = redissonProperties;
        this.redissonCustomizers = customizersProvider.getIfAvailable();
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
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
            case MASTER_SLAVE:
                configMasterSlave(config);
                break;
            case SENTINEL:
                configSentinel(config);
                break;
            case REPLICATED:
                configReplicated(config);
                break;
            default:
                throw new IllegalArgumentException("illegal redisson type: " + redissonProperties.getType());
        }
        // 用户自定义配置，拥有最高优先级
        redissonCustomizers.forEach(customizer -> customizer.customize(config));
        return Redisson.create(config);
    }

    private void configGlobal(Config config) {
        config.setCodec(redissonProperties.getCodec().getInstance())
                .setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setExecutor(redissonProperties.getExecutor())
                .setKeepPubSubOrder(redissonProperties.isKeepPubSubOrder())
                .setTransportMode(redissonProperties.getTransportMode())
                .setEventLoopGroup(redissonProperties.getEventLoopGroup())
                .setReferenceCodecProvider(redissonProperties.getReferenceCodecProvider())
                .setLockWatchdogTimeout(redissonProperties.getLockWatchdogTimeout())
                .setAddressResolverGroupFactory(redissonProperties.getAddressResolverGroupFactory().getInstance())
                .setReferenceEnabled(redissonProperties.isReferenceEnabled());
    }

    private void configSingle(Config config) {
        SingleServerConfig properties = redissonProperties.getSingle();
        config.useSingleServer()
                // BaseConfig
                .setPassword(properties.getPassword())
                .setSubscriptionsPerConnection(properties.getSubscriptionsPerConnection())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setTimeout(properties.getTimeout())
                .setClientName(properties.getClientName())
                .setPingTimeout(properties.getPingTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setIdleConnectionTimeout(properties.getIdleConnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTrustStore())
                .setSslTruststorePassword(properties.getSslKeystorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // SingleServerConfig
                .setAddress(properties.getAddress())
                .setDatabase(properties.getDatabase())
                .setConnectionMinimumIdleSize(properties.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(properties.getConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval());
    }

    private void configCluster(Config config) {
        ClusterServersConfig properties = redissonProperties.getCluster();
        config.useClusterServers()
                // BaseConfig
                .setPassword(properties.getPassword())
                .setSubscriptionsPerConnection(properties.getSubscriptionsPerConnection())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setTimeout(properties.getTimeout())
                .setClientName(properties.getClientName())
                .setPingTimeout(properties.getPingTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setIdleConnectionTimeout(properties.getIdleConnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTrustStore())
                .setSslTruststorePassword(properties.getSslKeystorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // BaseMasterSlaveServersConfig
                .setLoadBalancer(properties.getLoadBalancer().getInstance())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setFailedSlaveCheckInterval(properties.getFailedSlaveCheckInterval())
                .setFailedSlaveReconnectionInterval(properties.getFailedSlaveReconnectionInterval())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval())
                // ClusterServersConfig
                .addNodeAddress(properties.getNodeAddresses())
                .setScanInterval(properties.getScanInterval());
    }

    private void configMasterSlave(Config config) {
        MasterSlaveServersConfig properties = redissonProperties.getMasterSlave();
        config.useMasterSlaveServers()
                // BaseConfig
                .setPassword(properties.getPassword())
                .setSubscriptionsPerConnection(properties.getSubscriptionsPerConnection())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setTimeout(properties.getTimeout())
                .setClientName(properties.getClientName())
                .setPingTimeout(properties.getPingTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setIdleConnectionTimeout(properties.getIdleConnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTrustStore())
                .setSslTruststorePassword(properties.getSslKeystorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // BaseMasterSlaveServersConfig
                .setLoadBalancer(properties.getLoadBalancer().getInstance())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setFailedSlaveCheckInterval(properties.getFailedSlaveCheckInterval())
                .setFailedSlaveReconnectionInterval(properties.getFailedSlaveReconnectionInterval())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval())
                // MasterSlaveServersConfig
                .setMasterAddress(properties.getMasterAddress())
                .addSlaveAddress(properties.getSlaveAddresses())
                .setDatabase(properties.getDatabase());
    }

    private void configSentinel(Config config) {
        SentinelServersConfig properties = redissonProperties.getSentinel();
        config.useSentinelServers()
                // BaseConfig
                .setPassword(properties.getPassword())
                .setSubscriptionsPerConnection(properties.getSubscriptionsPerConnection())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setTimeout(properties.getTimeout())
                .setClientName(properties.getClientName())
                .setPingTimeout(properties.getPingTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setIdleConnectionTimeout(properties.getIdleConnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTrustStore())
                .setSslTruststorePassword(properties.getSslKeystorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // BaseMasterSlaveServersConfig
                .setLoadBalancer(properties.getLoadBalancer().getInstance())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setFailedSlaveCheckInterval(properties.getFailedSlaveCheckInterval())
                .setFailedSlaveReconnectionInterval(properties.getFailedSlaveReconnectionInterval())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval())
                // SentinelServersConfig
                .addSentinelAddress(properties.getSentinelAddresses())
                .setMasterName(properties.getMasterName())
                .setScanInterval(properties.getScanInterval())
                .setDatabase(properties.getDatabase());
    }

    private void configReplicated(Config config) {
        ReplicatedServersConfig properties = redissonProperties.getReplicated();
        config.useReplicatedServers()
                // BaseConfig
                .setPassword(properties.getPassword())
                .setSubscriptionsPerConnection(properties.getSubscriptionsPerConnection())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setTimeout(properties.getTimeout())
                .setClientName(properties.getClientName())
                .setPingTimeout(properties.getPingTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setIdleConnectionTimeout(properties.getIdleConnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTrustStore())
                .setSslTruststorePassword(properties.getSslKeystorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // BaseMasterSlaveServersConfig
                .setLoadBalancer(properties.getLoadBalancer().getInstance())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setFailedSlaveCheckInterval(properties.getFailedSlaveCheckInterval())
                .setFailedSlaveReconnectionInterval(properties.getFailedSlaveReconnectionInterval())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval())
                // ReplicatedServersConfig
                .addNodeAddress(properties.getNodeAddresses())
                .setScanInterval(properties.getScanInterval())
                .setDatabase(properties.getDatabase());
    }

}