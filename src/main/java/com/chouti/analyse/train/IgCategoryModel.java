package com.chouti.analyse.train;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-17.
 *******************************************************************************/
public class IgCategoryModel {

    private Integer categoryId;

    private Integer N1;

    private Integer N2;

    private Map<String,Integer> wordDocNumMap;

    private List<IgWordModel> igWordModelList;

    public Integer getN1() {
        return N1;
    }

    public void setN1(Integer n1) {
        N1 = n1;
    }

    public Integer getN2() {
        return N2;
    }

    public void setN2(Integer n2) {
        N2 = n2;
    }

    public List<IgWordModel> getIgWordModelList() {
        return igWordModelList;
    }

    public void setIgWordModelList(List<IgWordModel> igWordModelList) {
        this.igWordModelList = igWordModelList;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Map<String, Integer> getWordDocNumMap() {
        return wordDocNumMap;
    }

    public void setWordDocNumMap(Map<String, Integer> wordDocNumMap) {
        this.wordDocNumMap = wordDocNumMap;
    }

    public void addWordIg(IgWordModel model){
        if(null == igWordModelList){
            igWordModelList = new ArrayList<>();
        }
        igWordModelList.add(model);
    }
}
