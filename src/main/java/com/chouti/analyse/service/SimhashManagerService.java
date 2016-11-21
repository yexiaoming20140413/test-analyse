package com.chouti.analyse.service;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.model.*;
import com.chouti.analyse.mybatis.mapper.NewsMapper;
import com.chouti.analyse.segment.ChoutiTermsBean;
import com.chouti.analyse.utils.TimeUtils;
import com.chouti.analyse.utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-9.
 *******************************************************************************/
@Service
public class SimhashManagerService {

    private Logger logger = LoggerFactory.getLogger(SimhashManagerService.class);

    private static List<Map<String, Long>> allSimHashList = new ArrayList<>();

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private ChoutiSimhashService choutiSimhashService;

    @Autowired
    private TfIdfService tfIdfService;

    @Autowired
    private NbcClassifierService nbcClassifierService;


    /**
     * 初始化所有simhash 到内存中
     */
    public void initSimhashSet() {
        allSimHashList.clear();
        initSimhashMapByDay();
        for (int i = 0; i < allSimHashList.size(); i++) {
            long startTime = TimeUtils.getLeftTodayStartTime(i);
            long endTime = TimeUtils.getLeftTodayEndTime(i);

            Map<String, Long> oneDayHashMap = loadSimhashMapByTime(startTime, endTime);
            if (null == oneDayHashMap || oneDayHashMap.size() <= 0) {
                continue;
            }
            Map<String, Long> oriMap = allSimHashList.get(i);
            oriMap.putAll(oneDayHashMap);
        }

    }

    public void initSimhashMapByDay() {
        for (int i = 0; i < CommonParams.LOAD_SIM_HASH_SET_DATE; i++) {
            Map<String, Long> oneDayHashMap = new HashMap<>();
            allSimHashList.add(oneDayHashMap);
        }
    }

    public Map<String, Long> loadSimhashMapByTime(long startTime, long endTime) {
        int page = 1;
        Map<String, Long> simHashMap = new HashMap<>();
        Long count = newsMapper.queryNewsCountByTime(startTime, endTime);
        if (null == count || count <= 0) {
            logger.info("startTime:" + startTime + "-endTime:" + endTime + "-news is empty!");
            return simHashMap;
        }
        PageBean pageBean = new PageBean(count);
        pageBean.setPageSize(CommonParams.LOAD_SIMEHASH_DB_PAGE_SIZE);
        pageBean.setPage(page);
        while (pageBean.getPage() <= pageBean.getTotalPage()) {
            List<News> newsList = newsMapper.queryNewsByTimeAndPage(startTime, endTime, pageBean.getStart(), pageBean.getPageSize());
            if (CollectionUtils.isEmpty(newsList)) {
                return simHashMap;
            }

            for (News news : newsList) {
                if (StringUtils.isEmpty(news.getSimhash())) {
                    continue;
                }
                if (!StringUtils.isEmpty(news.getSimhash())) {
                    simHashMap.put(news.getSimhash(), news.getId());
                }
            }
            page++;
            pageBean.setPage(page);
        }
        return simHashMap;
    }


    public News genNewsSimhash(News news) {
        if (null == news) {
            return news;
        }
        ChoutiTermsBean choutiTermsBean = choutiSimhashService.getTerms(news.getContent());
        String hash = null;
        String tfIdf = null;
        if(null != choutiTermsBean){
            hash = choutiSimhashService.getSimhash(choutiTermsBean.getTermList(), CommonParams.SIM_HASH_CODE_LEN);
            tfIdf = tfIdfService.genNewsTfIdf(choutiTermsBean.getFrequencyMap(),choutiTermsBean.getTotalFrequence());
        }
        if (StringUtils.isEmpty(hash)) {
            choutiTermsBean = choutiSimhashService.getTerms(news.getTitle());
            if(null != choutiTermsBean){
                hash = choutiSimhashService.getSimhash(choutiTermsBean.getTermList(), CommonParams.SIM_HASH_CODE_LEN);
                tfIdf = tfIdfService.genNewsTfIdf(choutiTermsBean.getFrequencyMap(),choutiTermsBean.getTotalFrequence());
            }
        }
        news.setSimhash(hash);
        if (!StringUtils.isEmpty(hash)) {
            Integer result = newsMapper.updateNewsSimHash(news.getId(), hash);
            if (null == result || result <= 0) {
                logger.error("更新news simhash失败,newsId=" + news.getId());
            }
        } else {
            Integer result = newsMapper.updateNewsNohash(news.getId(), 1);
            if (null == result || result <= 0) {
                logger.error("更新news nohash失败,newsId=" + news.getId());
            }
        }

        if(!StringUtils.isEmpty(tfIdf)){
            long now = System.currentTimeMillis();
            NewsTfIdf newsTfIdf = new NewsTfIdf();
            newsTfIdf.setTfidf(tfIdf);
            newsTfIdf.setCreateTime(now);
            newsTfIdf.setUpdateTime(now);
            newsTfIdf.setNewsId(news.getId());
            Integer result = newsMapper.insertNewsTfidf(newsTfIdf);
            if (null == result || result <= 0) {
                logger.error("更新news tfidf失败,newsId=" + news.getId());
            }
        }

        if (!StringUtils.isEmpty(news.getUrl())) {
            String host = UrlUtils.getNewsHost(news.getUrl());
            if (!StringUtils.isEmpty(host)) {
                Long result = newsMapper.queryNewsHost(host);
                if (null == result || result <= 0) {
                    NewsHost newsHost = new NewsHost();
                    newsHost.setHost(host);
                    long now = System.currentTimeMillis();
                    newsHost.setCreateTime(now);
                    newsHost.setUpdateTime(now);
                    newsHost.setWeight(1.0);
                    newsMapper.insertNewsHost(newsHost);
                }
            }
        }
        return news;
    }


    public void loadHistoryNews2Hash() {
        boolean loop = true;
        long startTime = TimeUtils.getLeftTodayStartTime(CommonParams.LOAD_SIM_HASH_SET_DATE);
        long newsId = 0l;
        Integer pageSize = CommonParams.LOAD_NO_HASH_NEWS_DB_PAGE_SIZE;
        while (loop) {
            List<News> newsList = newsMapper.queryNoHashNewsList(startTime, newsId, pageSize);
            if (CollectionUtils.isEmpty(newsList) || newsList.size() < pageSize) {
                loop = false;
            }
            for (News news : newsList) {
                newsId = news.getId();
                genNewsSimhash(news);
                if (!StringUtils.isEmpty(news.getSimhash())) {
                    compareNewsHash(news);
                }
                nbcClassifierService.compareNewsCategory(news);
            }
        }

    }


    public void compareListNewsHash() {
        boolean loop = true;
        long endTime = TimeUtils.getTodayEndTime();
        long newsId = Long.MAX_VALUE;
        Integer pageSize = 1000;
        while (loop) {
            List<News> newsList = newsMapper.queryNewsListByTimeAndId(endTime, newsId, pageSize);
            if (CollectionUtils.isEmpty(newsList) || newsList.size() < pageSize) {
                loop = false;
            }
            for (News news : newsList) {
                compareNewsHash(news);
            }
        }
    }


    public void compareNewsHash(News news) {
        try {
            long newsId = news.getId();
            String hash = news.getSimhash();
            if (StringUtils.isEmpty(hash)) {
                return;
            }

            for (Map<String, Long> codeMap : allSimHashList) {
                for (Iterator iterator = codeMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String code = (String) entry.getKey();
                    int d = choutiSimhashService.getHMDistance(news.getSimhash(), code);
                    //重复新闻
                    if (d <= CommonParams.MIN_HM_DISTACE_LEN && d >= 0) {
                        Long id = codeMap.get(code);
                        repeatNews(news, id);
                        return;
                    }
                }
            }
            long createTime = news.getCreateTime();
            int leftDay = TimeUtils.getDaysByTimeLeftToday(createTime);
            Map<String, Long> codeMap = allSimHashList.get(leftDay);
            codeMap.put(news.getSimhash(), news.getId());
        } catch (Exception err) {
            logger.error("比较hash异常:", err);
        }

    }

    /**
     * 处理重复新闻
     *
     * @param news
     * @param oriNewsId
     */
    public void repeatNews(News news, Long oriNewsId) {
        if (null == news || null == oriNewsId || oriNewsId <= 0 || oriNewsId == news.getId()) {
            return;
        }
        //更新news表表示重复新闻
        Integer result = newsMapper.updateNewsRepeat(news.getId(), 1);
        if (null == result || result <= 0) {
            logger.error("更新news repeat 失败");
            return;
        }

        News oriNews = newsMapper.queryNewsById(oriNewsId);
        if (null == oriNews) {
            return;
        }
        Integer sameSource = 0;
        String newsHost = null;
        String oriHost = null;
        if (!StringUtils.isEmpty(oriNews.getUrl()) && !StringUtils.isEmpty(news.getUrl())) {
            oriHost = UrlUtils.getNewsHost(oriNews.getUrl());
            newsHost = UrlUtils.getNewsHost(news.getUrl());
        }
        if (!StringUtils.isEmpty(oriHost) && !StringUtils.isEmpty(newsHost) && oriHost.equals(newsHost)) {
            sameSource = 1;
        }
        RepeatModel repeatModel = new RepeatModel();
        repeatModel.setNewsId(oriNewsId);
        repeatModel.setRepeatId(news.getId());
        long now = System.currentTimeMillis();
        repeatModel.setCreateTime(now);
        repeatModel.setUpdateTime(now);
        repeatModel.setSameSource(sameSource);
        Long result1 = newsMapper.queryRepeateNewsById(oriNewsId, news.getId());
        if (null != result1 && result1 > 0) {
            return;
        }
        Integer result2 = newsMapper.insertRepeatNews(repeatModel);
        if (null == result2 || result2 <= 0) {
            logger.error("添加repeat news 失败");
        }

    }


}
