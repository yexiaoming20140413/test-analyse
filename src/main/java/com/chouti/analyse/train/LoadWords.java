package com.chouti.analyse.train;

import java.io.*;
import java.util.*;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-24.
 *******************************************************************************/
public class LoadWords {

    //明星 足球
    private static List<String> loadKeyWordList = new ArrayList<>();
    private static final String SOU_GOU_CI_KU_FILE_PATH ="/home/xiaoming/docs/ciku/";
    private static final String NLP_CIKU_FILE_PATH="/home/xiaoming/newsLearn/data/";

    private static final String MY_DICTORY_WORDS_FILE_PATH="/home/xiaoming/Downloads/hanlp/HanLP-1.3.1/data/dictionary/custom/myDictionary.txt";
    private static Map<String,Integer> wordsMap = new HashMap<String,Integer>();
    public static void initKeyWordList(){
        loadKeyWordList.add("足球");
        loadKeyWordList.add("明星");
        loadKeyWordList.add("娱乐");
        loadKeyWordList.add("娱乐");
        loadKeyWordList.add("运动");
        loadKeyWordList.add("政治");
        loadKeyWordList.add("职业");
        loadKeyWordList.add("制造");
        loadKeyWordList.add("中医");
        loadKeyWordList.add("中药");
        loadKeyWordList.add("作家");
    }
    public static void main(String args[]) throws Exception {
        initKeyWordList();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(MY_DICTORY_WORDS_FILE_PATH), "UTF-8"));
        File file = new File(NLP_CIKU_FILE_PATH);
        if (file.isDirectory()) {
            String[] files = file.list();
            for (String name : files) {
                boolean load = false;
                for(String key:loadKeyWordList){
                    if(-1 != name.indexOf(key)){
                        load = true;
                        break;
                    }
                }
                if(!load){
                    continue;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(NLP_CIKU_FILE_PATH + name), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.newLine();
                }
                br.close();
            }
        }

        File cikuFile = new File(SOU_GOU_CI_KU_FILE_PATH);
        if (cikuFile.isDirectory()) {
            String[] files = cikuFile.list();
            for (String name : files) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(SOU_GOU_CI_KU_FILE_PATH + name), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    if(wordsMap.containsKey(line)){
                        continue;
                    }
                    wordsMap.put(line,1);
                }
                br.close();
            }
        }

        for (Iterator iterator = wordsMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String word = (String) entry.getKey();
            bw.write(word);
            bw.newLine();
        }
        bw.close();
    }

}
