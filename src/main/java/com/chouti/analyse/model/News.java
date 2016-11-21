package com.chouti.analyse.model;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-9.
 *******************************************************************************/
public class News {

    private Long id;
    /**
     * 新闻标题
     */
    private String title;
    /**
     * 新闻链接
     */
    private String url;
    /**
     * 新闻分类
     */
    private String category;
    /**
     * 新闻内容
     */
    private String content;
    /**
     * 新闻配图
     */
    private String thumbnail;
    /**
     * 类型 [0:文本类型 1:图集 2:视频]
     */
    private Integer type;
    /**
     * 新闻发布时间
     */
    private Long publishTime;
    /**
     * 新闻抓取时间 时间戳
     */
    private Long createTime;
    /**
     * 新闻最后更新时间戳
     */
    private Long updateTime;
    /**
     * 指纹hash
     */
    private String simhash;
    /**
     * 是否发布到抽屉
     */
    private Integer pushChouti;
    /**
     * 是否能生成指纹hash
     */
    private Integer nohash;
    /**
     * tfidf 数组
     */
    private String tfIdf;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
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

    public String getSimhash() {
        return simhash;
    }

    public void setSimhash(String simhash) {
        this.simhash = simhash;
    }


    public Integer getPushChouti() {
        return pushChouti;
    }

    public void setPushChouti(Integer pushChouti) {
        this.pushChouti = pushChouti;
    }

    public Integer getNohash() {
        return nohash;
    }

    public void setNohash(Integer nohash) {
        this.nohash = nohash;
    }

    public String getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(String tfIdf) {
        this.tfIdf = tfIdf;
    }
}
