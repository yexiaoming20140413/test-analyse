package com.chouti.analyse.segment;

import java.util.List;
import java.util.Map;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-14.
 *******************************************************************************/
public class ChoutiTermsBean {

    private List<ChoutiTerm> termList;

    private Map<String,Integer> frequencyMap;

    private Integer totalFrequence;

    public List<ChoutiTerm> getTermList() {
        return termList;
    }

    public void setTermList(List<ChoutiTerm> termList) {
        this.termList = termList;
    }

    public Map<String, Integer> getFrequencyMap() {
        return frequencyMap;
    }

    public void setFrequencyMap(Map<String, Integer> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }

    public Integer getTotalFrequence() {
        return totalFrequence;
    }

    public void setTotalFrequence(Integer totalFrequence) {
        this.totalFrequence = totalFrequence;
    }
}
