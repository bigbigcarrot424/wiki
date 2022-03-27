package com.fangshuo.wiki.controller;

import com.fangshuo.wiki.req.EbookReq;
import com.fangshuo.wiki.resp.CommonResp;
import com.fangshuo.wiki.resp.EbookResp;
import com.fangshuo.wiki.resp.PageResp;
import com.fangshuo.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp ebookList(EbookReq req){
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
