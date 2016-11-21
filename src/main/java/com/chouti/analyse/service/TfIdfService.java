package com.chouti.analyse.service;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.model.News;
import com.chouti.analyse.mybatis.mapper.NewsMapper;
import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;


/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-11.
 *******************************************************************************/
@Service
public class TfIdfService {
    private static Logger logger = LoggerFactory.getLogger(TfIdfService.class);
    @Autowired
    private NewsMapper newsMapper;

    private ChoutiSegment choutiSegment = new ChoutiSegment();

    private static long TOTAL_DOC_NUM=0;

    private static Map<String,Long> wordDocMap = new HashMap<>();

    @Autowired
    private RedisService redisService;


    public void initTfIDFWordMap(){
        if(!CommonParams.DEAL_TFIDF){
            return;
        }
        if(redisService.loadedRedis()){
            wordDocMap = redisService.loadWordDocsMap();
            TOTAL_DOC_NUM = redisService.loadTotalDocNum();
            return;
        }
        initData();
        boolean loop = true;
        long endTime = TimeUtils.getTodayEndTime();
        long newsId = Long.MAX_VALUE;
        Integer pageSize = CommonParams.LOAD_SIMEHASH_DB_PAGE_SIZE;
        while (loop) {
            List<News> newsList = newsMapper.queryNewsListByTimeAndId(endTime, newsId, pageSize);
            if (CollectionUtils.isEmpty(newsList) || newsList.size() < pageSize) {
                loop = false;
            }
            for(News news:newsList){
                staticisWordDocsMap(news);
                newsId = news.getId();
            }
        }
        logger.info("init ifidf end!");
        redisService.addTFIDFdata(wordDocMap,TOTAL_DOC_NUM);
    }

    public void initData(){
        if(!CommonParams.DEAL_TFIDF){
            return;
        }
        wordDocMap.clear();
        TOTAL_DOC_NUM = 0;
        redisService.initTFIDFReidsData();
    }

    /**
     * 统计词文档 字典
     */
    public void staticisWordDocsMap(News news){
        if(!CommonParams.DEAL_TFIDF){
            return;
        }
        Map<String,Integer> frequencyMap = choutiSegment.segmentFrequency(news.getContent());
        if(null == frequencyMap || frequencyMap.size() <= 0){
            return;
        }
        for(Iterator iterator = frequencyMap.entrySet().iterator(); iterator.hasNext();){
            Map.Entry entry = (Map.Entry) iterator.next();
            String word = (String) entry.getKey();
            if(wordDocMap.containsKey(word)){
                wordDocMap.put(word,wordDocMap.get(word)+1);
            }else{
                wordDocMap.put(word,1l);
            }
        }
        TOTAL_DOC_NUM++;
    }


    public String genNewsTfIdf(Map<String,Integer> wordMap,Integer textFrequece){
        if(!CommonParams.DEAL_TFIDF){
            return null;
        }
        String tfIdfStr ="";
        List<Double> tfidfList = new ArrayList<>();
        for(Iterator iterator = wordMap.entrySet().iterator(); iterator.hasNext();){
            Map.Entry entry = (Map.Entry) iterator.next();
            String word = (String) entry.getKey();
            Integer wordFrequece = wordMap.get(word);
            if(wordDocMap.containsKey(word)){
                wordDocMap.put(word,wordDocMap.get(word)+1);
            }else{
                wordDocMap.put(word,1l);
            }
            redisService.addWordDocsNum(word,1l);
            double tf = (double)wordFrequece/(double)textFrequece;
            Long wordDocNum = wordDocMap.get(word);
            if(null == wordDocNum){
                wordDocNum = 0l;
            }
            double idf = java.lang.Math.log(TOTAL_DOC_NUM/(double)wordDocNum+0.01);
            double tfIdf = tf * idf;
            tfidfList.add(tfIdf);
        }
        for(int i =0;i < CommonParams.TF_IDF_WORD_LEN;i++){
            if(i >= tfidfList.size()){
                tfIdfStr +="0.00";
                tfIdfStr +=" ";
            }else{
                tfIdfStr +=tfidfList.get(i);
                tfIdfStr +=" ";
            }

        }
        tfidfList = null;
        TOTAL_DOC_NUM++;
        redisService.addDocsNum();
        return tfIdfStr;
    }


    public static void main(String args[]){
        System.out.println( Math.log(10000 + 0.01));
    }

}
