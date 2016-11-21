package com.chouti.analyse.train;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.segment.ChoutiSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-16.
 *******************************************************************************/
public class TrainFudanNews {
    private static Logger logger = LoggerFactory.getLogger(TrainFudanNews.class);
    private static final String SOUGO_NEWS_FILE_PATH = "/home/xiaoming/newsLearn/fudanText";

    private static ChoutiSegment choutiSegment = new ChoutiSegment();

    private static Map<String,Integer> categoryMap1 = new HashMap<>();

    private static Map<String,String> map1 = new HashMap<>();
    public static void main(String args[]) throws Exception{

        File file = new File(SOUGO_NEWS_FILE_PATH);
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f: files){
                if(!f.isDirectory()){
                    String name = f.getName();
                    System.out.println("fileName:"+name);
                }
            }
        }
    }

    public static void initData(){
        map1.put("Politics","政治");
        map1.put("Computer","计算机");
        map1.put("Economy","经济");
        map1.put("Agriculture","农业");
        map1.put("Art","体育");
        map1.put("Mine","法制");
        map1.put("Transport","法制");
        map1.put("Sports","法制");
        map1.put("Space","太空");
        map1.put("History","历史");
        map1.put("Military","历史");
        map1.put("Enviornment","历史");
    }


}
