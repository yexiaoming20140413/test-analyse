package com.chouti.analyse.service;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.model.NbcWordsMap;
import com.chouti.analyse.model.News;
import com.chouti.analyse.model.NewsCategory;
import com.chouti.analyse.mybatis.mapper.NewsMapper;
import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.utils.NBClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-16.
 *******************************************************************************/
@Service
public class NbcClassifierService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${cagetory.words.fileDir}")
    private String wordsFileDir;

    @Autowired
    private NewsMapper newsMapper;

    private static ChoutiSegment choutiSegment = new ChoutiSegment();

    private static List<NbcWordsMap> allCategoryWordList = new ArrayList<>();

    public void loadCagoryWords(){
        List<NewsCategory> newsCategoryList = newsMapper.queryCategoryList();
        if(!CollectionUtils.isEmpty(newsCategoryList)){
            for(int i = 0;i < newsCategoryList.size();i++){
                NewsCategory category = newsCategoryList.get(i);
                loadCategoryNbcPositiveWords(category.getId());
            }
            loadCategoryNbcNegativeWords();
        }

    }

    /**
     * 加载所有分类正文本词-词频
     * @param categoryId
     */
    public void loadCategoryNbcPositiveWords(Integer categoryId){
        try{
            Map<String,Integer> nbcPositiveMap = new HashMap<>();
            String categoryWordFreFile = wordsFileDir + CommonParams.CATEGORY_WORD_FEATURE_IG_PRIFIX+categoryId+".dat";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(categoryWordFreFile), "UTF-8"));
            String line;
            String word="";
            Long lineNum = 0l;
            Integer nums = 0;
            line = br.readLine();
            Integer docNums = Integer.parseInt(line);
            while ((line = br.readLine()) != null) {
                lineNum++;
                if(lineNum == 1){
                    word = line;
                }else if(lineNum == 2){
                    nums = Integer.parseInt(line);
                    nbcPositiveMap.put(word,nums);
                    lineNum = 0l;
                }
            }
            NbcWordsMap nbcWordsMap = new NbcWordsMap();
            nbcWordsMap.setCategoryId(categoryId);
            nbcWordsMap.setNbcPositiveMap(nbcPositiveMap);
            nbcWordsMap.setPositiveDocsNum(docNums);
            allCategoryWordList.add(nbcWordsMap);
        }catch(Exception err){
            err.printStackTrace();
            logger.error("加载分类正文本词-词频异常，categoryId="+categoryId);
        }

    }


    /**
     * 加载所有分类负文本词-词频
     */
    public void loadCategoryNbcNegativeWords(){
        try{
            for(int i = 0;i < allCategoryWordList.size();i++){
                NbcWordsMap nbcWordsMap = allCategoryWordList.get(i);
                Map<String,Integer> nbcNegativeMap = new HashMap<>();
                Integer negativeDocsNum = 0;
                for(int j = 0; j < allCategoryWordList.size();j++){
                    NbcWordsMap nbcWordsMap1 = allCategoryWordList.get(j);
                    if(nbcWordsMap.getCategoryId() == nbcWordsMap1.getCategoryId()){
                        continue;
                    }
                    negativeDocsNum += nbcWordsMap1.getPositiveDocsNum();
                    Map<String,Integer> nbcPositiveMap = nbcWordsMap1.getNbcPositiveMap();
                    for (Iterator iterator = nbcPositiveMap.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String word = (String) entry.getKey();
                        if(nbcNegativeMap.containsKey(word)){
                            nbcNegativeMap.put(word,nbcNegativeMap.get(word)+nbcPositiveMap.get(word));
                        }else{
                            nbcNegativeMap.put(word,nbcPositiveMap.get(word));
                        }
                    }
                }
                nbcWordsMap.setNbcNegativeMap(nbcNegativeMap);
                nbcWordsMap.setNegativeDocsNum(negativeDocsNum);
            }
        }catch(Exception err){
            logger.error("加载分类负文本词-词频异常");
        }

    }

    /**
     * 比较新闻跟哪个分类最接近
     * @param news
     * @return
     */
    public Integer compareNewsCategory(News news){
        if(null == news || StringUtils.isEmpty(news.getContent())){
            return null;
        }
        Integer nearCategoryId = null;
        for(int i = 0;i < allCategoryWordList.size();i++){
            NbcWordsMap nbcWordsMap = allCategoryWordList.get(i);
            Map<String,Integer> newsWordMap = choutiSegment.segmentFrequency(news.getContent());
            if(null == newsWordMap || newsWordMap.size() <= 0){
                return null;
            }
            NBClassifier classifier = new NBClassifier(nbcWordsMap.getNbcPositiveMap(),nbcWordsMap.getPositiveDocsNum(), nbcWordsMap.getNbcNegativeMap(), nbcWordsMap.getNegativeDocsNum());
            double[] classProb = classifier.classify(newsWordMap);
            if(classProb[0] > classProb[1]){
                logger.info("classProb:"+classProb);
                nearCategoryId = nbcWordsMap.getCategoryId();
                if(null != nearCategoryId && nearCategoryId > 0){
                    Integer result = newsMapper.updateNewsCategory(news.getId(),nearCategoryId);
                    if(null == result || result <= 0){
                        logger.error("更新新闻分类失败:"+result);
                    }
                    break;
                }
            }
            classifier = null;
        }

        logger.info("NewsId="+news.getId()+"最接近的分类Id是:"+nearCategoryId);
        return nearCategoryId;
    }

}
