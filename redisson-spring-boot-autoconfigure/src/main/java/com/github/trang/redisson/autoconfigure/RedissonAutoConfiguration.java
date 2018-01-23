package com.github.trang.redisson.autoconfigure;

import com.github.trang.autoconfigure.Customizer;
import com.github.trang.redisson.autoconfigure.RedissonProperties.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

    private RedissonProperties redissonProperties;
    private List<Customizer<Config>> customizers;

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
                throw new IllegalArgumentException("illegal parameter: " + redissonProperties.getType());
        }
        if (customizers != null && !customizers.isEmpty()) {
            for (Customizer<Config> customizer : customizers) {
                customizer.customize(config);
            }
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
                .setReferenceCodecProvider(redissonProperties.getReferenceCodecProvider())
                .setLockWatchdogTimeout(redissonProperties.getLockWatchdogTimeout())
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
                .setFailedAttempts(properties.getFailedAttempts())
                .setReconnectionTimeout(properties.getReconnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTruststore())
                .setSslTruststorePassword(properties.getSslTruststorePassword())
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
                .setDnsMonitoring(properties.isDnsMonitoring())
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
                .setFailedAttempts(properties.getFailedAttempts())
                .setReconnectionTimeout(properties.getReconnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTruststore())
                .setSslTruststorePassword(properties.getSslTruststorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // ClusterServersConfig
                .addNodeAddress(properties.getNodeAddresses())
                .setScanInterval(properties.getScanInterval())
                .setLoadBalancer(properties.getLoadBalancer())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval());
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
                .setFailedAttempts(properties.getFailedAttempts())
                .setReconnectionTimeout(properties.getReconnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTruststore())
                .setSslTruststorePassword(properties.getSslTruststorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // MasterSlaveServersConfig
                .setMasterAddress(properties.getMasterAddress())
                .addSlaveAddress(properties.getSlaveAddresses())
                .setDatabase(properties.getDatabase())
                .setLoadBalancer(properties.getLoadBalancer())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval());
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
                .setFailedAttempts(properties.getFailedAttempts())
                .setReconnectionTimeout(properties.getReconnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTruststore())
                .setSslTruststorePassword(properties.getSslTruststorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // SentinelServersConfig
                .addSentinelAddress(properties.getSentinelAddresses())
                .setMasterName(properties.getMasterName())
                .setDatabase(properties.getDatabase())
                .setLoadBalancer(properties.getLoadBalancer())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval());
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
                .setFailedAttempts(properties.getFailedAttempts())
                .setReconnectionTimeout(properties.getReconnectionTimeout())
                .setSslEnableEndpointIdentification(properties.isSslEnableEndpointIdentification())
                .setSslProvider(properties.getSslProvider())
                .setSslTruststore(properties.getSslTruststore())
                .setSslTruststorePassword(properties.getSslTruststorePassword())
                .setSslKeystore(properties.getSslKeystore())
                .setSslKeystorePassword(properties.getSslKeystorePassword())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.isKeepAlive())
                .setTcpNoDelay(properties.isTcpNoDelay())
                // SentinelServersConfig
                .addNodeAddress(properties.getNodeAddresses())
                .setScanInterval(properties.getScanInterval())
                .setDatabase(properties.getDatabase())
                .setLoadBalancer(properties.getLoadBalancer())
                .setMasterConnectionMinimumIdleSize(properties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(properties.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(properties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(properties.getSlaveConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setReadMode(properties.getReadMode())
                .setSubscriptionMode(properties.getSubscriptionMode())
                .setDnsMonitoringInterval(properties.getDnsMonitoringInterval());
    }

    @Autowired
    public void setRedissonProperties(RedissonProperties redissonProperties) {
        this.redissonProperties = redissonProperties;
    }

    @Autowired
    public void setCustomizers(ObjectProvider<List<Customizer<Config>>> customizers) {
        this.customizers = customizers.getIfAvailable();
    }

}