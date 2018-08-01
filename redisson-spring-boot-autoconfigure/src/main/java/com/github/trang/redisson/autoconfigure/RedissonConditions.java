package com.github.trang.redisson.autoconfigure;

import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionMessage.Style;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.github.trang.redisson.autoconfigure.enums.ClientMode;

/**
 * Redisson 的判断条件
 * <p>
 * Write the code. Change the world.
 *
 * @author trang
 * @date 2018/8/1
 */
class RedissonConditions {

    private static final String CLIENT_MODE_PROPERTY = "redisson.client-mode";

    /**
     * RedissonAutoConfiguration 的判断条件
     * <p>
     * redisson.client-mode == null || redisson.client-mode != none 时生效
     */
    static class RedissonCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment environment = context.getEnvironment();
            BindResult<String> bindResult = Binder.get(environment).bind(CLIENT_MODE_PROPERTY, String.class);
            ConditionMessage.Builder otherMessage = ConditionMessage.forCondition("RedissonCondition");
            if (!bindResult.isBound()) {
                return match(otherMessage.because(String.format("automatic client mode '%s'", ClientMode.DEFAULT)));
            }
            try {
                BindResult<ClientMode> specified = Binder.get(environment).bind(CLIENT_MODE_PROPERTY, ClientMode.class);
                ConditionMessage message = ConditionMessage.forCondition("RedissonCondition",
                        String.format("(%s=%s)", CLIENT_MODE_PROPERTY, bindResult.get()))
                        .found("client mode").items(Style.QUOTE, specified.get());
                // client-mode=none 相当于禁用自动配置
                return specified.get() != ClientMode.NONE ? match(message) : noMatch(message);
            } catch (BindException e) {
                return noMatch(otherMessage.found("unknown value").items(Style.QUOTE, bindResult.get()));
            }
        }

    }

    /**
     * RedissonClient 的判断条件
     * <p>
     * redisson.client-mode == null || redisson.client-mode == reactive || redisson.client-mode == both 时生效
     */
    static class RedissonClientCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment environment = context.getEnvironment();
            BindResult<String> bindResult = Binder.get(environment).bind(CLIENT_MODE_PROPERTY, String.class);
            if (!bindResult.isBound()) {
                return match(ConditionMessage.forCondition("RedissonClientCondition")
                        .because(String.format("automatic client mode '%s'", ClientMode.DEFAULT)));
            }
            BindResult<ClientMode> specified = Binder.get(environment).bind(CLIENT_MODE_PROPERTY, ClientMode.class);
            ConditionMessage message = ConditionMessage.forCondition("RedissonClientCondition",
                    String.format("(%s=%s)", CLIENT_MODE_PROPERTY, bindResult.get()))
                    .found("client mode").items(Style.QUOTE, specified.get());
            return specified.get() != ClientMode.REACTIVE ? match(message) : noMatch(message);
        }

    }

    /**
     * RedissonReactiveClient 的判断条件
     * <p>
     * redisson.client-mode != null && redisson.client-mode == reactive || redisson.client-mode == both 时生效
     */
    static class RedissonReactiveClientCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment environment = context.getEnvironment();
            BindResult<String> bindResult = Binder.get(environment).bind(CLIENT_MODE_PROPERTY, String.class);
            if (!bindResult.isBound()) {
                return noMatch(ConditionMessage.forCondition("RedissonReactiveClientCondition")
                        .because(String.format("automatic client mode '%s'", ClientMode.DEFAULT)));
            }
            BindResult<ClientMode> specified = Binder.get(environment).bind(CLIENT_MODE_PROPERTY, ClientMode.class);
            ConditionMessage message = ConditionMessage.forCondition("RedissonReactiveClientCondition",
                    String.format("(%s=%s)", CLIENT_MODE_PROPERTY, bindResult.get()))
                    .found("client mode").items(Style.QUOTE, specified.get());
            return specified.get() != ClientMode.DEFAULT ? match(message) : noMatch(message);
        }

    }

}