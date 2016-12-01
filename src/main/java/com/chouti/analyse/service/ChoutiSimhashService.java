package com.chouti.analyse.service;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.segment.ChoutiTerm;
import com.chouti.analyse.segment.ChoutiTermsBean;
import com.chouti.analyse.simhash.SimHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-9.
 *******************************************************************************/
@Service
public class ChoutiSimhashService {

    private static Logger logger = LoggerFactory.getLogger(ChoutiSimhashService.class);

    private static SimHash distance = new SimHash();



    public String getSimhash(String text, int hashbits) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            SimHash simHash = new SimHash(text, hashbits);
            return simHash.strSimHash;
        } catch (Exception err) {
            logger.error("get simhash err", err);
        }
        return null;

    }

    public String getSimhash(List<ChoutiTerm> segWords, int hashbits) {
        if (CollectionUtils.isEmpty(segWords)) {
            return null;
        }
        try {
            SimHash simHash = new SimHash();
            simHash.simHash(segWords,hashbits);
            return simHash.strSimHash;
        } catch (Exception err) {
            logger.error("get simhash err", err);
        }
        return null;

    }

    public static int getHMDistance(String str1, String str2) {
        if (StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)) {
            return -1;
        }
        return distance.getDistance(str1, str2);
    }

    public ChoutiTermsBean getTerms(String text){
        if(StringUtils.isEmpty(text)){
            return null;
        }
        try{
            ChoutiSegment choutiSegment = new ChoutiSegment();
            ChoutiTermsBean termsBean = choutiSegment.segmentText(text);
            return termsBean;
        }catch(Exception err){
            logger.error("getTermsArray 分词异常："+err);
        }
        return null;
    }

    public static void main(String args[]){
        int d = getHMDistance("1111111010010100110111001011000111110011011001001111110101010100","0110110101010010000110100000001110101101101000110100110011000100");
        System.out.println("d:"+d);
    }

}
