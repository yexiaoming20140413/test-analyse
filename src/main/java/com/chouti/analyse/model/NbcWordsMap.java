package com.chouti.analyse.model;

import java.util.Map;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-16.
 *******************************************************************************/
public class NbcWordsMap {

    private Integer categoryDocNum;

    private Integer categoryId;

    private String categoryName;

    private Map<String,Integer> nbcPositiveMap;

    private Map<String,Integer> nbcNegativeMap;

    private Integer positiveDocsNum;

    private Integer negativeDocsNum;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Map<String, Integer> getNbcPositiveMap() {
        return nbcPositiveMap;
    }

    public void setNbcPositiveMap(Map<String, Integer> nbcPositiveMap) {
        this.nbcPositiveMap = nbcPositiveMap;
    }

    public Map<String, Integer> getNbcNegativeMap() {
        return nbcNegativeMap;
    }

    public void setNbcNegativeMap(Map<String, Integer> nbcNegativeMap) {
        this.nbcNegativeMap = nbcNegativeMap;
    }

    public Integer getPositiveDocsNum() {
        return positiveDocsNum;
    }

    public void setPositiveDocsNum(Integer positiveDocsNum) {
        this.positiveDocsNum = positiveDocsNum;
    }

    public Integer getNegativeDocsNum() {
        return negativeDocsNum;
    }

    public void setNegativeDocsNum(Integer negativeDocsNum) {
        this.negativeDocsNum = negativeDocsNum;
    }

    public Integer getCategoryDocNum() {
        return categoryDocNum;
    }

    public void setCategoryDocNum(Integer categoryDocNum) {
        this.categoryDocNum = categoryDocNum;
    }
}
