package com.chouti.analyse.simhash;



import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.segment.ChoutiTerm;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by datamining on 2016/7/12.
 */
public class SimHash {

    private String tokens;

    private BigInteger intSimHash;

    public String strSimHash;

    private int hashbits = 64;

    private ChoutiSegment segment = new ChoutiSegment();

    public SimHash(String tokens) throws IOException {
        this.tokens = tokens;
        this.intSimHash = this.simHash();
    }

    public SimHash() {

    }

    public SimHash(String tokens, int hashbits) throws IOException {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.intSimHash = this.simHash();
    }

    HashMap<String, Integer> wordMap = new HashMap<String, Integer>();

    public BigInteger simHash() throws IOException {
        int[] v = new int[this.hashbits];
        List<ChoutiTerm> segWords = segment.segment(tokens);
        if(null == segWords || segWords.size() <= 0){
            return null;
        }
        for (int j = 0;j < segWords.size();j++) {
            String word = segWords.get(j).getWord();
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0) {
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }

        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
//        System.out.println(this.strSimHash + " length " + this.strSimHash.length());
        return fingerprint;
    }


    public BigInteger simHash(List<ChoutiTerm> segWords,int hashbits) throws IOException {
        int[] v = new int[this.hashbits];
        if(null == segWords || segWords.size() <= 0){
            return null;
        }
        for (int j = 0;j < segWords.size();j++) {
            String word = segWords.get(j).getWord();
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0) {
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }

        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
//        System.out.println(this.strSimHash + " length " + this.strSimHash.length());
        return fingerprint;
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    public int hammingDistance(SimHash other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

        // 统计x中二进制位数为1的个数
        // 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，
        // 对吧，然后，n&(n-1)就相当于把后面的数字清0，
        // 我们看n能做多少次这样的操作就OK了。

        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    public int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    public List subByDistance(SimHash simHash, int distance) {
        // 分成几组来检查
        int numEach = this.hashbits / (distance + 1);
        List characters = new ArrayList();

        StringBuffer buffer = new StringBuffer();

        int k = 0;
        for (int i = 0; i < this.intSimHash.bitLength(); i++) {
            // 当且仅当设置了指定的位时，返回 true
            boolean sr = simHash.intSimHash.testBit(i);

            if (sr) {
                buffer.append("1");
            } else {
                buffer.append("0");
            }

            if ((i + 1) % numEach == 0) {
                // 将二进制转为BigInteger
                BigInteger eachValue = new BigInteger(buffer.toString(), 2);
                System.out.println("----" + eachValue);
                buffer.delete(0, buffer.length());
                characters.add(eachValue);
            }
        }

        return characters;
    }


    public static void main(String[] args) throws IOException {

        String text1="遇见你之前/MeBeforeYou";
        String text2="两名美国人在阿富汗遇袭身亡";


        String text3="南棒海豹…跟他爹有一拼…#军事视频# #军事新闻# #国际军事# #环球军事# #武器的秘密# #浩汉防务# #制造业强国# #航空知识# #航空科普# #军情解码# http://t.cn/RtAJo5i .";
        String text4="【女子无证驾车被查 #扬言嫁给交警#赖一辈子[衰]】27日，交警在宁宣高速和凤收费拦截了一辆超速越野车，经查，驾车女子是无证驾驶。当女子听说要扣车并且还会有进一步处罚时，情绪非常激动，威胁交警已经给他拍照并记住警号了，如果非要处罚，她就嫁给交警，赖上一辈子。http://t.cn/Rtc4012扬子晚报";
        String text5="鲸播报：长沙非法拆除房屋致人死亡事故7名干部被批捕】2日讯，从长沙市人民检察院获悉，长沙市岳麓区观沙岭街道茶子山村非法拆除房屋致人死亡事故的侦查与处理已有新进展。包括观沙岭街道原党工委书记、办事处主任等7名干部已于近日分别被长沙市人民检察院依法批捕。(新华社)";
        String text6="在Mnet新舞蹈竞演节目#Hit The Stage#中，当“鬼怪”的突然出现时各位爱豆的反应。#SISTAR宝拉#、#少女时代孝渊#、#SHINee泰民#、#Infinite Hoya#、#Block B有权#、#Monsta X Shownu#、#NCT Ten#、#Twice##Momo#/#Sana#/#俞定延#/#金多贤#/#朴志效#    http://t.cn/RtcUnZ9 .";
        String text7="【女子无证驾车被查 #扬言嫁给交警#赖一辈子[衰]】27日，交警在宁宣高速和凤收费拦截了一辆超速越野车，经查，驾车女子是无证驾驶。当女子听说要扣车并且还会有进一步处罚时，情绪非常激动，威胁交警已经给他拍照并记住警号了，如果非要处罚，她就嫁给交警，赖上一辈子。http://t.cn/Rtc4012";
        String text8="【女子无证驾车被查 #扬言嫁给交警#】27日，交警在宁宣高速和凤收费拦截了一辆超速越野车，经查，驾车女子是无证驾驶。当女子听说要扣车并且还会有进一步处罚时，情绪非常激动，威胁交警已经给他拍照并记住警号了，如果非要处罚，她就嫁给交警，赖上一辈子。#控制不了自己情绪的女子娶不得[嘻嘻]#...全文： http://m.weibo.cn/1892327960/4002508907303169";
        String text9="【长沙检方：涉非法强拆致居民被埋死亡案的7名干部被批捕】从长沙市人民检察院获悉，岳麓区观沙岭街道茶子山村非法拆除房屋致人死亡事故的侦查与处理已有新进展。包括观沙岭街道原党工委书记、办事处主任等7名干部已于近日分别被长沙市人民检察院依法批捕。新华网http://t.cn/RtJiHC5";


        SimHash simHash1=new SimHash(text1,64);
        SimHash simHash2=new SimHash(text2,64);
        SimHash simHash3=new SimHash(text2,64);
        SimHash simHash4=new SimHash(text2,64);
        SimHash simHash5=new SimHash(text2,64);
        SimHash simHash6=new SimHash(text2,64);
        SimHash simHash7=new SimHash(text2,64);
        SimHash simHash8=new SimHash(text2,64);
        SimHash simHash9=new SimHash(text2,64);


        int dist=simHash1.getDistance(simHash1.strSimHash,simHash2.strSimHash);
        int dist1=simHash1.getDistance(simHash4.strSimHash,simHash5.strSimHash);
        int dist2=simHash1.getDistance(simHash7.strSimHash,simHash8.strSimHash);
        System.out.println(dist);
        System.out.println(dist1);
        System.out.println(dist2);
    }
}