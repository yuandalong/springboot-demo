package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 单条消费者
 *
 * @author ydl
 * @since 2019-07-01
 */
@Component
@Slf4j
public class SingleListener {


    @KafkaListener(id = "consumer", topics = "topic.quick.consumer")
    public void consumerListener(ConsumerRecord<Integer, String> record) {
        log.info("topic.quick.consumer receive : " + record.toString());
    }
}
