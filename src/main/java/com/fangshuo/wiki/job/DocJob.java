package com.fangshuo.wiki.job;// package com.jiawa.wiki.job;

import com.fangshuo.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {
    @Resource
    private DocService docService;

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

   /**
    * 每30秒更新电子书信息
    */
   @Scheduled(cron = "0/30 * * * * ?")
   public void cron() throws InterruptedException {
       docService.updateEbookInfo();
   }

}
