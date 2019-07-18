package com.ydl.springboot.es.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * 基础包的注释驱动配置，配置自动扫描的repositories根目录
 *
 * @author ydl
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.ydl.springboot.es.demo.dao")
public interface ESConfig {
}
