package com.chouti.analyse.mybatis.mapper;

import com.chouti.analyse.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-9.
 *******************************************************************************/
public interface NewsMapper {

    Long queryNewsCountByTime(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<News> queryNewsByTimeAndPage(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("start") Long start, @Param("size") Integer size);

    Integer updateNewsSimHash(@Param("id") Long id, @Param("hash") String hash);

    List<News> queryNoHashNewsList(@Param("startTime") Long startTime, @Param("newsId") Long newsId, @Param("size") Integer size);

    List<News> queryNewsListByTimeAndId(@Param("endTime") Long endTime, @Param("newsId") Long newsId, @Param("size") Integer size);

    Integer updateNewsRepeat(@Param("id") Long id, @Param("echo") Integer echo);

    Long queryRepeateNewsById(@Param("newsId") Long newsId, @Param("repeatId") Long repeatId);

    Integer insertRepeatNews(RepeatModel repeatModel);

    News queryNewsById(@Param("id") Long id);

    Long queryNewsHost(String host);

    Integer insertNewsHost(NewsHost newsHost);

    Integer updateNewsNohash(@Param("id") Long id, @Param("nohash") Integer nohash);

    Integer insertNewsTfidf(NewsTfIdf newsTfIdf);

    List<NewsTfIdf> queryNewsTfidf(@Param("id") Long id, @Param("size") Integer size);

    List<NewsCategory> queryCategoryList();

    Integer updateNewsCategory(@Param("id") Long id, @Param("categoryId") Integer categoryId);

    Integer updateTrainNewsCategory(@Param("id") Long id, @Param("categoryId") Integer categoryId);

    List<News> loadTrainFile(@Param("id") Long id, @Param("categoryId") Integer categoryId,@Param("size") Integer size);

    Integer insertTrainNews(News news);

    List<News> loadTrainNews(@Param("newsId") Long newsId, @Param("size") Integer size);

    Integer delNews(Long id);

}
