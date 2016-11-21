package com.chouti.analyse.train;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.model.NbcWordsMap;
import com.chouti.analyse.model.NewsCategory;
import com.chouti.analyse.segment.ChoutiSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-17.
 *******************************************************************************/
public class IgTrain {

    private static Logger logger = LoggerFactory.getLogger(IgTrain.class);
    private static final String SOUGO_NEWS_FILE_PATH = "/home/xiaoming/newsLearn/news_fenlei/";

    private static ChoutiSegment choutiSegment = new ChoutiSegment();

    private static Integer N1=0;

    private static List<IgCategoryModel> igCategoryModelList = new ArrayList<>();

    private static Integer CATEGORY_FEATURE_WORD_LEN=300;

    private static List<NbcWordsMap> allCategoryWordList = new ArrayList<>();

    private static Map<String,Integer> categoryMap1 = new HashMap<>();
    public static void main(String args[]) throws Exception{
        logger.info("IG分类特征词选取开始");
        long startTime = System.currentTimeMillis();
        categoryMap1.put("财经",1);
        categoryMap1.put("传媒",2);
        categoryMap1.put("教育",3);
        categoryMap1.put("军事",4);
        categoryMap1.put("汽车",5);
        categoryMap1.put("体育",6);
        categoryMap1.put("娱乐",7);
        for(int i = 1;i <= categoryMap1.size();i++){

            //加载贝叶斯分类词词频
            loadCategoryNbcPositiveWords(i);
            logger.info("IG分类特征词-step1:统计每个分类词出现文档次数");
            statisticalCategoryWordDocNums(i);
        }
        //计算每个分类每个词的正负文本频率和不出现频率
        logger.info("IG分类特征词-step2:计算每个分类每个词的正负文本频率和不出现频率");
        genWordIgSvm();
        //计算每个分类每个词的信息增益值
        logger.info("IG分类特征词-step3:计算每个分类每个词的信息增益值");
        calWordInfoGain();
        //针对每个分类词的信息增益值排序
        logger.info("IG分类特征词-step4:对每个分类所有词信息增益值排序");
        sortInfoGain();
        //保存每个分类经过信息增益后的特征词词频
        logger.info("IG分类特征词-step5:保存每个分类经过IG计算后的特征词-词频");
        saveCagegoryIgWords();
        logger.info("IG分类特征词选取结束,耗时:"+(System.currentTimeMillis()-startTime)+"ms");
    }

    public static void saveCagegoryIgWords() throws Exception{

        for(int i = 0;i < igCategoryModelList.size();i++){
            IgCategoryModel igCategoryModel = igCategoryModelList.get(i);
            List<IgWordModel> igWordModelList = igCategoryModel.getIgWordModelList();
            String categoryWordFreFile = SOUGO_NEWS_FILE_PATH + CommonParams.CATEGORY_WORD_FEATURE_IG_PRIFIX+igCategoryModel.getCategoryId()+".dat";
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryWordFreFile), "UTF-8"));
            Map<String,Integer> wordsMap = null;
            Integer categoryDocNum = 0;
            if(!CollectionUtils.isEmpty(igWordModelList)){
                Integer categoryId = igCategoryModel.getCategoryId();
                for(NbcWordsMap nbcWordsMap: allCategoryWordList){
                    if(nbcWordsMap.getCategoryId() == categoryId){
                        wordsMap = nbcWordsMap.getNbcPositiveMap();
                        categoryDocNum = nbcWordsMap.getCategoryDocNum();
                        break;
                    }
                }
                bw.write(categoryDocNum+"");
                bw.newLine();
                int index = 0;
                for(IgWordModel igWordModel:igWordModelList){
                    index ++;
                    if(index > CATEGORY_FEATURE_WORD_LEN){
                        break;
                    }
                    String word = igWordModel.getWord();
                    Integer num = wordsMap.get(word);
                    bw.write(word);
                    bw.newLine();
                    if(num == null){
                        logger.info("num is null");
                    }
                    bw.write(num+"");
                    bw.newLine();
                }
            }
            bw.close();
        }
    }



    /**
     * 加载所有分类正文本词-词频
     * @param categoryId
     */
    public static void loadCategoryNbcPositiveWords(Integer categoryId){
        try{
            Map<String,Integer> nbcPositiveMap = new HashMap<>();
            String categoryWordFreFile = SOUGO_NEWS_FILE_PATH + CommonParams.CATEGORY_WORD_FEATURE_NBC_PRIFIX+categoryId+".dat";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(categoryWordFreFile), "UTF-8"));
            String line;
            String word="";
            Long lineNum = 0l;
            Integer nums = 0;
            line = br.readLine();
            Integer docNums = Integer.parseInt(line);
            while ((line = br.readLine()) != null) {
                lineNum++;
                if(lineNum == 1){
                    word = line;
                }else if(lineNum == 2){
                    nums = Integer.parseInt(line);
                    nbcPositiveMap.put(word,nums);
                    lineNum = 0l;
                }
            }
            NbcWordsMap nbcWordsMap = new NbcWordsMap();
            nbcWordsMap.setCategoryId(categoryId);
            nbcWordsMap.setNbcPositiveMap(nbcPositiveMap);
            nbcWordsMap.setCategoryDocNum(docNums);
            allCategoryWordList.add(nbcWordsMap);
        }catch(Exception err){
            logger.error("加载分类正文本词-词频异常，categoryId="+categoryId);
        }

    }



    //针对每个分类每个词的信息增益值排序
    public static void sortInfoGain(){
        for(int i = 0;i < igCategoryModelList.size();i++){
            IgCategoryModel igCategoryModel = igCategoryModelList.get(i);
            List<IgWordModel> igWordModelList = igCategoryModel.getIgWordModelList();
            Collections.sort(igWordModelList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return ((IgWordModel) o2).getInfoGain().compareTo(((IgWordModel) o1).getInfoGain());
                }
            });
        }

    }

    //计算每个词的正负文本出现频率和不出现频率
    public static void genWordIgSvm(){
        for(int i = 0;i < igCategoryModelList.size();i++){
            IgCategoryModel igCategoryModel = igCategoryModelList.get(i);

            Map<String,Integer> negMap = new HashMap<>();
            Integer N2 = 0;
            for(int j = 0; j < igCategoryModelList.size();j++){
                IgCategoryModel igCategoryModel1 = igCategoryModelList.get(j);
                if(igCategoryModel.getCategoryId() == igCategoryModel1.getCategoryId()){
                    continue;
                }
                N2 += igCategoryModel1.getN1();
                Map<String,Integer> posMap = igCategoryModel1.getWordDocNumMap();
                for(Iterator iterator = posMap.entrySet().iterator(); iterator.hasNext();){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String word = (String) entry.getKey();
                    if(negMap.containsKey(word)){
                        negMap.put(word,negMap.get(word)+posMap.get(word));
                    }else{
                        negMap.put(word,posMap.get(word));
                    }
                }
            }
            igCategoryModel.setN2(N2);

            Map<String,Integer> posWordMap = igCategoryModel.getWordDocNumMap();
            //计算正文本分类每个词的向量
            for(Iterator iterator = posWordMap.entrySet().iterator(); iterator.hasNext();){
                Map.Entry entry = (Map.Entry) iterator.next();
                String word = (String) entry.getKey();
                IgWordModel igWordModel = new IgWordModel();
                igWordModel.setWord(word);
                igWordModel.setA(posWordMap.get(word));

                igWordModel.setB(negMap.get(word));

                igWordModel.setC(igCategoryModel.getN1()-igWordModel.getA());

                igWordModel.setD(igCategoryModel.getN2() - negMap.getOrDefault(word,0));
                igWordModel.setN1(igCategoryModel.getN1());
                igWordModel.setN2(igCategoryModel.getN2());
                igCategoryModel.addWordIg(igWordModel);

            }

        }
    }

    //计算每个词的信息增益
    public static void calWordInfoGain(){
        for(int i = 0;i < igCategoryModelList.size();i++){
            IgCategoryModel igCategoryModel = igCategoryModelList.get(i);
            List<IgWordModel> v = igCategoryModel.getIgWordModelList();
            for(IgWordModel igWordModel:v){
                igWordModel.genInfoGain();
            }
        }
    }

    //统计每个分类词出现文档次数
    public static void statisticalCategoryWordDocNums(int i) throws Exception{
        String categoryFile = SOUGO_NEWS_FILE_PATH + CommonParams.CATEGORY_NEWS_FILE_PRIFIX+i+".dat";

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(categoryFile), "UTF-8"));
        String line;
        Map<String,Integer> wordDocNums = new HashMap<>();
        while ((line = br.readLine()) != null) {
            Map<String,Integer> wordsMap =  choutiSegment.segmentFrequency(line,ChoutiSegment.SEGMENT_TYPE_NBC);
            if(null == wordsMap){
                continue;
            }
            for (Iterator iterator = wordsMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String word = (String) entry.getKey();
                if(wordDocNums.containsKey(word)){
                    wordDocNums.put(word,wordDocNums.get(word)+1);
                }else{
                    wordDocNums.put(word,1);
                }
            }
            N1 ++;
        }
        IgCategoryModel igCategoryModel = new IgCategoryModel();
        igCategoryModel.setCategoryId(i);
        igCategoryModel.setN1(N1);
        igCategoryModel.setWordDocNumMap(wordDocNums);
        igCategoryModelList.add(igCategoryModel);
        br.close();

    }
}
