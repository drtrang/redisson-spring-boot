package com.github.trang.redisson.autoconfigure;

import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import org.redisson.client.codec.Codec;
import org.redisson.codec.CodecProvider;
import org.redisson.codec.DefaultCodecProvider;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.redisson.connection.balancer.LoadBalancer;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;
import org.redisson.liveobject.provider.DefaultResolverProvider;
import org.redisson.liveobject.provider.ResolverProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.concurrent.ExecutorService;

/**
 * Redisson 自动配置属性
 *
 * @author trang
 */
@ConfigurationProperties(prefix = "spring.redisson")
@Getter
@Setter
public class RedissonProperties {

    private Integer threads = 0;
    private Integer nettyThreads = 0;
    private Codec codec = new JsonJacksonCodec();
    private ExecutorService executor;
    private EventLoopGroup eventLoopGroup;
    private CodecProvider codecProvider = new DefaultCodecProvider();
    private ResolverProvider resolverProvider = new DefaultResolverProvider();
    private boolean keepPubSubOrder = true;
    private boolean useLinuxNativeEpoll = false;
    private long lockWatchdogTimeout = 30 * 1000;
    private boolean redissonReferenceEnabled = true;

    private RedissonType type = RedissonType.SINGLE;
    private Long idleConnectionTimeout = 10000L;
    private Long pingTimeout = 1000L;
    private Long connectTimeout = 10000L;
    private Long timeout = 3000L;
    private Integer retryAttempts = 3;
    private Long retryInterval = 1500L;
    private Long reconnectionTimeout = 3000L;
    private Integer failedAttempts = 3;
    private String password;
    private Integer subscriptionsPerConnection = 5;
    private String clientName;

    @NestedConfigurationProperty
    private Single single = new Single();
    @NestedConfigurationProperty
    private Cluster cluster = new Cluster();
    @NestedConfigurationProperty
    private Sentinel sentinel = new Sentinel();
    @NestedConfigurationProperty
    private MasterSlave masterSlave = new MasterSlave();

    public enum RedissonType {
        SINGLE, CLUSTER, SENTINEL, MASTERSLAVE
    }

    @Getter @Setter
    public static class Single {
        private String address = "redis://127.0.0.1:6379";
        private Long scanInterval = 1000L;
        private Integer subscriptionConnectionMinimumIdleSize = 1;
        private Integer subscriptionConnectionPoolSize = 50;
        private Integer connectionMinimumIdleSize = 10;
        private Integer connectionPoolSize = 64;
        private Integer database = 0;
        private boolean dnsMonitoring = false;
        private Long dnsMonitoringInterval = 5000L;
    }

    @Getter @Setter
    public static class Cluster {
        private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        private Integer slaveSubscriptionConnectionMinimumIdleSize = 1;
        private Integer slaveSubscriptionConnectionPoolSize = 50;
        private Integer slaveConnectionMinimumIdleSize = 10;
        private Integer slaveConnectionPoolSize = 64;
        private Integer masterConnectionMinimumIdleSize = 10;
        private Integer masterConnectionPoolSize = 64;
        private ReadMode readMode = ReadMode.SLAVE;
        private String[] nodeAddresses;
        private Integer scanInterval = 1000;
    }

    @Getter @Setter
    public static class Sentinel {
        private String masterName = "mymaster";
        private String[] sentinelAddresses;
        private ReadMode readMode = ReadMode.SLAVE;
        private SubscriptionMode subscriptionMode;
        private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        private Integer slaveSubscriptionConnectionMinimumIdleSize = 1;
        private Integer slaveSubscriptionConnectionPoolSize = 50;
        private Integer slaveConnectionMinimumIdleSize = 10;
        private Integer slaveConnectionPoolSize = 64;
        private Integer masterConnectionMinimumIdleSize = 10;
        private Integer masterConnectionPoolSize = 64;
        private Long scanInterval = 1000L;
        private Integer database = 0;
    }

    @Getter @Setter
    public static class MasterSlave {
        private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        private Integer slaveSubscriptionConnectionMinimumIdleSize = 1;
        private Integer slaveSubscriptionConnectionPoolSize = 50;
        private Integer slaveConnectionMinimumIdleSize = 10;
        private Integer slaveConnectionPoolSize = 64;
        private Integer masterConnectionMinimumIdleSize = 10;
        private Integer masterConnectionPoolSize = 64;
        private ReadMode readMode = ReadMode.SLAVE;
        private String[] slaveAddresses;
        private String masterAddress;
        private Integer database = 0;
    }

}