package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量消费者
 * 1. 重新创建一份新的消费者配置，配置为一次拉取5条消息
 * 2. 创建一个监听容器工厂，设置其为批量消费并设置并发量为5，这个并发量根据分区数决定，必须小于等于分区数，否则会有线程一直处于空闲状态
 * 3. 创建一个分区数为8的Topic
 * 4. 创建监听方法，设置消费id为batch，clientID前缀为batch，监听topic.quick.batch，使用batchContainerFactory工厂创建该监听容器
 *
 * @author ydl
 * @since 2019-07-01
 */
@Component
@Slf4j
public class BatchListener {


    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.50.162.207:9092");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //一次拉取消息数量
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean("batchContainerFactory")
    public ConcurrentKafkaListenerContainerFactory listenerContainer() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        //设置并发量，小于或等于Topic的分区数，否则会有线程一直处于空闲状态
        container.setConcurrency(5);
        //设置为批量监听
        container.setBatchListener(true);
        return container;
    }

    /**
     * 通过代码创建topic
     * @return
     */
    //@Bean
    public NewTopic batchTopic() {
        return new NewTopic("topic.quick.batch", 8, (short) 1);
    }


    @KafkaListener(id = "batch", clientIdPrefix = "batch", topics = {"test"}, containerFactory = "batchContainerFactory")
    public void batchListener(List<String> data) {
        log.info("topic.quick.batch  receive : ");
        for (String s : data) {
            log.info(s);
        }
    }


    //@Bean
    public NewTopic batchWithPartitionTopic() {
        return new NewTopic("topic.quick.batch.partition", 8, (short) 1);
    }

    /**
     * 监听Topic中指定的分区
     * TopicPartition：topic--需要监听的Topic的名称，partitions --需要监听Topic的分区id，
     * partitionOffsets --可以设置从某个偏移量开始监听
     * PartitionOffset：partition --分区Id，非数组，initialOffset --初始偏移量
     *
     * @param data
     */
    @KafkaListener(id = "batchWithPartition",clientIdPrefix = "bwp",containerFactory = "batchContainerFactory",
            topicPartitions = {
                    @TopicPartition(topic = "topic.quick.batch.partition",partitions = {"1","3"}),
                    @TopicPartition(topic = "topic.quick.batch.partition",partitions = {"0","4"},
                            partitionOffsets = @PartitionOffset(partition = "2",initialOffset = "100"))
            }
    )
    public void batchListenerWithPartition(List<String> data) {
        log.info("topic.quick.batch.partition  receive : ");
        for (String s : data) {
            log.info(s);
        }
    }


    /**
     * 注解方式获取消息头及消息体
     *
     * @Payload：获取的是消息的消息体，也就是发送内容
     * @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY)：获取发送消息的key
     * @Header(KafkaHeaders.RECEIVED_PARTITION_ID)：获取当前消息是从哪个分区中监听到的
     * @Header(KafkaHeaders.RECEIVED_TOPIC)：获取监听的TopicName
     * @Header(KafkaHeaders.RECEIVED_TIMESTAMP)：获取时间戳
     *
     * @param key
     * @param partition
     * @param topic
     * @param ts
     */
    @KafkaListener(id = "anno", topics = "topic.quick.anno")
    public void annoListener(@Payload String data,
                             @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                             @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.info("topic.quick.anno receive : \n"+
                "data : "+data+"\n"+
                "key : "+key+"\n"+
                "partitionId : "+partition+"\n"+
                "topic : "+topic+"\n"+
                "timestamp : "+ts+"\n"
        );

    }

}
