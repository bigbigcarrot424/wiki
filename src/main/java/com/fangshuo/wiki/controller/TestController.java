package com.fangshuo.wiki.controller;

import com.fangshuo.wiki.domain.Test;
import com.fangshuo.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {
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

    @Resource
    private TestService testService;

    @GetMapping("/test/list")
    public List<Test> testList(){
        return testService.list();
    }
}
