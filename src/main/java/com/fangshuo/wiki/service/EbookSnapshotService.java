package com.fangshuo.wiki.service;

import com.fangshuo.wiki.mapper.DocMapper;
import com.fangshuo.wiki.mapper.EbookSnapshotMapperCust;
import com.fangshuo.wiki.resp.StatisticResp;
import com.fangshuo.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    @Resource
    private SnowFlake snowFlake;

    public void genSnapshot(){
        ebookSnapshotMapperCust.genSnapshot();
    }

    public List<StatisticResp> getStatistic(){
        return ebookSnapshotMapperCust.getStatistic();
    }

    public List<StatisticResp> get30Statistic(){
        return ebookSnapshotMapperCust.get30Statistic();
    }
}
