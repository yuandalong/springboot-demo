package com.ydl.springboot.es.demo;

import com.ydl.springboot.es.demo.service.SpringDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 使用spring data操作es的测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataTests {

    @Autowired
    private SpringDataService springDataService;

    @Test
    public void saveTest(){
        springDataService.save();
    }

    @Test
    public void getAllTest(){
        springDataService.getAll();
    }

    @Test
    public void test(){
        System.out.println("aaaa");
    }

}
