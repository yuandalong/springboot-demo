package com.ydl.springboot.start.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置类
 *
 * @author ydl
 * @since 2019/3/14
 */
@ConfigurationProperties(prefix = "spring.person")
@Data
public class PersonProperties {

  private String name;
  private int age;
  private String sex = "M";

  public PersonProperties() {
  }
  //@ConfigurationProperties: 注解主要用来把properties配置文件转化为对应的XxxProperties来使用的,
  // 并不会把该类放入到IOC容器中，如果想放入到容器中可以在XxxProperties上使用@Component来标注，
  // 也可以使用@EnableConfigurationProperties(XxxProperties.class)统一配置到Application上来，
  // 这种方式可以在Application上来统一开启指定的属性，这样也没必要在每个XxxProperties上使用@Component
}