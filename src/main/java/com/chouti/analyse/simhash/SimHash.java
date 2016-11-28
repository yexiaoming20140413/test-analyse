package com.chouti.analyse.simhash;



import com.alibaba.fastjson.JSONObject;
import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.segment.ChoutiTerm;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by datamining on 2016/7/12.
 */
public class SimHash {

    private String tokens;

    private BigInteger intSimHash;

    public String strSimHash;

    private int hashbits = 64;

    private static ChoutiSegment segment = new ChoutiSegment();

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

        String text1="看完这两个故事，你会珍惜对方一辈子 珍惜对方故事 浙大黄燕华 · 2016-11-28 10:36 浙江大学热门课程推荐： 浙江大学领导力提升与公司治理创新高级研修班 工业和信息化部中小企业领军人才千人培训计划（浙江大学第五期） 浙江大学金融创新与投资高级研修班 浙江大学国学智慧与商道文化高端研修班 浙江大学求是女子学堂 联系人：黄燕华 老师 微 信：yanhua5555555 电 话：13758218022 传 真：0571-8827 3952 浙大黄燕华(yanhua8022)";
        String text2="朋友就是雨中的伞 朋友 浙大姜曼 · 2016-11-28 10:36 浙江大学热门课程推荐： 浙江大学领导力提升与公司治理创新高级研修班 工业和信息化部中小企业领军人才千人培训计划（浙江大学第五期） 浙江大学金融创新与投资高级研修班 浙江大学国学智慧与商道文化高端研修班 浙江大学求是女子学堂 联系人：姜 曼 老师 微  信：mamiaon6081 电  话：15088677345 传  真：0571-8827 3952 浙大姜曼(jiangman6081)";

        Map<String, Integer> list1 = segment.segmentFrequency(text1);
        Map<String, Integer> list2 = segment.segmentFrequency(text2);
        System.out.println("list1:"+ JSONObject.toJSONString(list1));
        System.out.println("list2:"+ JSONObject.toJSONString(list2));
        SimHash simHash1=new SimHash(text1,64);
        SimHash simHash2=new SimHash(text2,64);
        int dist=simHash1.getDistance(simHash1.strSimHash,simHash2.strSimHash);
        System.out.println(dist);

    }
}