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
    private static final String SOUGO_NEWS_FILE_PATH = "/home/xiaoming/newsLearn/news_train_fudan/";

    private static ChoutiSegment choutiSegment = new ChoutiSegment();

    private static Map<String, Integer> categoryMap1 = new HashMap<>();

    private static Map<Integer, BufferedWriter> bwMap = new HashMap<>();


    private static Map<String, String> map1 = new HashMap<>();

    public static void main(String args[]) throws Exception {
        initData();
        //将语料库分类
//        newsCategory();


        for (Iterator iterator = map1.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String word = (String) entry.getKey();
            Integer cateGoryId = categoryMap1.get(map1.get(word));
            segmentCagoryNews(cateGoryId);
        }


    }

    public static void segmentCagoryNews(Integer i) throws Exception{
        String categoryFile = SOUGO_NEWS_FILE_PATH + CommonParams.CATEGORY_NEWS_FILE_PRIFIX+i+".dat";
        String categoryWordFreFile = SOUGO_NEWS_FILE_PATH + CommonParams.CATEGORY_WORD_FEATURE_NBC_PRIFIX+i+".dat";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(categoryFile), "UTF-8"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryWordFreFile), "UTF-8"));
        String line;
        long lineNum = 0;
        Map<String,Integer> wordNumsMap = new HashMap<>();
        List<WordFrequece> wordFrequeces = new ArrayList<>();
        String leibie = "";

        while ((line = br.readLine()) != null) {
            lineNum++;
            if(lineNum > 500){
                break;
            }
            //统计词-文本数
            Map<String,Integer> wordDocsMap = choutiSegment.segmentFrequency(line,ChoutiSegment.SEGMENT_TYPE_NBC);
            if(null == wordDocsMap || wordDocsMap.size() <= 0){
                continue;
            }
            for(Iterator iterator = wordDocsMap.entrySet().iterator(); iterator.hasNext();){
                Map.Entry entry = (Map.Entry) iterator.next();
                String word = (String) entry.getKey();
                if(wordNumsMap.containsKey(word)){
                    wordNumsMap.put(word,wordNumsMap.get(word)+1);
                }else{
                    wordNumsMap.put(word,1);
                }
            }
        }
        for (Iterator iterator = wordNumsMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String word = (String) entry.getKey();
            Integer nums = wordNumsMap.get(word);
            WordFrequece wordFrequece = new WordFrequece();
            wordFrequece.setWord(word);
            wordFrequece.setNums(nums);
            wordFrequeces.add(wordFrequece);
        }
        Collections.sort(wordFrequeces, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((WordFrequece) o2).getNums().compareTo(((WordFrequece) o1).getNums());
            }
        });
        bw.write((lineNum-1)+"");
        bw.newLine();
        int maxNum = 0;
        for(WordFrequece w:wordFrequeces){
            bw.write(w.getWord());
            bw.newLine();
            bw.write(w.getNums().toString());
            bw.newLine();
            maxNum++;
        }
        br.close();
        bw.close();
    }

    public static void newsCategory() throws Exception{
        File file = new File(SOUGO_NEWS_FILE_PATH);
        if (file.isDirectory()) {
            String[] files = file.list();
            for (String name : files) {
                if(-1 == name.indexOf("-")){
                    continue;
                }
                String prifix = name.substring(0, name.indexOf("-"));
                if (map1.containsKey(prifix)) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(SOUGO_NEWS_FILE_PATH + name), "GBK"));
                    String line;
                    StringBuffer sb = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append(" ");
                    }
                    BufferedWriter bw = getBufferWriter(categoryMap1.get(map1.get(prifix)));
                    bw.write(sb.toString());
                    bw.newLine();
                }
            }
        }
        closeBufferWriter();
    }

    public static void initData() {
        map1.put("C38", "政治");
        map1.put("C19", "计算机");
        map1.put("C34", "经济");
        map1.put("C32", "农业");
        map1.put("C3", "艺术");
//        map1.put("C23", "矿藏");
//        map1.put("C29", "运输");
        map1.put("C39", "体育");
        map1.put("C11", "太空");
        map1.put("C7", "历史");
//        map1.put("C37", "军事");
        map1.put("C31", "环境");
//        map1.put("C36", "医学");
//        map1.put("C35", "法律");
//        map1.put("C6", "哲学");
//        map1.put("C15", "能量");
//        map1.put("C5", "教育");
//        map1.put("C4", "文学");
//        map1.put("C16", "电子工业");
//        map1.put("C17", "通讯");

        categoryMap1.put("政治", 8);
        categoryMap1.put("计算机", 9);
        categoryMap1.put("经济", 10);
        categoryMap1.put("农业", 11);
        categoryMap1.put("艺术", 12);
//        categoryMap1.put("矿藏", 13);
//        categoryMap1.put("运输", 14);
        categoryMap1.put("体育", 6);
        categoryMap1.put("太空", 15);
        categoryMap1.put("历史", 16);
//        categoryMap1.put("军事", 4);
        categoryMap1.put("环境", 17);
//        categoryMap1.put("医学", 18);
//        categoryMap1.put("法律", 19);
//        categoryMap1.put("哲学", 20);
//        categoryMap1.put("能量", 21);
//        categoryMap1.put("教育", 3);
//        categoryMap1.put("文学", 22);
//        categoryMap1.put("电子工业", 23);
//        categoryMap1.put("通讯", 24);


    }

    public static void closeBufferWriter() throws Exception {
        for (Iterator iterator = bwMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer urlCode = (Integer) entry.getKey();
            BufferedWriter bw = bwMap.get(urlCode);
            bw.close();
        }
    }


    public static BufferedWriter getBufferWriter(Integer category) throws Exception {
        if (null != bwMap) {
            BufferedWriter bw = bwMap.get(category);
            if (null == bw) {
                String weibosTagSegLdaPath = SOUGO_NEWS_FILE_PATH + File.separator + "category_" + category + ".dat";
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weibosTagSegLdaPath), "UTF-8"));
                bwMap.put(category, bw);
            }
            return bw;
        }
        return null;
    }

}
