package com.chouti.analyse.utils;

import java.util.Iterator;
import java.util.Map;


/**
 * 实现描述：朴素贝叶斯分类
 *
 * @author jin.xu
 * @version v1.0.0
 *          1.先验概率P(y)= 类y下单词总数/整个训练样本的单词总数
 *          2.类条件概率P(xi|y)=(类y下单词xi在各个文档中出现过的次数之和+1)/(类y下单词总数+向量的维度)
 *          3.P(y|xi)=P(y)*P(xi|y)/P(xi)等价于P(y)*P(xi|y)
 * @see
 * @since 16-9-13 下午5:44
 */
public class NBClassifier {


    //正文本文本特征词,词频
    private Map<String, Integer> nbcNegativeMap;
    //负文本文本特征词,词频
    private Map<String, Integer> nbcPositiveMap;

    private double nbcNegativePrioriProb;
    private double nbcPositivePrioriProb;

    //正文本特征词总频率
    private Integer nbcNegativeWordCount;
    //负文本特征词总频率
    private Integer nbcPositeiveWordCount;


    public NBClassifier(Map<String, Integer> nbcPositiveMap, Integer nbcPositeiveWordCount, Map<String, Integer> nbcNegativeMap, Integer nbcNegativeWordCount) {


        this.nbcPositiveMap = nbcPositiveMap;
        this.nbcNegativeMap = nbcNegativeMap;

//        nbcPositivePrioriProb = 1.0 * nbcPositiveMap.size() / (nbcPositiveMap.size() + nbcNegativeMap.size());
//        nbcNegativePrioriProb = 1.0 * nbcNegativeMap.size() / (nbcNegativeMap.size() + nbcPositiveMap.size());

        nbcPositivePrioriProb = Math.log(1.0 * nbcPositiveMap.size() / (nbcPositiveMap.size() + nbcNegativeMap.size()));
        nbcNegativePrioriProb = Math.log(1.0 * nbcNegativeMap.size() / (nbcNegativeMap.size() + nbcPositiveMap.size()));

        this.nbcPositeiveWordCount = nbcPositeiveWordCount;
        this.nbcNegativeWordCount = nbcNegativeWordCount;
    }


    /**
     * 增量训练
     *
     * @param docMap 　待加入训练集的数据
     * @param isbYes 　对应标签
     */
    public void train(Map<String, Integer> docMap, boolean isbYes) {
        if (isbYes) {
            this.nbcNegativeMap = mergeDocMap(this.nbcNegativeMap, docMap);
        } else {
            this.nbcPositiveMap = mergeDocMap(this.nbcPositiveMap, docMap);
        }

        nbcNegativePrioriProb = 1.0 * nbcNegativeMap.size() / (nbcNegativeMap.size() + nbcPositiveMap.size());
        nbcPositivePrioriProb = 1.0 * nbcPositiveMap.size() / (nbcNegativeMap.size() + nbcPositiveMap.size());

    }


    /**
     * 分类文档
     *
     * @param wordFreMap 　待分类文档对应的单词－频数映射
     * @return
     */
    public double[] classify(Map<String, Integer> wordFreMap) {
        double[][] conditionalProb = new double[2][wordFreMap.size()];
        conditionalProb[0] = getProbMatrix(nbcPositiveMap, nbcPositeiveWordCount, wordFreMap,true);
        conditionalProb[1] = getProbMatrix(nbcNegativeMap, nbcNegativeWordCount, wordFreMap,false);
        double[] classProb = {nbcPositivePrioriProb, nbcNegativePrioriProb};
        for (int i = 0; i < classProb.length; ++i) {
            for (int j = 0; j < wordFreMap.size(); ++j) {
                classProb[i] += java.lang.Math.log(conditionalProb[i][j]);
            }
        }
        return classProb;
    }

    /**
     * 概率归一化
     *
     * @param classProb 　原概率列表
     * @return
     */
    public double[] normalized(double[] classProb) {
        double[] classProbAfterNor = new double[classProb.length];
        double sum = 0.0;
        for (int i = 0; i < classProb.length; ++i) {
            sum += classProb[i];
        }
        for (int i = 0; i < classProb.length; ++i) {
            classProbAfterNor[i] = classProb[i] / sum;
        }
        return classProbAfterNor;
    }

    /**
     * 计算给定分类属性取值下每个特征属性不同取值的条件概率矩阵
     *
     * @param docMap         　给定分类属性取值对应的单词－频数映射
     * @param classWordCount 给定分类属性取值的单词总频数
     * @param wordFreMap     　待分类文档对应的单词－频数映射
     * @return
     */
    private double[] getProbMatrix(Map<String, Integer> docMap, Integer classWordCount, Map<String, Integer> wordFreMap, boolean positive) {
        double[] probMatrixPerClass = new double[wordFreMap.size()];
        int index = 0;
        for (Iterator iterator = wordFreMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            double tmpCount = 0.0;
            Double weight = 1.0;
            if (positive) {
                System.out.println("word:" + key + "-nbcPositiveMap:" + nbcPositiveMap.get(key) + "-nbcNegativeMap:" + nbcNegativeMap.get(key) + "nbcPositiveMapCount:" + nbcPositeiveWordCount + "-nbcNegativeWordCount:" + nbcNegativeWordCount);

            }
            if (docMap.containsKey(key)) {
                tmpCount = (double) docMap.get(key);
            }

            probMatrixPerClass[index++] = 1.0 * (tmpCount + 0.0001) * weight / (classWordCount + 0.0002);
        }
        return probMatrixPerClass;
    }

    /**
     * 计算docMap中所有单词频数和
     *
     * @param docMap 　单词－频数映射
     * @return
     */
    private Long getWordCount(Map<String, Integer> docMap) {
        Long totalFrequency = 0l;
        for (Iterator iterator = docMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            totalFrequency += (Integer) entry.getValue();
        }
        return totalFrequency;
    }

    /**
     * 将文档合并到训练文档集
     *
     * @param allDocMap 　训练文档集
     * @param docMap    　待增量添加文档
     * @return
     */
    private Map<String, Integer> mergeDocMap(Map<String, Integer> allDocMap, Map<String, Integer> docMap) {
        for (Iterator iterator = docMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            Integer value = (Integer) entry.getValue();
            if (allDocMap.containsKey(key)) {
                allDocMap.put(key, allDocMap.get(key) + value);
            } else {
                allDocMap.put(key, value);
            }
        }
        return allDocMap;
    }


    public static void main(String args[]) {

//        word:娱乐-nbcPositiveMap:null-nbcNegativeMap:nullnbcPositiveMapCount:126291-nbcNegativeWordCount:474579
//        word:冤家-nbcPositiveMap:null-nbcNegativeMap:6nbcPositiveMapCount:126291-nbcNegativeWordCount:474579
//        word:底片-nbcPositiveMap:null-nbcNegativeMap:1nbcPositiveMapCount:126291-nbcNegativeWordCount:474579
//        word:明星-nbcPositiveMap:null-nbcNegativeMap:nullnbcPositiveMapCount:126291-nbcNegativeWordCount:474579
//        word:同台-nbcPositiveMap:null-nbcNegativeMap:nullnbcPositiveMapCount:126291-nbcNegativeWordCount:474579

        double a = 1.0 / 126291;
        double b = 1.0 / 474579;
        double c = 7.0 / 474579;
        double d = 2.0 / 474579;
        double d1 = a * a * a * a * a;
        double d2 = b * c * d * b * b;
        if (d1 > d2) {
            System.out.println("11111111111111");
        }
        double d3 = Math.log(a) + Math.log(a) + Math.log(a) + Math.log(a) + Math.log(a);
        double d4 = Math.log(b) + Math.log(c) + Math.log(d) + Math.log(b) + Math.log(b);
        if (d3 > d4) {
            System.out.println("22222222222222");
        }
    }
}
