package com.chouti.analyse.configure;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-9.
 *******************************************************************************/
public class CommonParams {


    public static boolean DEAL_TFIDF = false;

    /**
     * simhash 指纹长度
     */
    public static int SIM_HASH_CODE_LEN=64;

    /**
     *  加载N天news hashSet
     */
    public static int LOAD_SIM_HASH_SET_DATE=7;

    /**
     * 文本相似海明最佳距离
     */
    public static int MIN_HM_DISTACE_LEN=5;
    /**
     * 初始化simhash map 数据库分页大小
     */
    public static int LOAD_SIMEHASH_DB_PAGE_SIZE=1000;
    /**
     * 定时从数据库中加载未计算hashnews 分页大小
     */
    public static int LOAD_NO_HASH_NEWS_DB_PAGE_SIZE=50;

    /**
     * 训练分类文本数量
     */
    public static int TRAIN_NEWS_SIZE =2000;
    /**
     * 每个分类特征词数量
     */
    public static int CATEGORY_FEATURE_WORD_LEN = 500;

    /**
     * 计算tfidf 词权单词数量
     */
    public static int TF_IDF_WORD_LEN=20;

    public static String CATEGORY_NEWS_FILE_PRIFIX="category_";

    public static String CATEGORY_WORD_FEATURE_NBC_PRIFIX="category_words_nbc_";

    public static String CATEGORY_WORD_FEATURE_IG_PRIFIX="category_words_ig_";

    public static String CATEGORY_WORD_FEATURE_IG_WEIGHT_PRIFIX="category_words_ig_weight_";

    /**
     * 相同源的重复新闻
     */
    public static Integer REPEAT_NEWS_SAMESOURCE=1;
    /**
     * 不同源的重复新闻
     */
    public static Integer REPEAT_NEWS=2;

    /**
     * 计算新闻热度分值时间因子刻度
     */
    public static Double NEWS_SCORE_TIME_RANGE= 3600d* 1 * 1000;
    /**
     * 计算新闻热度推荐数因子刻度
     */
    public static Double NEWS_SCORE_VOTE_RANGE=3d;





}
