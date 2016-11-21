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
public class TrainSouhuNews {
    private static Logger logger = LoggerFactory.getLogger(TrainSouhuNews.class);
    private static final String SOUGO_NEWS_FILE_PATH = "/home/xiaoming/newsLearn/news_train_souhu/";

    private static ChoutiSegment choutiSegment = new ChoutiSegment();

    private static Map<String,Integer> categoryMap1 = new HashMap<>();
    public static void main(String args[]) throws Exception{
        categoryMap1.put("财经",1);
        categoryMap1.put("传媒",2);
        categoryMap1.put("教育",3);
        categoryMap1.put("军事",4);
        categoryMap1.put("汽车",5);
        categoryMap1.put("体育",6);
        categoryMap1.put("娱乐",7);

        for(int i = 1;i <= categoryMap1.size();i++){
            segmentCagoryNews(i);
        }

    }

    public static void segmentCagoryNews(int i) throws Exception{
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
//            //统计词-词频
//            List<ChoutiTerm> terms =  choutiSegment.segment(line,ChoutiSegment.SEGMENT_TYPE_NBC);
//            if(null == terms){
//                continue;
//            }
//            for(ChoutiTerm term:terms){
//                if(wordNumsMap.containsKey(term.getWord())){
//                    wordNumsMap.put(term.getWord(),wordNumsMap.get(term.getWord())+1);
//                }else{
//                    wordNumsMap.put(term.getWord(),1);
//                }
//            }
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
        bw.write(lineNum+"");
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
}
