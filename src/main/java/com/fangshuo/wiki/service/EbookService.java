package com.fangshuo.wiki.service;

import com.fangshuo.wiki.domain.Ebook;
import com.fangshuo.wiki.domain.EbookExample;
import com.fangshuo.wiki.mapper.EbookMapper;
import com.fangshuo.wiki.req.EbookQueryReq;
import com.fangshuo.wiki.req.EbookSaveReq;
import com.fangshuo.wiki.resp.EbookQueryResp;
import com.fangshuo.wiki.resp.PageResp;
import com.fangshuo.wiki.util.CopyUtil;
import com.fangshuo.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        //当name不为空时
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        if(!ObjectUtils.isEmpty(req.getCategoryId2())){
            criteria.andCategory2IdEqualTo(req.getCategoryId2());
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        //PageHelper会作用到这个查询上
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        /*List<EbookResp> respList = new ArrayList<>();
        for (Ebook ebook : ebookList) {
            EbookResp ebookResp = new EbookResp();
            BeanUtils.copyProperties(ebook, ebookResp);
            respList.add(ebookResp);
        }*/

        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(EbookSaveReq req){
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())){
            //如果是空，那么新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        }else{
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id){
        ebookMapper.deleteByPrimaryKey(id);
    }
}
