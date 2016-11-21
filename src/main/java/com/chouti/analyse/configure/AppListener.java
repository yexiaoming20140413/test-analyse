package com.chouti.analyse.configure;

import com.chouti.analyse.service.ConsineService;
import com.chouti.analyse.service.NbcClassifierService;
import com.chouti.analyse.service.SimhashManagerService;
import com.chouti.analyse.service.TfIdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-10.
 *******************************************************************************/
public class AppListener implements ApplicationListener {
    private Logger logger = LoggerFactory.getLogger(AppListener.class);


    private SimhashManagerService simhashManagerService;

    private TfIdfService tfIdfService;

    private ConsineService consineService;

    private ConfigurableApplicationContext context;

    private NbcClassifierService nbcClassifierService;


    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        // 在这里可以监听到Spring Boot的生命周期
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            // 初始化环境变量
            logger.info("AppListener:初始化环境变量");
        } else if (event instanceof ApplicationPreparedEvent) {
            // 初始化完成
            logger.info("AppListener:初始化完成");
        } else if (event instanceof ContextRefreshedEvent) {
            // 应用刷新
            logger.info("AppListener:应用刷新");
        } else if (event instanceof ApplicationReadyEvent) {
            // 应用已启动完成
            logger.info("AppListener:应用已启动完成");
            this.context = ((ApplicationReadyEvent) event).getApplicationContext();
            simhashManagerService = (SimhashManagerService) context.getBean("simhashManagerService");
            tfIdfService = (TfIdfService) context.getBean("tfIdfService");
            consineService = (ConsineService) context.getBean("consineService");
            nbcClassifierService = (NbcClassifierService) context.getBean("nbcClassifierService");
            simhashManagerService.initSimhashSet();
            tfIdfService.initTfIDFWordMap();
            consineService.loadAllConsine();
            nbcClassifierService.loadCagoryWords();
        } else if (event instanceof ContextStartedEvent) {
            // 应用启动，需要在代码动态添加监听器才可捕获
            logger.info("AppListener:应用启动");
        } else if (event instanceof ContextStoppedEvent) {
            // 应用停止
            logger.info("AppListener:应用停止");
        } else if (event instanceof ContextClosedEvent) {
            // 应用关闭
            logger.info("AppListener:应用关闭");
        } else {
        }

    }
}
