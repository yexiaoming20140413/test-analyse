package com.chouti.analyse.train;

import com.chouti.analyse.configure.CommonParams;

import java.io.*;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-24.
 *******************************************************************************/
public class LoadWords {

    public static void main(String args[]) throws Exception {
        String readFilePath = "/home/xiaoming/newsLearn/data";
        String writeFilePath = "/home/xiaoming/Downloads/hanlp/HanLP-1.3.1/data/dictionary/custom/myDictionary.txt";
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFilePath), "UTF-8"));
        File file = new File(readFilePath);
        if (file.isDirectory()) {
            String[] files = file.list();
            for (String name : files) {
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
