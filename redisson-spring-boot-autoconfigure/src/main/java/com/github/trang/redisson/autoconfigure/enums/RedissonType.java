package com.github.trang.redisson.autoconfigure.enums;

/**
 * Redis 服务端模式
 *
 * @author trang
 */
public enum RedissonType {

    /**
     * 单节点模式
     */
    SINGLE,

    /**
     * 集群模式
     */
    CLUSTER,

    /**
     * 主从模式
     */
    MASTER_SLAVE,

    /**
     * 哨兵模式
     */
    SENTINEL,

    /**
     * 云托管模式
     */
    REPLICATED,

    /**
     * 禁用 Redisson
     */
    NONE

}