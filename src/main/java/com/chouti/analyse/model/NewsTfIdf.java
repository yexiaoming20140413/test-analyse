package com.chouti.analyse.model;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-14.
 *******************************************************************************/
public class NewsTfIdf {

    private Long id;

    private Long newsId;

    private String tfidf;

    private Long createTime;

    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTfidf() {
        return tfidf;
    }

    public void setTfidf(String tfidf) {
        this.tfidf = tfidf;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }
}
