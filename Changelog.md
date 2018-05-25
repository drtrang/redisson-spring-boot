# Changelog

## 1.2.2
1. 修复：配置路径不正确
1. 更改：更新 parent 版本为 1.2.2
2. 更改：Redisson Spring 的配置前缀更改为 `spring.redisson`
3. 更改：RedissonTransactionManager 默认更改为关闭

## 1.2.0
1. 更改：更新 parent 版本为 1.2.0
2. 更改：更新 Redisson 版本为 3.7.0，跟进最新的配置方法
3. 更改：Redisson 自动配置的前缀由 `spring.redisson` 更改为 `redisson`
4. 更改：整合 Redisson 与 Spring 的配置，前缀为 `redisson.spring`
5. 更改：RedissonSpringCacheManager 的默认配置，`dynamic` 由 true 改为 false，`fallbackToNoOpCache` 由 false 改为 true
6. 新增：增加 Redisson 3.7.0 版本中增加的事务功能

## 1.1.1
1. 更新 Redisson 版本为 3.6.4
2. 删除 ConfigMap 中的自动增加的 `default cache`

## 1.0.1
1. 修复不能正确注入 password 的 bug
2. 完善 Redisson 的自动配置属性