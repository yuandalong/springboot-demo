package com.ydl.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 非web项目，引入spring-boot-starter，入口类实现CommandLineRunner，重写run方法，run方法就是程序主入口
 * @author ydl
 */
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("afdafads");
    System.out.println("14234234");
  }
}
