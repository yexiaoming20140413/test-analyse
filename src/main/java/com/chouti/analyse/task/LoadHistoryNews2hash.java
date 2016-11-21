package com.chouti.analyse.task;

import com.chouti.analyse.service.SimhashManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-10.
 *******************************************************************************/
@Component
@Configurable
@EnableScheduling
public class LoadHistoryNews2hash {
    private Logger logger = LoggerFactory.getLogger(LoadHistoryNews2hash.class);

    @Autowired
    private SimhashManagerService simhashManagerService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void loadHistoryNews2Hash() {
        logger.info("加载历史news 计算hash开始");
        long startTime = System.currentTimeMillis();
        simhashManagerService.loadHistoryNews2Hash();
        logger.info("加载历史news 计算hash结束,耗时：" + (System.currentTimeMillis() - startTime) + "ms");
    }

//    @Scheduled(cron = "0 */10 * * * ?")
//    public void compareHistoryHash(){
//        logger.info("比较历史news hash");
//        long startTime = System.currentTimeMillis();
//        simhashManagerService.compareListNewsHash();
//        logger.info("比较历史news hash结束,耗时："+(System.currentTimeMillis()-startTime)+"ms");
//    }
}
