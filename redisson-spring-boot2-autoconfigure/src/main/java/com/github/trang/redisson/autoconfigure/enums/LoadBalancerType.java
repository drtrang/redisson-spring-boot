package com.github.trang.redisson.autoconfigure.enums;

import org.redisson.connection.balancer.LoadBalancer;
import org.redisson.connection.balancer.RandomLoadBalancer;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;

/**
 * 负载均衡算法
 *
 * @author trang
 */
public enum LoadBalancerType {

    /**
     * 轮询
     */
    ROUND_ROBIN {
        @Override
        public LoadBalancer getInstance() {
            return new RoundRobinLoadBalancer();
        }
    },

    /**
     * 加权轮询，这里不提供该方式，因为构造 WeightedRoundRobinBalancer 实例时需要传入权重参数，如有需要请创建 #{@code Customizer<Config>}
     */
    WEIGHTED_ROUND_ROBIN {
        @Override
        public LoadBalancer getInstance() {
            throw new UnsupportedOperationException("please create a Customizer<Config>.");
        }
    },

    /**
     * 随机
     */
    RANDOM {
        @Override
        public LoadBalancer getInstance() {
            return new RandomLoadBalancer();
        }
    };

    public abstract LoadBalancer getInstance();

}