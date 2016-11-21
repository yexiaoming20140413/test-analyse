package com.chouti.analyse.segment;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-10-27.
 *******************************************************************************/
public class ChoutiSegment {


//    private Segment segment = HanLP.newSegment().enableCustomDictionary(true).enableAllNamedEntityRecognize(false).enableNameRecognize(true).enableTranslatedNameRecognize(true)
//            .enableNumberQuantifierRecognize(true);

    private Segment segment = HanLP.newSegment().enableCustomDictionary(true).enableOrganizationRecognize(true).enablePlaceRecognize(true).enableTranslatedNameRecognize(true).enableJapaneseNameRecognize(true).enablePartOfSpeechTagging(true);

//    private Segment crfSegment = new CRFSegment().enableCustomDictionary(true).enableAllNamedEntityRecognize(true);

    private Segment nshortSegment = new NShortSegment().enableCustomDictionary(true).enableAllNamedEntityRecognize(true);


//    private Map<String, Integer> frequencyMap = new HashMap<>();

    private static int MIN_SEG_WORD_LEN = 3;


    public static String SEGMENT_TYPE_NBC="NBC";


    public ChoutiSegment() {

    }

    public static Set<String> holdPosSet = new HashSet<String>();
    public static Set<String> classifierHoldPosSet = new HashSet<String>();
    public static Map<String, String> cixingMap = new HashMap<>();

    static {

        holdPosSet.add("n");      //名词
        holdPosSet.add("nr");     //人名
        holdPosSet.add("ns");     //地名
        holdPosSet.add("nt");     //机构团体名
        holdPosSet.add("nz");     //其他专名
        holdPosSet.add("vd");     //副动词
        holdPosSet.add("vn");     //动名词
        holdPosSet.add("an");     //名形词
        holdPosSet.add("nis");    //机构后缀
        holdPosSet.add("nrf");    //nrf 音译人名
        holdPosSet.add("v");      //动词
        holdPosSet.add("g");      //学术词汇
        holdPosSet.add("nnt");      //职务职称
        holdPosSet.add("gb");    //生物相关词汇
        holdPosSet.add("gbc");    //生物类别
        holdPosSet.add("gc");    //化学相关词汇
        holdPosSet.add("gg");    //地理地质相关词汇
        holdPosSet.add("gi");    //数学相关词汇
        holdPosSet.add("gm");    //化学相关词汇
        holdPosSet.add("gp");    //物理相关词汇
        holdPosSet.add("nb");    //生物名
        holdPosSet.add("nba");    //动物名
        holdPosSet.add("nbc");    //动物纲目
        holdPosSet.add("nbp");    //植物名
        holdPosSet.add("nf");    //食品
        holdPosSet.add("nh");    //医药疾病等健康相关名词
        holdPosSet.add("nhd");    //疾病
        holdPosSet.add("nhm");    //药品
        holdPosSet.add("ni");    //机构相关
        holdPosSet.add("nic");    //下属机构
        holdPosSet.add("nis");    //机构后缀
        holdPosSet.add("nit");    //教育相关机构
        holdPosSet.add("nm");    //物品名
        holdPosSet.add("nmc");    //化学品名
        holdPosSet.add("nn");    //工作相关名词
        holdPosSet.add("nnd");    //职业
        holdPosSet.add("nnt");    //职务职称
        holdPosSet.add("ntch");    //酒店宾馆
        holdPosSet.add("nth");    //医院
        holdPosSet.add("nto");    //政府机构
        holdPosSet.add("nts");    //中小学
        holdPosSet.add("ntu");    //大学


//        classifierHoldPosSet.add("v");      //动词
        classifierHoldPosSet.add("n");      //名词
        classifierHoldPosSet.add("nr");     //人名
        classifierHoldPosSet.add("ns");     //地名
        classifierHoldPosSet.add("nt");     //机构团体名
        classifierHoldPosSet.add("nz");     //其他专名
        classifierHoldPosSet.add("nis");    //机构后缀
        classifierHoldPosSet.add("nrf");    //nrf 音译人名
        classifierHoldPosSet.add("g");      //学术词汇
        classifierHoldPosSet.add("nnt");      //职务职称
        classifierHoldPosSet.add("gb");    //生物相关词汇
        classifierHoldPosSet.add("gbc");    //生物类别
        classifierHoldPosSet.add("gc");    //化学相关词汇
        classifierHoldPosSet.add("gg");    //地理地质相关词汇
        classifierHoldPosSet.add("gi");    //数学相关词汇
        classifierHoldPosSet.add("gm");    //化学相关词汇
        classifierHoldPosSet.add("gp");    //物理相关词汇
        classifierHoldPosSet.add("nb");    //生物名
        classifierHoldPosSet.add("nba");    //动物名
        classifierHoldPosSet.add("nbc");    //动物纲目
        classifierHoldPosSet.add("nbp");    //植物名
        classifierHoldPosSet.add("nf");    //食品
        classifierHoldPosSet.add("nh");    //医药疾病等健康相关名词
        classifierHoldPosSet.add("nhd");    //疾病
        classifierHoldPosSet.add("nhm");    //药品
        classifierHoldPosSet.add("ni");    //机构相关
        classifierHoldPosSet.add("nic");    //下属机构
        classifierHoldPosSet.add("nis");    //机构后缀
        classifierHoldPosSet.add("nit");    //教育相关机构
        classifierHoldPosSet.add("nm");    //物品名
        classifierHoldPosSet.add("nmc");    //化学品名
        classifierHoldPosSet.add("nn");    //工作相关名词
        classifierHoldPosSet.add("nnd");    //职业
        classifierHoldPosSet.add("nnt");    //职务职称
        classifierHoldPosSet.add("ntch");    //酒店宾馆
        classifierHoldPosSet.add("nth");    //医院
        classifierHoldPosSet.add("nto");    //政府机构
        classifierHoldPosSet.add("nts");    //中小学
        classifierHoldPosSet.add("ntu");    //大学



//        holdPosSet.add("n");      //名词
//        holdPosSet.add("nr");     //人名
//        holdPosSet.add("ns");     //地名
//        holdPosSet.add("nt");     //机构团体名
//        holdPosSet.add("nz");     //其他专名
//        holdPosSet.add("vd");     //副动词
//        holdPosSet.add("vn");     //动名词
//        holdPosSet.add("an");     //名形词
//        holdPosSet.add("nis");    //机构后缀
//        holdPosSet.add("nrf");    //nrf 音译人名
//        holdPosSet.add("v");      //动词
//        holdPosSet.add("g");      //学术词汇
//        holdPosSet.add("nnt");    //职务职称
//        holdPosSet.add("g");    //学术词汇
//        holdPosSet.add("gb");    //生物相关词汇
//        holdPosSet.add("gbc");    //生物类别
//        holdPosSet.add("gc");    //化学相关词汇
//        holdPosSet.add("gg");    //地理地质相关词汇
//        holdPosSet.add("gi");    //数学相关词汇
//        holdPosSet.add("gm");    //化学相关词汇
//        holdPosSet.add("gp");    //物理相关词汇
//        holdPosSet.add("nb");    //生物名
//        holdPosSet.add("nba");    //动物名
//        holdPosSet.add("nbc");    //动物纲目
//        holdPosSet.add("nbp");    //植物名
//        holdPosSet.add("nf");    //食品
//        holdPosSet.add("nh");    //医药疾病等健康相关名词
//        holdPosSet.add("nhd");    //疾病
//        holdPosSet.add("nhm");    //药品
//        holdPosSet.add("ni");    //机构相关
//        holdPosSet.add("nic");    //下属机构
//        holdPosSet.add("nis");    //机构后缀
//        holdPosSet.add("nit");    //教育相关机构
//        holdPosSet.add("nm");    //物品名
//        holdPosSet.add("nmc");    //化学品名
//        holdPosSet.add("nn");    //工作相关名词
//        holdPosSet.add("nnd");    //职业
//        holdPosSet.add("nnt");    //职务职称
//
//
//        holdPosSet.add("nr1");    //复姓
//        holdPosSet.add("nr2");    //蒙古姓名
//        holdPosSet.add("nrf");    //音译人名
//        holdPosSet.add("nrj");    //日语人名
//        holdPosSet.add("ns");    //地名
//
//        holdPosSet.add("nsf");    //音译地名
//        holdPosSet.add("nt");    //机构团体名
//        holdPosSet.add("ntc");    //公司名
//        holdPosSet.add("ntcb");    //银行
//        holdPosSet.add("ntcf");    //工厂
//
//        holdPosSet.add("ntch");    //酒店宾馆
//        holdPosSet.add("nth");    //医院
//        holdPosSet.add("nto");    //政府机构
//        holdPosSet.add("nts");    //中小学
//        holdPosSet.add("ntu");    //大学


        cixingMap.put("a", "形容词");
        cixingMap.put("ad", "副形词");
        cixingMap.put("ag", "形容词性语素");
        cixingMap.put("al", "形容词惯用语");
        cixingMap.put("an", "名形词");
        cixingMap.put("b", "区别词");
        cixingMap.put("begin", "仅用于始##始");
        cixingMap.put("bg", "区别语素");
        cixingMap.put("bl", "区别词性惯用语");
        cixingMap.put("c", "连词");
        cixingMap.put("cc", "并列连词");
        cixingMap.put("d", "副词");
        cixingMap.put("dg", "辄,俱,复之类的副词");
        cixingMap.put("dl", "连语");
        cixingMap.put("e", "叹词");
        cixingMap.put("end", "仅用于终##终");
        cixingMap.put("f", "方位词");
        cixingMap.put("g", "学术词汇");
        cixingMap.put("gb", "生物相关词汇");
        cixingMap.put("gbc", "生物类别");
        cixingMap.put("gc", "化学相关词汇");
        cixingMap.put("gg", "地理地质相关词汇");
        cixingMap.put("gi", "数学相关词汇");
        cixingMap.put("gm", "化学相关词汇");
        cixingMap.put("gp", "物理相关词汇");
        cixingMap.put("h", "前缀");
        cixingMap.put("i", "成语");
        cixingMap.put("j", "简称略语");
        cixingMap.put("k", "后缀");
        cixingMap.put("l", "习用语");
        cixingMap.put("m", "数词");
        cixingMap.put("mg", "数语素");
        cixingMap.put("Mg", "甲乙丙丁之类的数词");
        cixingMap.put("mq", "数量词");
        cixingMap.put("n", "名词");
        cixingMap.put("nb", "生物名");
        cixingMap.put("nba", "动物名");
        cixingMap.put("nbc", "动物纲目");
        cixingMap.put("nbp", "植物名");
        cixingMap.put("nf", "食品");
        cixingMap.put("ng", "名词性语素");
        cixingMap.put("nh", "医药疾病等健康相关名词");
        cixingMap.put("nhd", "疾病");
        cixingMap.put("nhm", "药品");
        cixingMap.put("ni", "机构相关");
        cixingMap.put("nic", "下属机构");
        cixingMap.put("nis", "机构后缀");
        cixingMap.put("nit", "教育相关机构");
        cixingMap.put("nl", "名词性惯用语");
        cixingMap.put("nm", "物品名");

        cixingMap.put("nmc", "化学品名");
        cixingMap.put("nn", "工作相关名词");
        cixingMap.put("nnd", "职业");
        cixingMap.put("nnt", "职务职称");
        cixingMap.put("nr", "人名");


        cixingMap.put("nr1", "复姓");
        cixingMap.put("nr2", "蒙古姓名");
        cixingMap.put("nrf", "音译人名");
        cixingMap.put("nrj", "日语人名");
        cixingMap.put("ns", "地名");

        cixingMap.put("nsf", "音译地名");
        cixingMap.put("nt", "机构团体名");
        cixingMap.put("ntc", "公司名");
        cixingMap.put("ntcb", "银行");
        cixingMap.put("ntcf", "工厂");

        cixingMap.put("ntch", "酒店宾馆");
        cixingMap.put("nth", "医院");
        cixingMap.put("nto", "政府机构");
        cixingMap.put("nts", "中小学");
        cixingMap.put("ntu", "大学");


        cixingMap.put("nx", "字母专名");
        cixingMap.put("nz", "其他专名");
        cixingMap.put("o", "拟声词");
        cixingMap.put("p", "介词");
        cixingMap.put("pba", "介词“把”");

        cixingMap.put("pbei", "介词“被”");
        cixingMap.put("q", "量词");
        cixingMap.put("qg", "量词语素");
        cixingMap.put("qt", "时量词");
        cixingMap.put("qv", "动量词");

        cixingMap.put("r", "代词");
        cixingMap.put("rg", "代词性语素");
        cixingMap.put("Rg", "古汉语代词性语素");
        cixingMap.put("rr", "人称代词");
        cixingMap.put("ry", "疑问代词");

        cixingMap.put("rys", "处所疑问代词");
        cixingMap.put("ryt", "时间疑问代词");
        cixingMap.put("ryv", "谓词性疑问代词");
        cixingMap.put("rz", "指示代词");
        cixingMap.put("rzs", "处所指示代词");

        cixingMap.put("rzt", "时间指示代词");
        cixingMap.put("rzv", "谓词性指示代词");
        cixingMap.put("s", "处所词");
        cixingMap.put("t", "时间词");
        cixingMap.put("tg", "时间词性语素");

        cixingMap.put("u", "助词");
        cixingMap.put("ud", "助词");
        cixingMap.put("ude1", "的 底");
        cixingMap.put("ude2", "地");
        cixingMap.put("ude3", "得");

        cixingMap.put("udeng", "等 等等 云云");
        cixingMap.put("udh", "的话");
        cixingMap.put("ug", "过");
        cixingMap.put("uguo", "过");
        cixingMap.put("uj", "助词");

        cixingMap.put("ul", "连词");
        cixingMap.put("ule", "了 喽");
        cixingMap.put("ulian", "连 （“连小学生都会”）");
        cixingMap.put("uls", "来讲 来说 而言 说来");
        cixingMap.put("usuo", "所");

        cixingMap.put("uv", "连词");
        cixingMap.put("uyy", "一样 一般 似的 般");
        cixingMap.put("uz", "着");
        cixingMap.put("uzhe", "着");
        cixingMap.put("uzhi", "之");


        cixingMap.put("v", "动词");
        cixingMap.put("vd", "副动词");
        cixingMap.put("vf", "趋向动词");
        cixingMap.put("vg", "动词性语素");
        cixingMap.put("vi", "不及物动词（内动词）");

        cixingMap.put("vl", "动词性惯用语");
        cixingMap.put("vn", "动名词");
        cixingMap.put("vshi", "动词“是”");
        cixingMap.put("vx", "形式动词");
        cixingMap.put("vyou", "动词“有”");

        cixingMap.put("w", "标点符号");
        cixingMap.put("wb", "百分号千分号");
        cixingMap.put("wd", "逗号");
        cixingMap.put("wf", "分号");
        cixingMap.put("wh", "单位符号，全角：￥ ＄ ￡  °  ℃  半角：$");

        cixingMap.put("wj", "句号");
        cixingMap.put("wky", "右括号");
        cixingMap.put("wkz", "左括号");
        cixingMap.put("wn", "顿号");
        cixingMap.put("wp", "破折号");

        cixingMap.put("ws", "省略号");
        cixingMap.put("wt", "叹号");
        cixingMap.put("ww", "问号");
        cixingMap.put("wyy", "右引号");
        cixingMap.put("wyz", "左引号");

        cixingMap.put("x", "字符串");
        cixingMap.put("xu", "网址URL");
        cixingMap.put("xx", "非语素字");
        cixingMap.put("y", "语气词");
        cixingMap.put("yg", "语气语素");

        cixingMap.put("z", "状态词");
        cixingMap.put("zg", "状态词");
    }

    public List<ChoutiTerm> segment(String text) {
        text = text.replaceAll("@\\S+ ", "");
        text = text.replaceAll("@[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]{2,30}", " ");//去@某某
        text = text.replaceAll("((http|ftp|https):\\/\\/){0,1}[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?", "");//去网址
        text = text.replaceAll("]", "");
        Pattern p = Pattern.compile("#([^#]+)#");
        Matcher m = p.matcher(text);
        Set<String> topics = new HashSet<String>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                String str = m.group(i);
                topics.add(str);
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);

        List<Term> terms = segment.seg(sb.toString());
        if (CollectionUtils.isEmpty(terms)) {
            return null;
        }
        List<ChoutiTerm> resultTerm = new ArrayList<>();
        CoreStopWordDictionary.apply(terms);
        for (Term term : terms) {
            String pos = term.nature.name();
            String word = term.word.toString();
            if (!holdPosSet.contains(pos)) {
                continue;
            }
            word.replaceAll(" ","");
            if (word.length() == 1) continue;
            ChoutiTerm choutiTerm = new ChoutiTerm();
            choutiTerm.setWord(word);
            String nature = cixingMap.get(pos);
            if (StringUtils.isEmpty(nature)) {
                nature = pos;
            }
            choutiTerm.setNature(nature);
            resultTerm.add(choutiTerm);
        }
        if (CollectionUtils.isEmpty(resultTerm) || terms.size() < MIN_SEG_WORD_LEN) {
            return null;
        }
        return resultTerm;

    }


    public List<ChoutiTerm> segment(String text,String type) {
        text = text.replaceAll("@\\S+ ", "");
        text = text.replaceAll("@[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]{2,30}", " ");//去@某某
        text = text.replaceAll("((http|ftp|https):\\/\\/){0,1}[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?", "");//去网址
        text = text.replaceAll("]", "");
        Pattern p = Pattern.compile("#([^#]+)#");
        Matcher m = p.matcher(text);
        Set<String> topics = new HashSet<String>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                String str = m.group(i);
                topics.add(str);
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);

        List<Term> terms = segment.seg(sb.toString());
        if (CollectionUtils.isEmpty(terms)) {
            return null;
        }
        List<ChoutiTerm> resultTerm = new ArrayList<>();
        CoreStopWordDictionary.apply(terms);
        for (Term term : terms) {
            String pos = term.nature.name();
            String word = term.word.toString();
            if(!StringUtils.isEmpty(type) && type.equals(SEGMENT_TYPE_NBC)){
                if (!classifierHoldPosSet.contains(pos)) {
                    continue;
                }

            } else{
                if (!holdPosSet.contains(pos)) {
                    continue;
                }
            }
            word.replaceAll(" ","");

            if (word.length() == 1) continue;
            ChoutiTerm choutiTerm = new ChoutiTerm();
            choutiTerm.setWord(word);
            String nature = cixingMap.get(pos);
            if (StringUtils.isEmpty(nature)) {
                nature = pos;
            }
            choutiTerm.setNature(nature);
            resultTerm.add(choutiTerm);
        }
        if (CollectionUtils.isEmpty(resultTerm) || terms.size() < MIN_SEG_WORD_LEN) {
            return null;
        }
        return resultTerm;

    }

    /**
     * 分词统计词频
     *
     * @return
     */
    public Map<String, Integer> segmentFrequency(String text) {
        if(StringUtils.isEmpty(text)){
            return null;
        }
        Map<String,Integer> frequencyMap = new HashMap<>();
        text = text.replaceAll("@\\S+ ", "");
        text = text.replaceAll("@[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]{2,30}", " ");//去@某某
        text = text.replaceAll("((http|ftp|https):\\/\\/){0,1}[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?", "");//去网址
        text = text.replaceAll("]", "");
        Pattern p = Pattern.compile("#([^#]+)#");
        Matcher m = p.matcher(text);
        Set<String> topics = new HashSet<String>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                String str = m.group(i);
                topics.add(str);
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);

        List<Term> terms = segment.seg(sb.toString());
        CoreStopWordDictionary.apply(terms);

        for (Term term : terms) {
            String pos = term.nature.name();
            String word = term.word.toString();
            if (StringUtils.isEmpty(word)) {
                continue;
            }
            word.replaceAll(" ","");
            if (!holdPosSet.contains(pos)) {
                continue;
            }
            if (word.length() == 1) continue;
            if (frequencyMap.containsKey(word)) {
                frequencyMap.put(word, frequencyMap.get(word) + 1);
            } else {
                frequencyMap.put(word, 1);
            }
        }
        return frequencyMap;
    }

    /**
     * 分词统计词频
     *
     * @return
     */
    public Map<String, Integer> segmentFrequency(String text,String type) {
        if(StringUtils.isEmpty(text)){
            return null;
        }
        Map<String,Integer> frequencyMap = new HashMap<>();
        text = text.replaceAll("@\\S+ ", "");
        text = text.replaceAll("@[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]{2,30}", " ");//去@某某
        text = text.replaceAll("((http|ftp|https):\\/\\/){0,1}[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?", "");//去网址
        text = text.replaceAll("]", "");
        Pattern p = Pattern.compile("#([^#]+)#");
        Matcher m = p.matcher(text);
        Set<String> topics = new HashSet<String>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                String str = m.group(i);
                topics.add(str);
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);

        List<Term> terms = segment.seg(sb.toString());
        CoreStopWordDictionary.apply(terms);

        for (Term term : terms) {
            String pos = term.nature.name();
            String word = term.word.toString();
            if (StringUtils.isEmpty(word)) {
                continue;
            }
            word.replaceAll(" ","");
            if(!StringUtils.isEmpty(type) && type.equals(SEGMENT_TYPE_NBC)){
                if (!classifierHoldPosSet.contains(pos)) {
                    continue;
                }

            } else{
                if (!holdPosSet.contains(pos)) {
                    continue;
                }
            }
            if (word.length() == 1) continue;
            if (frequencyMap.containsKey(word)) {
                frequencyMap.put(word, frequencyMap.get(word) + 1);
            } else {
                frequencyMap.put(word, 1);
            }
        }
        return frequencyMap;
    }

    public ChoutiTermsBean segmentText(String text){
        text = text.replaceAll("@\\S+ ", "");
        text = text.replaceAll("@[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]{2,30}", " ");//去@某某
        text = text.replaceAll("((http|ftp|https):\\/\\/){0,1}[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?", "");//去网址
        text = text.replaceAll("]", "");
        Pattern p = Pattern.compile("#([^#]+)#");
        Matcher m = p.matcher(text);
        Set<String> topics = new HashSet<String>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                String str = m.group(i);
                topics.add(str);
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);

        List<Term> terms = segment.seg(sb.toString());
        if (CollectionUtils.isEmpty(terms)) {
            return null;
        }
        List<ChoutiTerm> resultTerm = new ArrayList<>();
        Map<String,Integer> frequencyMap = new HashMap<>();
        CoreStopWordDictionary.apply(terms);
        Integer totalFrequence = 0;
        for (Term term : terms) {
            String pos = term.nature.name();
            String word = term.word.toString();
            if (!holdPosSet.contains(pos)) {
                continue;
            }

            if (word.length() == 1) continue;
            ChoutiTerm choutiTerm = new ChoutiTerm();
            choutiTerm.setWord(word);
            String nature = cixingMap.get(pos);
            if (StringUtils.isEmpty(nature)) {
                nature = pos;
            }
            choutiTerm.setNature(nature);
            resultTerm.add(choutiTerm);
            totalFrequence++;
            if (frequencyMap.containsKey(word)) {
                frequencyMap.put(word, frequencyMap.get(word) + 1);
            } else {
                frequencyMap.put(word, 1);
            }
        }
        if (CollectionUtils.isEmpty(resultTerm) || terms.size() < MIN_SEG_WORD_LEN) {
            return null;
        }
        ChoutiTermsBean termsBean = new ChoutiTermsBean();
        termsBean.setTermList(resultTerm);
        termsBean.setFrequencyMap(frequencyMap);
        termsBean.setTotalFrequence(totalFrequence);
        return termsBean;
    }


}
