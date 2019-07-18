package com.ydl.springboot.es.demo.service;

import com.ydl.springboot.es.demo.dao.BookDao;
import com.ydl.springboot.es.demo.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 使用spring-data包操作es
 * @author ydl
 * @since 2019-07-17
 */

@Service
public class SpringDataService {
    @Autowired
    BookDao dao;

    public void save(){
        Book book = new Book();
        book.setId("1");
        book.setName("test");
        book.setAuthor("ydl");
        dao.save(book);
    }

    public void getAll(){
        dao.findAll().forEach(System.out::println);
    }

    public void findByName(){
        dao.findByName("ydl");
    }

    public void findById(){
        dao.findBookById("1");
    }

    public void update(){
        Book book = dao.findBookById("1");
        book.setAuthor("123");
        dao.save(book);
    }

    public void del(){
        dao.deleteById("1");
    }
}
