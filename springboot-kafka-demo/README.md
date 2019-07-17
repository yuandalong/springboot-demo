# kafka应用实现顺序

1. 创建消费者和生产者的Map配置
1. 根据Map配置创建对应的消费者工厂(consumerFactory)和生产者工厂(producerFactory)
1. 根据consumerFactory创建监听器的监听器工厂
1. 根据producerFactory创建KafkaTemplate(Kafka操作类)
1. 创建监听容器

---

# spring 操作topic
    代码里用的比较少，就不写demo了，参考：
    https://www.jianshu.com/p/aa196f24f332
    
    
# Kafka使用事务的两种方式
    
1. 配置Kafka事务管理器并使用@Transactional注解
1. 使用KafkaTemplate的executeInTransaction方法