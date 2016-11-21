package com.chouti.analyse.model;

import java.util.Vector;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-15.
 *******************************************************************************/
public class NewsTfidfVector {

    private Long newsId;

    private Vector<Double> tfidfV;


    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Vector getTfidfV() {
        return tfidfV;
    }

    public void setTfidfV(Vector tfidfV) {
        this.tfidfV = tfidfV;
    }
}
