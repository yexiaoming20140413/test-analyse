package com.chouti.analyse.service;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.configure.RedisKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-14.
 *******************************************************************************/
@Service
public class RedisService {

    private static Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 判断是否初始化数据到redis中
     * @return
     */
    public boolean loadedRedis(){
        String result = redisTemplate.opsForValue().get(RedisKeys.TOTAL_DOCS_KEY_STR);
        if(StringUtils.isEmpty(result)){
            return false;
        }
        return true;
    }

    /**
     * 从redis中加载词文档 字典
     * @return
     */
    public Map<String,Long> loadWordDocsMap(){
        Map<Object, Object> redisMap = redisTemplate.opsForHash().entries(RedisKeys.WORD_DOCS_KEY_HS);
        Map<String,Long> resultMap = new HashMap<>();
        for (Iterator iterator = redisMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String word = (String) entry.getKey();
            String value = (String) redisMap.get(word);
            Long num = Long.parseLong(value);
            resultMap.put(word,num);
        }
        redisMap = null;
        return resultMap;
    }

    /**
     * 从redis中加载所有文档数量
     * @return
     */
    public Long loadTotalDocNum(){
        String value = redisTemplate.opsForValue().get(RedisKeys.TOTAL_DOCS_KEY_STR);
        if(StringUtils.isEmpty(value)){
            return 0l;
        }
        Long result = Long.parseLong(value);
        return result;
    }
    /**
     * 初始化TFIDF统计相关数据
     */
    public void initTFIDFReidsData(){
        redisTemplate.delete(RedisKeys.WORD_DOCS_KEY_HS);
        redisTemplate.delete(RedisKeys.TOTAL_DOCS_KEY_STR);
    }

    public void addTFIDFdata(Map<String,Long> dataMap,Long totalDocNum){
        if(CollectionUtils.isEmpty(dataMap) || null == totalDocNum){
            logger.error("往redis中初始化TFIDF数据参数错误:"+totalDocNum);
            return;
        }
        Map<String,String> resultMap = new HashMap<>();
        for (Iterator iterator = dataMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String word = (String) entry.getKey();
            Long num = (Long) dataMap.get(word);
            resultMap.put(word,num.toString());
        }
        redisTemplate.opsForHash().putAll(RedisKeys.WORD_DOCS_KEY_HS,resultMap);
        redisTemplate.opsForValue().set(RedisKeys.TOTAL_DOCS_KEY_STR,totalDocNum.toString());
        resultMap = null;
    }


    /**
     * 添加词文档数
     * @param word
     * @param docNum
     */
    public void addWordDocsNum(String word,Long docNum){
        redisTemplate.opsForHash().increment(RedisKeys.WORD_DOCS_KEY_HS,word,docNum);
    }

    public void addDocsNum(){
        redisTemplate.opsForValue().increment(RedisKeys.TOTAL_DOCS_KEY_STR,1l);
    }
}
