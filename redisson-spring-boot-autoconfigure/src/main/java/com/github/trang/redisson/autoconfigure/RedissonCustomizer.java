package com.github.trang.redisson.autoconfigure;

import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ClassUtils;

import com.github.trang.autoconfigure.Customizer;

/**
 * Redisson 定制化配置
 * <p>
 * Write the code. Change the world.
 *
 * @author trang
 * @date 2018/7/31
 */
class RedissonCustomizer {

    private static boolean epollPresent = ClassUtils.isPresent("io.netty.channel.epoll.EpollEventLoopGroup",
            RedissonCustomizer.class.getClassLoader());

    /**
     * 针对不同操作系统，设置不同的 transportMode 默认值
     * 因为文件配置方式中不能判断 transportMode 的值是用户主动设置的，还是 redisson 默认设置的，所以这里只对 spring-boot 方式的配置生效
     */
    @Bean
    Customizer<Config> transportModeCustomizer() {
        return config -> {
            if (config.getTransportMode() == null) {
                if (epollPresent) {
                    config.setTransportMode(TransportMode.EPOLL);
                } else {
                    config.setTransportMode(TransportMode.NIO);
                }
            }
        };
    }

}