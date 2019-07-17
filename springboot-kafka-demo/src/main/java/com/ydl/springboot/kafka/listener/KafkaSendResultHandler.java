package com.ydl.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * 生产者消息发送结果回调
 *
 * @author ydl
 * @since 2019-07-01
 */
@Component
@Slf4j
public class KafkaSendResultHandler implements ProducerListener {


    /**
     * 成功回调
     *
     * @param producerRecord
     * @param recordMetadata
     */
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        log.info("Message send success : " + producerRecord.toString());
    }

    /**
     * 失败回调
     *
     * @param producerRecord
     * @param exception
     */
    @Override
    public void onError(ProducerRecord producerRecord, Exception exception) {
        log.info("Message send error : " + producerRecord.toString());
    }
}
