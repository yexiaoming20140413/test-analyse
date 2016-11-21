package com.chouti.analyse.service;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.model.NewsTfIdf;
import com.chouti.analyse.model.NewsTfidfVector;
import com.chouti.analyse.mybatis.mapper.NewsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Vector;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-15.
 *******************************************************************************/
@Service
public class ConsineService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NewsMapper newsMapper;

    private static Vector<NewsTfidfVector> NEWS_TFIDF_VECTOR= new Vector<>();

    public void loadAllConsine(){
        if(!CommonParams.DEAL_TFIDF){
            return;
        }
        logger.info("初始化NewsTfidf队列开始");
        long startTime = System.currentTimeMillis();
        boolean loop = true;
        long id = Long.MAX_VALUE;
        Integer pageSize = CommonParams.LOAD_NO_HASH_NEWS_DB_PAGE_SIZE;
        while (loop) {
            List<NewsTfIdf> tfidfList = newsMapper.queryNewsTfidf(id, pageSize);
            if (CollectionUtils.isEmpty(tfidfList) || tfidfList.size() < pageSize) {
                loop = false;
            }
            for(NewsTfIdf newsTfidf:tfidfList){
                id = newsTfidf.getId();
                String tfidf = newsTfidf.getTfidf();
                if(!StringUtils.isEmpty(tfidf)){
                    Vector tfidfV = new Vector();
                    String[] arr = tfidf.split(" ");
                    for(String s:arr){
                        Double d = Double.parseDouble(s);
                        tfidfV.add(d);
                    }
                    NewsTfidfVector model = new NewsTfidfVector();
                    model.setNewsId(newsTfidf.getNewsId());
                    model.setTfidfV(tfidfV);
                    NEWS_TFIDF_VECTOR.add(model);
                }
            }
        }
        logger.info("初始化NewsTfidf队列结束,耗时:"+(System.currentTimeMillis()-startTime)+"ms");
    }

    public void addNewsTfidfVector(NewsTfidfVector v){
        if(!CommonParams.DEAL_TFIDF){
            return;
        }
        if(null != NEWS_TFIDF_VECTOR ){
            NEWS_TFIDF_VECTOR.add(v);
        }
    }

    public void compareNewsConsine(){
        if(!CommonParams.DEAL_TFIDF){
            return;
        }
        if(null != NEWS_TFIDF_VECTOR && NEWS_TFIDF_VECTOR.size() > 0){
            for(int i = 0;i < NEWS_TFIDF_VECTOR.size();i++){
                NewsTfidfVector model = NEWS_TFIDF_VECTOR.get(i);
                for(int j = i+1;j < NEWS_TFIDF_VECTOR.size();j++){
                    NewsTfidfVector model1 = NEWS_TFIDF_VECTOR.get(j);
                    if(model.getNewsId() == model1.getNewsId()){
                        continue;
                    }
                    double consined = calculateNewsConsine(model.getTfidfV(),model1.getTfidfV());
                    if(consined > 0.95){
                        logger.info("NewsId:"+model.getNewsId()+"与NewsId:"+model1.getNewsId()+"相似");
                    }
                }
            }
        }
    }

    public double calculateNewsConsine(Vector v1,Vector v2){
        if(!CommonParams.DEAL_TFIDF){
            return 0.0;
        }
        if(null == v1 || null == v2){
            return 0.0;
        }
        if(v1.size() != v2.size()){
            return 0.0;
        }
        double d1=0.0;
        double d2=0.0;
        double d3=0.0;
        for(int i = 0;i < v1.size();i++){
            d1 += (double)v1.get(i)*(double)v2.get(i);
            d2 += (double)v1.get(i)*(double)v1.get(i);
            d3 += (double)v2.get(i)*(double)v2.get(i);
        }
        double consined = d1/(Math.sqrt(d2)*Math.sqrt(d3));
        return consined;
    }


    public static void main(String args[]){
        double d = (double)13/(Math.sqrt(12)*Math.sqrt(16));
        System.out.println(d);
    }
}
