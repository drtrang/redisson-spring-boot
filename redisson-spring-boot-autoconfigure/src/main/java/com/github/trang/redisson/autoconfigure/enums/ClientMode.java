package com.github.trang.redisson.autoconfigure.enums;

/**
 * Redisson 客户端模式
 *
 * @author trang
 */
public enum ClientMode {

    /**
     * 默认客户端
     */
    DEFAULT,

    /**
     * 异步响应客户端
     */
    REACTIVE,

    /**
     * 注册上述两个客户端
     */
    BOTH,

    /**
     * 禁用 Redisson
     */
    NONE

}