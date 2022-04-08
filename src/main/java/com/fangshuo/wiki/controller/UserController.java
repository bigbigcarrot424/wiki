package com.fangshuo.wiki.controller;

import com.fangshuo.wiki.req.UserQueryReq;
import com.fangshuo.wiki.req.UserSaveReq;
import com.fangshuo.wiki.resp.CommonResp;
import com.fangshuo.wiki.resp.UserQueryResp;
import com.fangshuo.wiki.resp.PageResp;
import com.fangshuo.wiki.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public CommonResp userList(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 使用post请求时，前面要加上@RequestBody，这个注解对应的是json方式的提交，需要加这个@RequestBody才能接收到。
    // 两种不同方式的表单提交：1. application/json 2. application/x-www-form-urlencoded 这个是表单方式的提交
    public CommonResp save(@RequestBody @Valid UserSaveReq req){
        CommonResp<Object> resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp<Object> resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }
}
