package com.fangshuo.wiki.controller;

import com.fangshuo.wiki.req.CategoryQueryReq;
import com.fangshuo.wiki.req.CategorySaveReq;
import com.fangshuo.wiki.resp.CategoryQueryResp;
import com.fangshuo.wiki.resp.CommonResp;
import com.fangshuo.wiki.resp.PageResp;
import com.fangshuo.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp categoryList(@Valid CategoryQueryReq req){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> list = categoryService.all();
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 使用post请求时，前面要加上@RequestBody，这个注解对应的是json方式的提交，需要加这个@RequestBody才能接收到。
    // 两种不同方式的表单提交：1. application/json 2. application/x-www-form-urlencoded 这个是表单方式的提交
    public CommonResp save(@RequestBody @Valid CategorySaveReq req){
        CommonResp<Object> resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp<Object> resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
