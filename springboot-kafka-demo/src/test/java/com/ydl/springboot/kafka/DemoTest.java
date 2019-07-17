package com.ydl.springboot.kafka;

import com.ydl.springboot.kafka.listener.KafkaSendResultHandler;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ydl
 * @since 2019-07-01
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DemoTest {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    private static final String TEST_TOPIC = "test";

    @Test
    public void testDemo() throws InterruptedException {
        kafkaTemplate.send(TEST_TOPIC, "this is my first demo");
        //send是异步发送
        //休眠5秒，为了使监听器有足够的时间监听到topic的数据
        Thread.sleep(5000);

        try {
            //加上get后变为同步方法
            //Future模式中，我们采取异步执行事件，等到需要返回值得时候我们再调用get方法获取future的返回值
            kafkaTemplate.send(TEST_TOPIC, "test sync send message").get();

            //设置超时时间
            //设置了超时时间后，超时了也会发送，只不过get方法会返回超时
            //可以用这种方式来记录sql的慢查询
            kafkaTemplate.send(TEST_TOPIC, "test send message timeout").get(1, TimeUnit.MICROSECONDS);


        } catch (ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Resource
    private KafkaTemplate defaultKafkaTemplate;

    /**
     * 使用默认topic
     */
    @Test
    public void testDefaultKafkaTemplate() {
        //发送时不指定topic，topic在template里通过setDefaultTopic指定
        defaultKafkaTemplate.sendDefault("I`m send msg to default topic");
    }


    /**
     * 发送特殊消息
     */
    @Test
    public void testTemplateSend() {
        //发送带有时间戳的消息
        kafkaTemplate.send(TEST_TOPIC, 0, System.currentTimeMillis(), 0, "send message with timestamp");

        //使用ProducerRecord发送消息
        ProducerRecord record = new ProducerRecord(TEST_TOPIC, "use ProducerRecord to send message");
        kafkaTemplate.send(record);

        //使用Message发送消息
        Map<String, Object> map = new HashMap<>(4);
        map.put(KafkaHeaders.TOPIC, TEST_TOPIC);
        map.put(KafkaHeaders.PARTITION_ID, 0);
        map.put(KafkaHeaders.MESSAGE_KEY, 0);
        GenericMessage message = new GenericMessage("use Message to send message", new MessageHeaders(map));
        kafkaTemplate.send(message);
    }


    @Autowired
    private KafkaSendResultHandler producerListener;

    /**
     * 生产者回调
     *
     * @throws InterruptedException
     */
    @Test
    public void testProducerListen() throws InterruptedException {
        kafkaTemplate.setProducerListener(producerListener);
        kafkaTemplate.send(TEST_TOPIC, "test producer listen");
        Thread.sleep(1000);
    }

    /**
     * kafka 事务消息
     * 使用Transactional注解
     *
     * @throws InterruptedException
     */
    @Test
    @Transactional
    public void testTransactionalAnnotation() throws InterruptedException {
        kafkaTemplate.send(TEST_TOPIC, "test transactional annotation");
        throw new RuntimeException("fail");
    }

    /**
     * kafka 事务消息
     * 使用KafkaTemplate.executeInTransaction开启事务
     * 这种方式开启事务是不需要配置事务管理器的，也可以称为本地事务。直接编写测试方法
     *
     * @throws InterruptedException
     */
    @Test
    public void testExecuteInTransaction() throws InterruptedException {
        kafkaTemplate.executeInTransaction(new KafkaOperations.OperationsCallback() {
            @Override
            public Object doInOperations(KafkaOperations kafkaOperations) {
                kafkaOperations.send(TEST_TOPIC, "test executeInTransaction");
                throw new RuntimeException("fail");
                //return true;
            }
        });
    }


    /**
     * 发送包含请求头的消息
     * @throws InterruptedException
     */
    @Test
    public void testAnno() throws InterruptedException {
        Map map = new HashMap<>();
        map.put(KafkaHeaders.TOPIC, "topic.quick.anno");
        map.put(KafkaHeaders.MESSAGE_KEY, 0);
        map.put(KafkaHeaders.PARTITION_ID, 0);
        map.put(KafkaHeaders.TIMESTAMP, System.currentTimeMillis());

        kafkaTemplate.send(new GenericMessage<>("test anno listener", map));
    }
}
