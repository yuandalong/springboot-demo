package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

/**
 * 消息过滤器
 * 消息过滤器可以在消息抵达监听容器前被拦截，过滤器根据系统业务逻辑去筛选出需要的数据再交由KafkaListener处理。
 * 配置消息其实是非常简单的额，只需要为监听容器工厂配置一个RecordFilterStrategy(消息过滤策略)，返回true的时候消息将会被抛弃，返回false时，消息能正常抵达监听容器。
 *
 * @author ydl
 * @since 2019-07-10
 */
@Component
@Slf4j
public class FilterListener {

    @Autowired
    private ConsumerFactory consumerFactory;

    @Bean
    public ConcurrentKafkaListenerContainerFactory filterContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        //配合RecordFilterStrategy使用，被过滤的信息将被丢弃
        factory.setAckDiscarded(true);
        factory.setRecordFilterStrategy(consumerRecord -> {
            long data = Long.parseLong((String) consumerRecord.value());
            log.info("filterContainerFactory filter : " + data);
            return data % 2 != 0;
            //返回true将会被丢弃
        });
        return factory;
    }

    @KafkaListener(id = "filterCons", topics = "topic.quick.filter", containerFactory = "filterContainerFactory")
    public void filterListener(String data) {
        //这里做数据持久化的操作
        log.error("topic.quick.filter receive : " + data);
    }
}