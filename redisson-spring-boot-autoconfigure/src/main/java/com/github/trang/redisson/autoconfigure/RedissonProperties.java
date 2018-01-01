package com.github.trang.redisson.autoconfigure;

import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import org.redisson.client.codec.Codec;
import org.redisson.codec.CodecProvider;
import org.redisson.codec.DefaultCodecProvider;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ReadMode;
import org.redisson.config.SslProvider;
import org.redisson.config.SubscriptionMode;
import org.redisson.connection.balancer.LoadBalancer;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;
import org.redisson.liveobject.provider.DefaultResolverProvider;
import org.redisson.liveobject.provider.ResolverProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.net.URI;
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

    /** 线程池数量，默认值：当前处理核数量*2 */
    private int threads = 0;
    /** Netty 线程池数量，默认值：当前处理核数量*2 */
    private int nettyThreads = 0;
    /** Redis 进行序列化和反序列化的类型，默认值：JsonJacksonCodec */
    private Codec codec = new JsonJacksonCodec();
    /** Codec 注册和获取功能的提供者，默认值：DefaultCodecProvider */
    private CodecProvider codecProvider = new DefaultCodecProvider();
    /** Resolver 注册和获取功能的提供者，默认值：DefaultResolverProvider */
    private ResolverProvider resolverProvider = new DefaultResolverProvider();
    /** 单独提供一个线程池实例 */
    private ExecutorService executor;
    /** Redisson参考功能的配置选项，默认值：true */
    private boolean redissonReferenceEnabled = true;
    /** 如果服务器的绑定地址是本地回路网络接口则自动激活一个 UNIX 域套接字，并同时采用epoll作为传输方式，默认值：false */
    private boolean useLinuxNativeEpoll = false;
    /** 单独指定一个 EventLoopGroup */
    private EventLoopGroup eventLoopGroup;
    /** 锁监视器的超时时间，默认值：30000 ms */
    private long lockWatchdogTimeout = 30 * 1000;
    /** 是否顺序处理或并发处理 PubSub 消息，默认值：true */
    private boolean keepPubSubOrder = true;

    /** Redis 服务端模式 */
    private RedissonType type = RedissonType.SINGLE;
    /** 单节点模式 */
    @NestedConfigurationProperty
    private SingleServerConfig single = new SingleServerConfig();
    /** 集群模式 */
    @NestedConfigurationProperty
    private ClusterServersConfig cluster = new ClusterServersConfig();
    /** 主从模式 */
    @NestedConfigurationProperty
    private MasterSlaveServersConfig masterSlave = new MasterSlaveServersConfig();
    /** 哨兵模式 */
    @NestedConfigurationProperty
    private SentinelServersConfig sentinel = new SentinelServersConfig();
    /** 云托管模式 */
    @NestedConfigurationProperty
    private ReplicatedServersConfig replicated = new ReplicatedServersConfig();

    /**
     * Redis 服务端模式
     */
    public enum RedissonType {
        /** 单节点模式 */
        SINGLE,
        /** 集群模式 */
        CLUSTER,
        /** 主从模式 */
        MASTER_SLAVE,
        /** 哨兵模式 */
        SENTINEL,
        /** 云托管模式 */
        REPLICATED
    }

    @Getter
    @Setter
    private static class BaseConfig {
        /** 连接空闲超时时间，默认值：10000 ms */
        private int idleConnectionTimeout = 10000;
        /** PING 操作的超时时间，默认值：1000 ms */
        private int pingTimeout = 1000;
        /** 连接超时时间，默认值：10000 ms */
        private int connectTimeout = 10000;
        /** 命令等待超时时间，，默认值：10000 ms */
        private int timeout = 3000;
        /** 命令失败重试次数，默认值：3 次 */
        private int retryAttempts = 3;
        /** 命令重试发送时间间隔，默认值：1500 ms */
        private int retryInterval = 1500;
        /** 重新连接时间间隔，默认值：3000 ms */
        private int reconnectionTimeout = 3000;
        /** 执行失败最大次数，默认值：3 次 */
        private int failedAttempts = 3;
        /** Redis 实例密码，默认值：null */
        private String password;
        /** 单个连接最大订阅数量，默认值：5 个*/
        private int subscriptionsPerConnection = 5;
        /** 客户端名称，默认值：null */
        private String clientName;
        /** 启用 SSL 终端识别，默认值：是 */
        private boolean sslEnableEndpointIdentification = true;
        /** SSL 实现方式，默认值：JDK */
        private SslProvider sslProvider = SslProvider.JDK;
        /** SSL 信任证书库路径，默认值：null */
        private URI sslTruststore;
        /** SSL 信任证书库密码，默认值：null */
        private String sslTruststorePassword;
        /** SSL 钥匙库路径，默认值：null */
        private URI sslKeystore;
        /** SSL 钥匙库密码，默认值：null */
        private String sslKeystorePassword;
        /** PING 命令的发送时间间隔，默认值：0 ms */
        private int pingConnectionInterval;
        /** 开启连接的 TCP KeppAlive 特性，默认值：false */
        private boolean keepAlive = false;
        /** 开启连接的 TCP NoDelay 特性，默认值：false */
        private boolean tcpNoDelay = false;
    }

    @Getter
    @Setter
    private static class BaseMasterSlaveServersConfig extends BaseConfig {
        /** 负载均衡算法，默认值：RoundRobinLoadBalancer */
        private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        /** 主节点最小空闲连接数，默认值：10 */
        private int masterConnectionMinimumIdleSize = 10;
        /** 主节点连接池大小，默认值：64 */
        private int masterConnectionPoolSize = 64;
        /** 从节点最小空闲连接数，默认值：10 */
        private int slaveConnectionMinimumIdleSize = 10;
        /** 从节点连接池大小，默认值：64 */
        private int slaveConnectionPoolSize = 64;
        /** 从节点发布和订阅连接的最小空闲连接数，默认值：1 */
        private int subscriptionConnectionMinimumIdleSize = 1;
        /** 从节点发布和订阅连接池大小，默认值：50 */
        private int subscriptionConnectionPoolSize = 50;
        /** 读取操作的负载均衡模式，默认值：SLAVE */
        private ReadMode readMode = ReadMode.SLAVE;
        /** 订阅操作的负载均衡模式，默认值：SLAVE */
        private SubscriptionMode subscriptionMode = SubscriptionMode.SLAVE;
        /** DNS 监测时间间隔，默认值：5000 ms */
        private long dnsMonitoringInterval = 5000;
    }

    @Getter
    @Setter
    public static class SingleServerConfig extends BaseConfig {
        /** 节点地址，格式：redis://host:port */
        private String address = "redis://127.0.0.1:6379";
        /** 数据库编号，默认值：0 */
        private int database = 0;
        /** 最小空闲连接数，默认值：10 */
        private int connectionMinimumIdleSize = 10;
        /** 连接池大小，默认值：64 */
        private int connectionPoolSize = 64;
        /** 发布和订阅连接的最小空闲连接数，默认值：1 */
        private int subscriptionConnectionMinimumIdleSize = 1;
        /** 发布和订阅连接池大小，默认值：50 */
        private int subscriptionConnectionPoolSize = 50;
        /** 是否启用 DNS 监测，默认值：true */
        private boolean dnsMonitoring = true;
        /** DNS 监测时间间隔，默认值：5000 ms */
        private long dnsMonitoringInterval = 5000L;
    }

    @Getter
    @Setter
    public static class ClusterServersConfig extends BaseMasterSlaveServersConfig {
        /** 集群节点地址，格式：redis://host:port */
        private String[] nodeAddresses;
        /** 集群扫描间隔时间，默认值：1000 ms */
        private int scanInterval = 1000;
    }

    @Getter
    @Setter
    public static class MasterSlaveServersConfig extends BaseMasterSlaveServersConfig {
        /** 主节点地址，格式：redis://host:port */
        private String masterAddress;
        /** 从节点地址，格式：redis://host:port */
        private String[] slaveAddresses;
        /** 数据库编号，默认值：0 */
        private int database = 0;
    }

    @Getter
    @Setter
    public static class SentinelServersConfig extends BaseMasterSlaveServersConfig {
        /** 哨兵节点地址，格式：redis://host:port */
        private String[] sentinelAddresses;
        /** 主服务器的名称，默认值：null */
        private String masterName;
        /** 数据库编号，默认值：0 */
        private int database = 0;
    }

    @Getter
    @Setter
    public static class ReplicatedServersConfig extends BaseMasterSlaveServersConfig {
        /** 集群节点地址，格式：redis://host:port */
        private String[] nodeAddresses;
        /** 主节点变化扫描间隔时间，默认值：1000 ms */
        private int scanInterval = 1000;
        /** 数据库编号，默认值：0 */
        private int database = 0;
    }

}