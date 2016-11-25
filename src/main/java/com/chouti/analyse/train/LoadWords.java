package com.chouti.analyse.train;

import com.chouti.analyse.configure.CommonParams;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-24.
 *******************************************************************************/
public class LoadWords {

    //明星 足球
    private static List<String> loadKeyWordList = new ArrayList<>();

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
        String readFilePath = "/home/xiaoming/newsLearn/data";
        String writeFilePath = "/home/xiaoming/Downloads/hanlp/HanLP-1.3.1/data/dictionary/custom/myDictionary.txt";
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFilePath), "UTF-8"));
        File file = new File(readFilePath);
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
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/home/xiaoming/newsLearn/data/" + name), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.newLine();
                }
                br.close();
            }
        }
        bw.close();
    }

}
