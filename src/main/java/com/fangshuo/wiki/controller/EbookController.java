package com.fangshuo.wiki.controller;

import com.fangshuo.wiki.req.EbookQueryReq;
import com.fangshuo.wiki.req.EbookSaveReq;
import com.fangshuo.wiki.resp.CommonResp;
import com.fangshuo.wiki.resp.EbookQueryResp;
import com.fangshuo.wiki.resp.PageResp;
import com.fangshuo.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp ebookList(@Valid EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 使用post请求时，前面要加上@RequestBody，这个注解对应的是json方式的提交，需要加这个@RequestBody才能接收到。
    // 两种不同方式的表单提交：1. application/json 2. application/x-www-form-urlencoded 这个是表单方式的提交
    public CommonResp save(@RequestBody @Valid EbookSaveReq req){
        CommonResp<Object> resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp<Object> resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }
}
