package com.fangshuo.wiki.mapper;

import com.fangshuo.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {
    public void genSnapshot();

    List<StatisticResp> getStatistic();
}
