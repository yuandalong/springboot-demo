package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KafkaListener指定异常处理器
 * 异常处理逻辑与代码业务逻辑解耦
 *
 * @author ydl
 * @since 2019-07-10
 */
@Component
@Slf4j
public class ErrorListener {


    @KafkaListener(id = "err", topics = "topic.quick.error", errorHandler = "consumerAwareErrorHandler")
    public void errorListener(String data) {
        log.info("topic.quick.error  receive : " + data);
        throw new RuntimeException("fail");
    }

    /**
     * 单条消费异常处理器
     *
     * @return
     */
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return (message, e, consumer) -> {
            log.info("consumerAwareErrorHandler receive : " + message.getPayload().toString());
            return null;
        };
    }


    /**
     * 批量消费异常处理器
     *
     * @return
     */
    @Bean
    public ConsumerAwareListenerErrorHandler listConsumerAwareErrorHandler() {
        return (message, e, consumer) -> {
            log.info("consumerAwareErrorHandler receive : " + message.getPayload().toString());
            MessageHeaders headers = message.getHeaders();
            List<String> topics = headers.get(KafkaHeaders.RECEIVED_TOPIC, List.class);
            List<Integer> partitions = headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, List.class);
            List<Long> offsets = headers.get(KafkaHeaders.OFFSET, List.class);
            Map<TopicPartition, Long> offsetsToReset = new HashMap<>(2);

            return null;
        };
    }

}

