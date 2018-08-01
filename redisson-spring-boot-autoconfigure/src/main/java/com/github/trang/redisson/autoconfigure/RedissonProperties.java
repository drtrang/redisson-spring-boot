package com.github.trang.redisson.autoconfigure;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import org.redisson.codec.DefaultReferenceCodecProvider;
import org.redisson.codec.ReferenceCodecProvider;
import org.redisson.config.ReadMode;
import org.redisson.config.SslProvider;
import org.redisson.config.SubscriptionMode;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.trang.redisson.autoconfigure.enums.AddressResolverGroupFactoryType;
import com.github.trang.redisson.autoconfigure.enums.ClientMode;
import com.github.trang.redisson.autoconfigure.enums.CodecType;
import com.github.trang.redisson.autoconfigure.enums.LoadBalancerType;
import com.github.trang.redisson.autoconfigure.enums.RedisType;

import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;

/**
 * Redisson 自动配置属性
 *
 * @author trang
 */
@ConfigurationProperties(prefix = "redisson")
@Getter
@Setter
public class RedissonProperties {

    /** 客户端模式，默认值：default */
    private ClientMode clientMode = ClientMode.DEFAULT;
    /** 线程池数量，默认值：当前处理核数量*2 */
    private int threads = 0;
    /** Netty 线程池数量，默认值：当前处理核数量*2 */
    private int nettyThreads = 0;
    /** Redis 进行序列化和反序列化的类型，默认值：jackson */
    private CodecType codec = CodecType.JACKSON;
    /** Codec 注册和获取功能的提供者，默认值：DefaultReferenceCodecProvider */
    private ReferenceCodecProvider referenceCodecProvider = new DefaultReferenceCodecProvider();
    /** 单独提供一个线程池实例 */
    private ExecutorService executor;
    /** Redisson 参考功能的配置选项，默认值：true */
    private boolean referenceEnabled = true;
    /** TransportMode，默认值：linux: epoll，other: nio */
    private TransportMode transportMode;
    /** 单独指定一个 EventLoopGroup */
    private EventLoopGroup eventLoopGroup;
    /** 锁监视器的超时时间，默认值：30000 ms */
    private long lockWatchdogTimeout = 30 * 1000;
    /** 是否顺序处理或并发处理 PubSub 消息，默认值：true */
    private boolean keepPubSubOrder = true;
    /** Redis 服务端模式，默认值：single */
    private RedisType type = RedisType.SINGLE;
    /** 地址解析器，默认值：DnsAddressResolverGroupFactory */
    private AddressResolverGroupFactoryType addressResolverGroupFactory = AddressResolverGroupFactoryType.DEFAULT;

    /** 基础配置 */
    private Config config = new Config();
    /** 单节点模式 */
    private SingleServerConfig single = new SingleServerConfig();
    /** 集群模式 */
    private ClusterServersConfig cluster = new ClusterServersConfig();
    /** 主从模式 */
    private MasterSlaveServersConfig masterSlave = new MasterSlaveServersConfig();
    /** 哨兵模式 */
    private SentinelServersConfig sentinel = new SentinelServersConfig();
    /** 云托管模式 */
    private ReplicatedServersConfig replicated = new ReplicatedServersConfig();

    @Getter
    @Setter
    public static class Config {
        /** 用文件方式配置 Redisson，如果指定了该文件则不会再用 spring-boot 配置文件中声明的配置 */
        private String location = null;
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
        /** 命令等待超时时间，，默认值：3000 ms */
        private int timeout = 3000;
        /** 命令失败重试次数，默认值：3 */
        private int retryAttempts = 3;
        /** 命令重试发送时间间隔，默认值：1500 ms */
        private int retryInterval = 1500;
        /** Redis 实例密码，默认值：null */
        private String password = null;
        /** 单个连接最大订阅数量，默认值：5 */
        private int subscriptionsPerConnection = 5;
        /** 客户端名称，默认值：null */
        private String clientName = null;
        /** 启用 SSL 终端识别，默认值：true */
        private boolean sslEnableEndpointIdentification = true;
        /** SSL 实现方式，默认值：jdk */
        private SslProvider sslProvider = SslProvider.JDK;
        /** SSL 信任证书库路径，默认值：null */
        private URI sslTrustStore = null;
        /** SSL 信任证书库密码，默认值：null */
        private String sslTrustStorePassword = null;
        /** SSL 钥匙库路径，默认值：null */
        private URI sslKeystore = null;
        /** SSL 钥匙库密码，默认值：null */
        private String sslKeystorePassword = null;
        /** PING 命令的发送时间间隔，默认值：0 ms */
        private int pingConnectionInterval = 0;
        /** 开启连接的 TCP KeepAlive 特性，默认值：false */
        private boolean keepAlive = false;
        /** 开启连接的 TCP NoDelay 特性，默认值：false */
        private boolean tcpNoDelay = false;
    }

    @Getter
    @Setter
    private static class BaseMasterSlaveServersConfig extends BaseConfig {
        /** 负载均衡算法，默认值：round_robin */
        private LoadBalancerType loadBalancer = LoadBalancerType.ROUND_ROBIN;
        /** 主节点最小空闲连接数，默认值：32 */
        private int masterConnectionMinimumIdleSize = 32;
        /** 主节点连接池大小，默认值：64 */
        private int masterConnectionPoolSize = 64;
        /** 从节点最小空闲连接数，默认值：32 */
        private int slaveConnectionMinimumIdleSize = 32;
        /** 从节点连接池大小，默认值：64 */
        private int slaveConnectionPoolSize = 64;
        /** 当第一个 Redis 命令执行失败的时间间隔到达该值时，从节点将被排除在可用节点的内部列表中，默认值：60000 ms */
        private int failedSlaveCheckInterval = 60000;
        /** 当节点被排除在可用服务器的内部列表中时，从节点重新连接尝试的间隔，默认值：3000 ms */
        private int failedSlaveReconnectionInterval = 3000;
        /** 读取操作的负载均衡模式，默认值：slave */
        private ReadMode readMode = ReadMode.SLAVE;
        /** 订阅操作的负载均衡模式，默认值：master */
        private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;
        /** 从节点发布和订阅连接的最小空闲连接数，默认值：1 */
        private int subscriptionConnectionMinimumIdleSize = 1;
        /** 从节点发布和订阅连接池大小，默认值：50 */
        private int subscriptionConnectionPoolSize = 50;
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
        /** 最小空闲连接数，默认值：32 */
        private int connectionMinimumIdleSize = 32;
        /** 连接池大小，默认值：64 */
        private int connectionPoolSize = 64;
        /** 发布和订阅连接的最小空闲连接数，默认值：1 */
        private int subscriptionConnectionMinimumIdleSize = 1;
        /** 发布和订阅连接池大小，默认值：50 */
        private int subscriptionConnectionPoolSize = 50;
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
        private String masterName = null;
        /** 哨兵扫描间隔时间，默认值：1000 ms */
        private int scanInterval = 1000;
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