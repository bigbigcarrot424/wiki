package com.fangshuo.wiki.service;

import com.fangshuo.wiki.domain.Doc;
import com.fangshuo.wiki.domain.DocExample;
import com.fangshuo.wiki.mapper.DocMapper;
import com.fangshuo.wiki.req.DocQueryReq;
import com.fangshuo.wiki.req.DocSaveReq;
import com.fangshuo.wiki.resp.DocQueryResp;
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
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<DocQueryResp> list(DocQueryReq req){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();

        docExample.setOrderByClause("sort asc");
        PageHelper.startPage(req.getPage(), req.getSize());
        //PageHelper会作用到这个查询上
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);
        /*List<DocResp> respList = new ArrayList<>();
        for (Doc doc : docList) {
            DocResp docResp = new DocResp();
            BeanUtils.copyProperties(doc, docResp);
            respList.add(docResp);
        }*/

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }
    public List<DocQueryResp> all(){
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");

        List<Doc> docList = docMapper.selectByExample(docExample);
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);

        return respList;
    }

    /**
     * 保存
     */
    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(req.getId())){
            //如果是空，那么新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }else{
            //更新
            docMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }
}
