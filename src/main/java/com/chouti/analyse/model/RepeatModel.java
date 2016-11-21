package com.chouti.analyse.model;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-10.
 *******************************************************************************/
public class RepeatModel {

    private Long id;

    private Long newsId;

    private Long repeatId;

    private Long createTime;

    private Long updateTime;

    private Integer sameSource;

    private String newsHost;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Long getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(Long repeatId) {
        this.repeatId = repeatId;
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

    public Integer getSameSource() {
        return sameSource;
    }

    public void setSameSource(Integer sameSource) {
        this.sameSource = sameSource;
    }

    public String getNewsHost() {
        return newsHost;
    }

    public void setNewsHost(String newsHost) {
        this.newsHost = newsHost;
    }
}
