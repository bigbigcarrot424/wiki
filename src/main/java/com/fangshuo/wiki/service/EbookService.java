package com.fangshuo.wiki.service;

import com.fangshuo.wiki.domain.Ebook;
import com.fangshuo.wiki.domain.EbookExample;
import com.fangshuo.wiki.mapper.EbookMapper;
import com.fangshuo.wiki.req.EbookReq;
import com.fangshuo.wiki.resp.EbookResp;
import com.fangshuo.wiki.util.CopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        //当name不为空时
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        List<EbookResp> respList = CopyUtil.copyList(ebookList, EbookResp.class);
        /*List<EbookResp> respList = new ArrayList<>();
        for (Ebook ebook : ebookList) {
            EbookResp ebookResp = new EbookResp();
            BeanUtils.copyProperties(ebook, ebookResp);
            respList.add(ebookResp);
        }*/

        return respList;
    }
}
