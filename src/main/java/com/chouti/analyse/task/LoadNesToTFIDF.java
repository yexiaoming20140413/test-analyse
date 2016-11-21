package com.chouti.analyse.task;

import com.chouti.analyse.service.SimhashManagerService;
import com.chouti.analyse.service.TfIdfService;
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
 * xiaoming  on 16-11-11.
 *******************************************************************************/
@Component
@Configurable
@EnableScheduling
public class LoadNesToTFIDF {

    private Logger logger = LoggerFactory.getLogger(LoadNesToTFIDF.class);

    @Autowired
    private TfIdfService tfIdfService;

//    @Scheduled(cron = "0 */2 * * * ?")
    public void loadSimhashSet() {
        logger.info("初始化tf-idf词文档字典开始");
        long startTime = System.currentTimeMillis();
        tfIdfService.initTfIDFWordMap();
        logger.info("初始化tf-idf词文档字典结束,耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }
}
