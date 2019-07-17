package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 消费者
 * 这里的消费者其实就是一个监听类，指定监听名为topic.quick.demo的Topic，consumerID为demo。
 *
 * @author ydl
 * @since 2019-07-01
 */
@Component
@Slf4j
public class DemoListener {

    /**
     * 声明consumerID为demo，监听topicName为test的Topic
     * 使用@KafkaListener这个注解并不局限于这个监听容器是单条数据消费还是批量消费，区分单数据还是多数据消费只需要配置一下注解的containerFactory属性即可
     * <p>
     * 监听器方法支持的参数有：
     * data ： 对于data值的类型其实并没有限定，根据KafkaTemplate所定义的类型来决定。data为List集合的则是用作批量消费。
     * ConsumerRecord：具体消费数据类，包含Headers信息、分区信息、时间戳等
     * Acknowledgment：用作Ack机制的接口
     * Consumer：消费者类，使用该类我们可以手动提交偏移量、控制消费速率等功能
     * 监听器参数例子：
     * public void listen1(String data)
     * public void listen2(ConsumerRecord<K,V> data)
     * public void listen3(ConsumerRecord<K,V> data, Acknowledgment acknowledgment)
     * public void listen4(ConsumerRecord<K,V> data, Acknowledgment acknowledgment, Consumer<K,V> consumer)
     * public void listen5(List<String> data)
     * public void listen6(List<ConsumerRecord<K,V>> data)
     * public void listen7(List<ConsumerRecord<K,V>> data, Acknowledgment acknowledgment)
     * public void listen8(List<ConsumerRecord<K,V>> data, Acknowledgment acknowledgment, Consumer<K,V> consumer)
     *
     * KafkaListener注解属性： id：消费者的id，当GroupId没有被配置的时候，默认id为GroupId
     * containerFactory：上面提到了@KafkaListener区分单数据还是多数据消费只需要配置一下注解的containerFactory属性就可以了，这里面配置的是监听容器工厂，也就是ConcurrentKafkaListenerContainerFactory，配置BeanName
     * topics：需要监听的Topic，可监听多个
     * topicPartitions：可配置更加详细的监听信息，必须监听某个Topic中的指定分区，或者从offset为200的偏移量开始监听
     * errorHandler：监听异常处理器，配置BeanName
     * groupId：消费组ID
     * idIsGroup：id是否为GroupId
     * clientIdPrefix：消费者Id前缀
     * beanRef：真实监听容器的BeanName，需要在 BeanName前加 "__"
     */
    @KafkaListener(id = "demo", topics = "test")
    public void listen(String msgData) {
        log.info("demo receive : " + msgData);
    }


}
