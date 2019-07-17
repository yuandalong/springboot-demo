package com.ydl.springboot.starter.use;

import com.ydl.springboot.start.demo.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UseApplicationTests {

  @Autowired
  private PersonService personService;

  @Test
  public void contextLoads() {
    personService.sayHello();
  }

}
