package com.ydl.springboot.start.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义自己的自动配置PersonServiceAutoConfiguration，并将核心功能类PersonService放入到Spring Context容器中
 *
 * @author ydl
 * @since 2019/3/14
 */
@Configuration
@EnableConfigurationProperties(PersonProperties.class)
@ConditionalOnClass(PersonService.class)
@ConditionalOnProperty(prefix = "spring.person", value = "enabled", matchIfMissing = true)
public class PersonServiceAutoConfiguration {

  @Autowired
  private PersonProperties properties;

  /**
   * 当容器中没有指定Bean的情况下，自动配置PersonService类
   */
  @Bean
  @ConditionalOnMissingBean(PersonService.class)
  public PersonService personService() {
    return new PersonService(properties);
  }

  // 常用注解说明
  // @ConditionalOnClass：当类路径classpath下有指定的类的情况下进行自动配置
  // @ConditionalOnMissingBean:当容器(Spring Context)中没有指定Bean的情况下进行自动配置
  // @ConditionalOnProperty(prefix = “example.service”, value = “enabled”, matchIfMissing = true)，
  // 当配置文件中example.service.enabled=true时进行自动配置，如果没有设置此值就默认使用matchIfMissing对应的值
  // @ConditionalOnMissingBean，当Spring Context中不存在该Bean时。
  // @ConditionalOnBean:当容器(Spring Context)中有指定的Bean的条件下
  // @ConditionalOnMissingClass:当类路径下没有指定的类的条件下
  // @ConditionalOnExpression:基于SpEL表达式作为判断条件
  // @ConditionalOnJava:基于JVM版本作为判断条件
  // @ConditionalOnJndi:在JNDI存在的条件下查找指定的位置
  // @ConditionalOnNotWebApplication:当前项目不是Web项目的条件下
  // @ConditionalOnWebApplication:当前项目是Web项目的条件下
  // @ConditionalOnResource:类路径下是否有指定的资源
  // @ConditionalOnSingleCandidate:当指定的Bean在容器中只有一个，或者在有多个Bean的情况下，用来指定首选的Bean
  //
  // @EnableConfigurationProperties(XxxProperties.class) 注解的作用是@ConfigurationProperties注解生效。
  // 如果只配置@ConfigurationProperties注解，在IOC容器中是获取不到properties配置文件转化的bean的
  //
  // @ConfigurationProperties: 注解主要用来把properties配置文件转化为对应的XxxProperties来使用的,
  // 并不会把该类放入到IOC容器中，如果想放入到容器中可以在XxxProperties上使用@Component来标注，
  // 也可以使用@EnableConfigurationProperties(XxxProperties.class)统一配置到Application上来，
  // 这种方式可以在Application上来统一开启指定的属性，这样也没必要在每个XxxProperties上使用@Component
}
