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
 * xiaoming  on 16-11-9.
 *******************************************************************************/
@Component
@Configurable
@EnableScheduling
public class LoadSimhashSet {

    private Logger logger = LoggerFactory.getLogger(LoadSimhashSet.class);

    @Autowired
    private SimhashManagerService simhashManagerService;

    @Scheduled(cron = "00 01 00 * * ?")
    public void loadSimhashSet() {
        logger.info("初始化simhash加载到内存中开始");
        long startTime = System.currentTimeMillis();
        simhashManagerService.initSimhashSet();
        logger.info("初始化simhash加载到内存中,耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }
}
