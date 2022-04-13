package com.fangshuo.wiki.job;// package com.jiawa.wiki.job;

import com.fangshuo.wiki.service.EbookSnapshotService;
import com.fangshuo.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EbookSnapshotJob {
    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @Resource
    private SnowFlake snowFlake;


    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);

   /**
    * 每30秒更新电子书信息
    */
   @Scheduled(cron = "0/30 * * * * ?")
   public void doSnapshot() throws InterruptedException {
       // 增加日志流水号
       MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
       LOG.info("生成今日电子书快照开始");
       long start = System.currentTimeMillis();
       ebookSnapshotService.genSnapshot();
       LOG.info("生成今日电子书快照结束，耗时：{}毫秒", System.currentTimeMillis() - start);
   }

}
