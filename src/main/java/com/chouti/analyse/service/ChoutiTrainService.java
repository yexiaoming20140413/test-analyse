package com.chouti.analyse.service;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.model.NbcWordsMap;
import com.chouti.analyse.model.News;
import com.chouti.analyse.model.NewsCategory;
import com.chouti.analyse.mybatis.mapper.NewsMapper;
import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.train.IgCategoryModel;
import com.chouti.analyse.train.IgWordModel;
import com.chouti.analyse.train.TrainSouhuNews;
import com.chouti.analyse.train.WordFrequece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-23.
 *******************************************************************************/
@Service
public class ChoutiTrainService {

    private static Logger logger = LoggerFactory.getLogger(TrainSouhuNews.class);

    @Value("${cagetory.words.fileDir.chouti}")
    private String CHOUTI_NEWS_FILE_PATH;

    @Value("${cagetory.words.fileDir.souhu}")
    private String SOUHU_NEWS_FILE_PATH;

    private static ChoutiSegment choutiSegment = new ChoutiSegment();

    @Autowired
    private NewsMapper newsMapper;




    public void loadCategoryNews() throws Exception{
        logger.info("开始加载训练语料库");
        long startTime = System.currentTimeMillis();
        List<NewsCategory> newsCategoryList = newsMapper.queryCategoryList();
        if(!CollectionUtils.isEmpty(newsCategoryList)){
            for(int i = 0;i < newsCategoryList.size();i++){
                NewsCategory category = newsCategoryList.get(i);
                loadTextToFile(category.getId());
            }

        }
        logger.info("加载训练预料库结束:"+(System.currentTimeMillis()-startTime)+"ms");
    }

    public void staticisCategoryWords() throws Exception{
        logger.info("开始训练分类词词频");
        long startTime = System.currentTimeMillis();
        List<NewsCategory> newsCategoryList = newsMapper.queryCategoryList();
        if(!CollectionUtils.isEmpty(newsCategoryList)){
            for(int i = 0;i < newsCategoryList.size();i++){
                NewsCategory category = newsCategoryList.get(i);
                segmentCagoryNews(category.getId());
            }
        }
        logger.info("开始训练分类词词频结束:"+(System.currentTimeMillis()-startTime)+"ms");
    }

    public void loadTextToFile(Integer categoryId) throws Exception{
        if(categoryId == null || categoryId <= 0){
            return;
        }
        String weibosTagSegLdaPath = CHOUTI_NEWS_FILE_PATH + File.separator +"category_"+categoryId + ".dat";
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weibosTagSegLdaPath), "UTF-8"));
        boolean loop = true;
        long id = Long.MAX_VALUE;
        Integer pageSize = CommonParams.LOAD_NO_HASH_NEWS_DB_PAGE_SIZE;
        int index = 0;
        while(loop){
            List<News> trainNewsList = newsMapper.loadTrainFile(id,categoryId,pageSize);
            if (CollectionUtils.isEmpty(trainNewsList) || trainNewsList.size() < pageSize) {
                loop = false;
            }
            for(News news:trainNewsList){

                index ++;
                if(index > CommonParams.TRAIN_NEWS_SIZE){
                    loop = false;
                    break;
                }
                id = news.getId();
                String title = news.getTitle();
                title = replaceBlank(title);
                String content = news.getContent();
                content = replaceBlank(content);
                bw.write(title+" "+content);
                bw.newLine();
            }
        }
        bw.close();
        System.out.println("categoryId="+categoryId+",新闻总数:"+index);
    }

    public  String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public void segmentCagoryNews(int i) throws Exception{
        String categoryFile = CHOUTI_NEWS_FILE_PATH + CommonParams.CATEGORY_NEWS_FILE_PRIFIX+i+".dat";
        String categoryWordFreFile = CHOUTI_NEWS_FILE_PATH + CommonParams.CATEGORY_WORD_FEATURE_NBC_PRIFIX+i+".dat";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(categoryFile), "UTF-8"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryWordFreFile), "UTF-8"));
        String line;
        long lineNum = 0;
        Map<String,Integer> wordNumsMap = new HashMap<>();
        List<WordFrequece> wordFrequeces = new ArrayList<>();
        String leibie = "";

        while ((line = br.readLine()) != null) {
            lineNum++;
            Map<String,Integer> wordDocsMap = choutiSegment.segmentFrequency(line);
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

    private static List<NbcWordsMap> allCategoryWordList = new ArrayList<>();


    private static Integer N1=0;

    private static List<IgCategoryModel> igCategoryModelList = new ArrayList<>();

    private static Integer CATEGORY_FEATURE_WORD_LEN=2000;

    public void genCategoryIgWords() throws Exception{
        logger.info("IG分类特征词选取开始");
        long startTime = System.currentTimeMillis();

        List<NewsCategory> newsCategoryList = newsMapper.queryCategoryList();
        if(!CollectionUtils.isEmpty(newsCategoryList)){
            for(int i = 0;i < newsCategoryList.size();i++){
                NewsCategory category = newsCategoryList.get(i);
                loadCategoryNbcPositiveWords(category.getId());
                logger.info("IG分类特征词-step1:统计每个分类词出现文档次数");
                statisticalCategoryWordDocNums(category.getId());
            }

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


    public void saveCagegoryIgWords() throws Exception{

        for(int i = 0;i < igCategoryModelList.size();i++){
            double d = 0.0;
            IgCategoryModel igCategoryModel = igCategoryModelList.get(i);
            List<IgWordModel> igWordModelList = igCategoryModel.getIgWordModelList();
            String categoryWordFreFile = CHOUTI_NEWS_FILE_PATH + CommonParams.CATEGORY_WORD_FEATURE_IG_PRIFIX+igCategoryModel.getCategoryId()+".dat";
//            String categoryWordWeightFile = SOUGO_NEWS_FILE_PATH + CommonParams.CATEGORY_WORD_FEATURE_IG_WEIGHT_PRIFIX+igCategoryModel.getCategoryId()+".dat";
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryWordFreFile), "UTF-8"));
//            BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryWordWeightFile), "UTF-8"));
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
                int maxWordNum = 0;
                double maxWeight = 0.0;
                for(IgWordModel igWordModel:igWordModelList){
                    index ++;
                    if(index > CommonParams.CATEGORY_FEATURE_WORD_LEN){
                        break;
                    }

                    String word = igWordModel.getWord();
                    Integer num = wordsMap.get(word);
                    if(num == null){
                        continue;
                    }
                    if(maxWordNum == 0){
                        maxWordNum = num;
                        maxWeight = igWordModel.getInfoGain();
                    }
                    Double weightNum = (igWordModel.getInfoGain()/maxWeight)*maxWordNum;

//                    bw1.write(word);
//                    bw1.newLine();
//                    bw1.write(weightNum.intValue()+" "+igWordModel.getInfoGain()+"");
//                    bw1.newLine();

                    bw.write(word);
                    bw.newLine();

//                    bw.write(num+"");
                    bw.write(weightNum.intValue()+"");
                    bw.newLine();
                }
            }
            bw.close();
//            bw1.close();
        }
    }



    /**
     * 加载所有分类正文本词-词频
     * @param categoryId
     */
    public void loadCategoryNbcPositiveWords(Integer categoryId){
        try{
            Map<String,Integer> nbcPositiveMap = new HashMap<>();
            String categoryWordFreFile = CHOUTI_NEWS_FILE_PATH + CommonParams.CATEGORY_WORD_FEATURE_NBC_PRIFIX+categoryId+".dat";
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
            if(CollectionUtils.isEmpty(igWordModelList)){
                continue;
            }
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
            if(null == v){
                continue;
            }
            for(IgWordModel igWordModel:v){
                igWordModel.genInfoGain();
            }
        }
    }

    //统计每个分类词出现文档次数
    public void statisticalCategoryWordDocNums(int i) throws Exception{
        String categoryFile = CHOUTI_NEWS_FILE_PATH + CommonParams.CATEGORY_NEWS_FILE_PRIFIX+i+".dat";

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(categoryFile), "UTF-8"));
        String line;
        Map<String,Integer> wordDocNums = new HashMap<>();
        N1 = 0;
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


    public void loadSouhuNewsToDb() throws Exception{
//        List<NewsCategory> newsCategoryList = newsMapper.queryCategoryList();
//        if(!CollectionUtils.isEmpty(newsCategoryList)){
//            for(int i = 0;i < newsCategoryList.size();i++){
//                NewsCategory category = newsCategoryList.get(i);
//                loadSouhuCategoryNewsToDb(category.getId());
//
//            }
//
//        }

    }

    public void loadSouhuCategoryNewsToDb(Integer categoryId) throws Exception{
        String categoryFile = SOUHU_NEWS_FILE_PATH + CommonParams.CATEGORY_NEWS_FILE_PRIFIX+categoryId+".dat";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(categoryFile), "UTF-8"));
        String line;
        while((line = br.readLine()) != null){
            News news = new News();
            news.setContent(line);
            news.setCategoryId(categoryId);
            news.setCreateTime(System.currentTimeMillis());
            newsMapper.insertTrainNews(news);
        }
        br.close();
    }
}
