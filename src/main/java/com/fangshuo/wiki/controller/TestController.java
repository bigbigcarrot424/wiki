package com.fangshuo.wiki.controller;

import com.fangshuo.wiki.domain.Test;
import com.fangshuo.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    @Resource
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;


    @Value("${test.hello:TEST}")
    private String testHello;

    @GetMapping("/hello")
    public String hello(){
        return "hello world" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "hello world! Post" + name;
    }

    @GetMapping("/test/list")
    public List<Test> testList(){
        return testService.list();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
//        long parseLong = Long.parseLong(key);
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
}
