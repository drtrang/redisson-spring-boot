package com.github.trang.redisson.autoconfigure;

import static java.util.stream.Collectors.toSet;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionMessage.Style;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.github.trang.redisson.autoconfigure.enums.RedissonType;

/**
 * Redisson 自动配置判断条件
 * <p>
 * Write the code. Change the world.
 *
 * @author trang
 * @date 2018/7/31
 */
class RedissonCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String type = context.getEnvironment().getProperty("redisson.type");
        if (type == null || type.isEmpty() || type.trim().isEmpty()) {
            type = RedissonType.SINGLE.name();
        }
        ConditionMessage.Builder condition = ConditionMessage.forCondition("RedissonCondition",
                String.format("(redisson.type=%s)", type));
        if (type.equalsIgnoreCase(RedissonType.NONE.name())) {
            return noMatch(condition.found("matched value").items(Style.QUOTE, type));
        }
        Set<String> relaxedTypes = Arrays.stream(RedissonType.values())
                .filter(t -> t != RedissonType.NONE)
                .map(Enum::name)
                .map(name -> Arrays.asList(name, name.toLowerCase(), name.toUpperCase()))
                .flatMap(List::stream)
                .collect(toSet());
        if (relaxedTypes.contains(type)) {
            return match(condition.found("matched value").items(Style.QUOTE, type));
        } else {
            return noMatch(condition.because("has unrecognized value '" + type + "'"));
        }
    }

}