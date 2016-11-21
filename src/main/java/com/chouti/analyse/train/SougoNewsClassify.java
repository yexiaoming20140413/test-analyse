package com.chouti.analyse.train;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-16.
 *******************************************************************************/
public class SougoNewsClassify {
    private static Logger logger = LoggerFactory.getLogger(SougoNewsClassify.class);

    private static final String SOUGO_NEWS_FILE_PATH = "/home/xiaoming/newsLearn/";

    private static Map<String, String> urlCategoryMap = new HashMap<>();

    private static Map<String, BufferedWriter> bwMap = new HashMap<>();

    private static Map<String,Integer> cateNewsNum = new HashMap<>();

    private static Map<String,Integer> categoryMap = new HashMap<>();


    public static void main(String args[]) throws Exception {
        initCategoryMap();
        String fenleiFile = SOUGO_NEWS_FILE_PATH + "categories_2012.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fenleiFile), "GBK"));
        String line;
        String category = "";
        while ((line = br.readLine()) != null) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            if (-1 != line.indexOf("：")) {
                category = line.substring(0, line.indexOf("："));
            }
            if (-1 != line.indexOf("http")) {
                Integer categoryI = categoryMap.get(category);
                if(null == categoryI){
                    continue;
                }
                urlCategoryMap.put(line, categoryI.toString());
                category = "";
            }
        }
        br.close();
        logger.info("urlCategoryMap:" + urlCategoryMap);
        loadAllNews();
        for (Iterator iterator = bwMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String urlCode = (String) entry.getKey();
            BufferedWriter bw = bwMap.get(urlCode);
            bw.close();
        }
    }

    public static void loadAllNews() throws Exception {
        String fenleiFile = SOUGO_NEWS_FILE_PATH + "news_sohusite_xml.dat";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fenleiFile), "GBK"));
        String line;
        long lineNum = 0;
        String leibie = "";
        while ((line = br.readLine()) != null) {
            lineNum++;
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            if (lineNum == 2) {
                String url = line.substring(5, line.length() - 6);
                leibie = getCategory(url);
            } else if (lineNum == 5) {
                String content = line.substring(9, line.length() - 10);
                if (!StringUtils.isEmpty(leibie) && !StringUtils.isEmpty(content)) {
                    BufferedWriter bw = getBufferWriter(leibie);
                    if(cateNewsNum.containsKey(leibie)){
                        Integer count = cateNewsNum.get(leibie);
                        if(count >= 500){
                            continue;
//                            cateNewsNum.put(leibie,count+1);
                        }else{
                            cateNewsNum.put(leibie,count+1);
                        }
                    }else{
                        cateNewsNum.put(leibie,1);
                    }
                    if (null != bw) {
                        bw.write(content);
                        bw.newLine();
                    }
                }
                leibie = "";
            } else if (lineNum == 6) {
                lineNum = 0;
            }

        }
        br.close();
    }

    public static String getCategory(String url) {
        for (Iterator iterator = urlCategoryMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String urlCode = (String) entry.getKey();
            if (url.startsWith(urlCode)) {
                return urlCategoryMap.get(urlCode);
            }
        }
        return null;
    }

    public static BufferedWriter getBufferWriter(String category) throws Exception{
        if (null != bwMap) {
            BufferedWriter bw = bwMap.get(category);
            if(null == bw){
                String weibosTagSegLdaPath = "/home/xiaoming/newsLearn/news_train_souhu" + File.separator +"category_"+category + ".dat";
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weibosTagSegLdaPath), "UTF-8"));
                bwMap.put(category, bw);
            }
            return bw;
        }
        return null;
    }

    public static void initCategoryMap(){

        categoryMap.put("财经",1);
        categoryMap.put("传媒",2);
        categoryMap.put("教育",3);
        categoryMap.put("军事",4);
        categoryMap.put("汽车",5);
        categoryMap.put("体育",6);
        categoryMap.put("娱乐",7);

    }


}
