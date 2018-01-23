package com.github.trang.redisson.autoconfigure;

import org.redisson.config.Config;

/**
 * Callback interface that can be implemented by beans wishing to customize the redisson client
 * before it is fully initialized, in particular to tune its configuration.
 *
 * @author trang
 */
public interface RedissonCustomizer {

    /**
     * Customize the redisson.
     *
     * @param config the {@code Redisson} to customize
     */
    void customize(Config config);

}