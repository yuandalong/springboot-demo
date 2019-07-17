package com.ydl.springboot.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ydl
 * @since 2019-07-01
 */
@Configuration
@EnableKafka
public class KafkaConfiguration {
    private static final String TEST_TOPIC = "test";
    private static final String BOOTSTRAP_SERVERS = "10.50.162.207:9092";

    /**
     * ConcurrentKafkaListenerContainerFactory为创建Kafka监听器的工厂类，这里只配置了消费者
     *
     * @return
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    /**
     * 根据consumerProps填写的参数创建消费者工厂
     *
     * @return
     */
    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }


    /**
     * 根据senderProps填写的参数创建生产者工厂
     *
     * @return
     */
    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory<>(senderProps());
        ////开启事务
        //factory.transactionCapable();
        ////设置TransactionIdPrefix，TransactionIdPrefix是用来生成Transactional.id的前缀
        //factory.setTransactionIdPrefix("tran-");
        return factory;
    }


    /**
     * kafkaTemplate实现了Kafka发送接收等功能
     *
     * @return
     * @Primary注解的意思是在拥有多个同类型的Bean时优先使用该Bean，到时候方便我们使用@Autowired注解自动注入。
     */
    @Bean
    @Primary
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    /**
     * 直接指定template的topic
     *
     * @return
     */
    @Bean("defaultKafkaTemplate")
    public KafkaTemplate<Integer, String> defaultKafkaTemplate() {
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(producerFactory());
        template.setDefaultTopic(TEST_TOPIC);
        return template;
    }

    /**
     * 消费者配置参数
     *
     * @return
     */
    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>(8);
        //连接地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        //GroupID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bootKafka");
        //是否自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        //自动提交的频率
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        //Session超时设置
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //键的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        //值的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    /**
     * 生产者配置
     *
     * @return
     */
    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>(8);
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        //重试，0为不启用重试机制
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        //控制批处理大小，单位为字节
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        //批量发送，延迟为1毫秒，启用该功能能有效减少生产者发送消息次数，从而提高并发量
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        //生产者可以使用的总内存字节来缓冲等待发送到服务器的记录
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024000);
        //键的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        //值的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }


    /**
     * kafka事务管理器
     * 注意如果项目里定义了KafkaTransactionManager，那么ProducerFactory必须设置事务管理，否则启动会报错
     *
     * @param producerFactory
     * @return
     */
    //@Bean
    public KafkaTransactionManager transactionManager(ProducerFactory producerFactory) {
        KafkaTransactionManager manager = new KafkaTransactionManager(producerFactory);
        return manager;
    }


    /**
     * Bean方式创建消费者监听容器
     * @return
     */
    @Bean
    public KafkaMessageListenerContainer demoListenerContainer() {
        ContainerProperties properties = new ContainerProperties(TEST_TOPIC);

        properties.setGroupId("bean");

        properties.setMessageListener(new MessageListener<Integer,String>() {
            private Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public void onMessage(ConsumerRecord<Integer, String> record) {
                log.info("topic.quick.bean receive : " + record.toString());
            }
        });

        return new KafkaMessageListenerContainer(consumerFactory(), properties);
    }

}
