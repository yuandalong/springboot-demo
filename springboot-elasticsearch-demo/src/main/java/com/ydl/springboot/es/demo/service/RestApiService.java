package com.ydl.springboot.es.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ydl.springboot.es.demo.entity.Book;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 使用 rest api操作es
 *
 * @author ydl
 * @since 2019-07-17
 */
@Service
public class RestApiService {
    @Autowired
    private RestClient client;

    /**
     * 同步请求
     *
     * @throws IOException
     */
    public void getEsInfo() throws IOException {
        // 构造HTTP请求，第一个参数是请求方法，第二个参数是服务器的端点，host默认是http://localhost:9200
        Request request = new Request("GET", "/");
//        // 设置其他一些参数比如美化json
//        request.addParameter("pretty", "true");

//        // 设置请求体
//        request.setEntity(new NStringEntity("{\"json\":\"text\"}", ContentType.APPLICATION_JSON));

//        // 还可以将其设置为String，默认为ContentType为application/json
//        request.setJsonEntity("{\"json\":\"text\"}");

        /*
        performRequest是同步的，将阻塞调用线程并在请求成功时返回Response，如果失败则抛出异常
        内部属性可以取出来通过下面的方法
         */
        Response response = client.performRequest(request);
//        // 获取请求行
//        RequestLine requestLine = response.getRequestLine();
//        // 获取host
//        HttpHost host = response.getHost();
//        // 获取状态码
//        int statusCode = response.getStatusLine().getStatusCode();
//        // 获取响应头
//        Header[] headers = response.getHeaders();
        // 获取响应体
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
    }

    /**
     * 异步请求
     */
    public void asyn() {
        Request request = new Request(
                "GET",
                "/");
        client.performRequestAsync(request, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                try {
                    System.out.println(EntityUtils.toString(response.getEntity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("异步执行HTTP请求并成功");
            }

            @Override
            public void onFailure(Exception exception) {
                System.out.println("异步执行HTTP请求并失败");
            }
        });
        System.out.println("main end");
    }

    /**
     * 并行异步执行
     */
    public void parallAsyn() {
        //final CountDownLatch latch = new CountDownLatch(10);
        //for (int i = 0; i < 10; i++) {
        //    Request request = new Request("PUT", "/posts/doc/" + i);
        //    //let's assume that the documents are stored in an HttpEntity array
        //    request.setEntity("123");
        //    client.performRequestAsync(
        //            request,
        //            new ResponseListener() {
        //                @Override
        //                public void onSuccess(Response response) {
        //
        //                    latch.countDown();
        //                }
        //
        //                @Override
        //                public void onFailure(Exception exception) {
        //
        //                    latch.countDown();
        //                }
        //            }
        //    );
        //}
        //latch.await();
    }

    public void add(Book book) throws IOException {
        // 构造HTTP请求，第一个参数是请求方法，第二个参数是服务器的端点，host默认是http://localhost:9200，
        // endpoint直接指定为index/type的形式
        Request request = new Request("POST", "/book/book/" +
                book.getId());
        // 设置其他一些参数比如美化json
        //request.addParameter("pretty", "true");
        ContentType contentType = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), "UTF-8");
        // 设置请求体并指定ContentType，如果不指定默认为APPLICATION_JSON
        request.setEntity(new NStringEntity(JSONObject.toJSONString(book), contentType));
        //request.Header.Set("Content-Type", "application/json");
        System.out.println(request);

        // 发送HTTP请求
        Response response = client.performRequest(request);

        // 获取响应体, id: AWXvzZYWXWr3RnGSLyhH
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
    }


    public void getEntityById() throws IOException {
        Request request = new Request("GET", "/book/book/1");
        // 添加json返回优化
        request.addParameter("pretty", "true");
        // 执行HHTP请求
        Response response = client.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
        JSONObject j = JSON.parseObject(responseBody);
        System.out.println(j.getString("_source"));
    }
}
