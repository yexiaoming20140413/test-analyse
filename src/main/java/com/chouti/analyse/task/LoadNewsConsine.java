package com.chouti.analyse.task;

import com.chouti.analyse.service.ConsineService;
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
 * xiaoming  on 16-11-15.
 *******************************************************************************/
@Component
@Configurable
@EnableScheduling
public class LoadNewsConsine {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConsineService consineService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void loadSimhashSet() {
        logger.info("比较News Consine 开始");
        long startTime = System.currentTimeMillis();
        consineService.compareNewsConsine();
        logger.info("比较News Consine 结束,耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }
}
