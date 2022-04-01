package com.fangshuo.wiki.controller;

import com.fangshuo.wiki.req.DocQueryReq;
import com.fangshuo.wiki.req.DocSaveReq;
import com.fangshuo.wiki.resp.CommonResp;
import com.fangshuo.wiki.resp.DocQueryResp;
import com.fangshuo.wiki.resp.PageResp;
import com.fangshuo.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    @GetMapping("/list")
    public CommonResp docList(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 使用post请求时，前面要加上@RequestBody，这个注解对应的是json方式的提交，需要加这个@RequestBody才能接收到。
    // 两种不同方式的表单提交：1. application/json 2. application/x-www-form-urlencoded 这个是表单方式的提交
    public CommonResp save(@RequestBody @Valid DocSaveReq req){
        CommonResp<Object> resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp<Object> resp = new CommonResp<>();
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id){
        //这里返回的是String类型，所以泛型要使用String
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }
}
