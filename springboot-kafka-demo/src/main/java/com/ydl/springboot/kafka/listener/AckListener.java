package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 手动确认消息-Ack
 *
 * 使用Kafka的Ack机制比较简单，只需简单的三步即可：
 * 设置ENABLE_AUTO_COMMIT_CONFIG=false，禁止自动提交
 * 设置AckMode=MANUAL_IMMEDIATE
 * 监听方法加入Acknowledgment ack 参数
 *
 * 怎么拒绝消息呢，只要在监听方法中不调用ack.acknowledge()即可
 *
 * @author ydl
 * @since 2019-07-01
 */
@Component
@Slf4j
public class AckListener {


    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean("ackContainerFactory")
    public ConcurrentKafkaListenerContainerFactory ackContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        //factory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        return factory;
    }


    @KafkaListener(id = "ack", topics = "topic.quick.ack", containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack) {
        log.info("topic.quick.ack receive : " + record.value());
        ack.acknowledge();
    }


    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 手动ack时，不ack的消息的处理方式有两种：
     * 1.重新将消息发送到队列中，这种方式比较简单而且可以使用Headers实现第几次消费的功能，用以下次判断
     * @param record
     * @param ack
     * @param consumer
     */
    @KafkaListener(id = "ack", topics = "topic.quick.ack", containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack, Consumer consumer) {
        log.info("topic.quick.ack receive : " + record.value());

        //如果偏移量为偶数则确认消费，否则拒绝消费
        if (record.offset() % 2 == 0) {
            log.info(record.offset() + "--ack");
            ack.acknowledge();
        } else {
            log.info(record.offset() + "--nack");
            kafkaTemplate.send("topic.quick.ack", record.value());
        }
    }

    /**
     * 手动ack时，不ack的消息的处理方式有两种：
     * 2.使用Consumer.seek方法，重新回到该未ack消息偏移量的位置重新消费，这种可能会导致死循环，原因出现于业务一直没办法处理这条数据，
     * 但还是不停的重新定位到该数据的偏移量上。
     * @param record
     * @param ack
     * @param consumer
     */
    @KafkaListener(id = "ack", topics = "topic.quick.ack", containerFactory = "ackContainerFactory")
    public void ackListener2(ConsumerRecord record, Acknowledgment ack, Consumer consumer) {
        log.info("topic.quick.ack receive : " + record.value());

        //如果偏移量为偶数则确认消费，否则拒绝消费
        if (record.offset() % 2 == 0) {
            log.info(record.offset() + "--ack");
            ack.acknowledge();
        } else {
            log.info(record.offset() + "--nack");
            consumer.seek(new TopicPartition("topic.quick.ack", record.partition()), record.offset());
        }
    }
}