package com.ydl.springboot.es.demo;

import com.ydl.springboot.es.demo.entity.Book;
import com.ydl.springboot.es.demo.service.RestApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * 使用rest api操作es测试类
 *
 * @author ydl
 * @since 2019-07-17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestApiTests {
    @Autowired
    private RestApiService service;

    @Test
    public void getEsInfoTest() throws IOException {
        service.getEsInfo();
    }

    @Test
    public void asynTest() throws InterruptedException {
        service.asyn();
        Thread.sleep(10000);
    }

    @Test
    public void addTest() throws IOException {
        Book book = new Book();
        book.setId("2");
        book.setName("ttt");
        service.add(book);
    }

    @Test
    public void getEntityByIdTest() throws IOException {
        service.getEntityById();
    }
}
