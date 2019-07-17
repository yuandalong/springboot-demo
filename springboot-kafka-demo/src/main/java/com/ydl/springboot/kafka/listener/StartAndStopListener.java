package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 启动和暂停listener
 * @author ydl
 * @since 2019-07-08
 */
@Slf4j
public class StartAndStopListener {

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private ConsumerFactory consumerFactory;

    @Bean
    public ConcurrentKafkaListenerContainerFactory delayContainerFactory() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(consumerFactory);
        //禁止自动启动
        container.setAutoStartup(false);
        return container;
    }

    @KafkaListener(id = "durable", topics = "topic.quick.durable",containerFactory = "delayContainerFactory")
    public void durableListener(String data) {
        //这里做数据持久化的操作
        log.info("topic.quick.durable receive : " + data);
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void startListener() {
        log.info("开启监听");
        //判断监听容器是否启动，未启动则将其启动
        if (!registry.getListenerContainer("durable").isRunning()) {
            registry.getListenerContainer("durable").start();
        }
        else{
            registry.getListenerContainer("durable").resume();
        }
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void shutDownListener() {
        log.info("关闭监听");
        registry.getListenerContainer("durable").pause();
    }
}
