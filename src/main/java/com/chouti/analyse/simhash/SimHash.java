package com.chouti.analyse.simhash;



import com.alibaba.fastjson.JSONObject;
import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.segment.ChoutiTerm;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by datamining on 2016/7/12.
 */
@Profile("dohko")
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

        String text1="　　北京发布网约车管理细则 　　继今年7月28日交通运输部联合多部门公布《网络预约出租汽车经营服务管理暂行办法》后，经过几个月的研究和制定，今天，北京市《网约预约出租车经营服务管理细则》正式对外发布。 　　在国家规定和授权的基础上，北京网约车细则细化了网约车平台公司、车辆、驾驶员的相关许可和要求，规定网约车平台经营期为4年，到期经审核合格后可延期，网约车平台要依法承担承运人责任、安全生产责任和相应的社会责任，承担车辆、驾驶员的安全管理职责以及为人员和车辆购买相关保险。 　　车辆准入方面，北京网约车细则要求车辆需在北京市登记，满足北京市公布实施的最新机动车排放标准，对车辆的排量、轴距也做了明确的规定。要求，5座三厢小客车排气量不小于1.8L，车辆轴距不小于2650毫米，含新能源车；驾驶员准入方面，北京网约车细则规定从事网约车的驾驶员需具有北京市户籍，也就是在车辆和司机准入方面，北京市依旧延续了此前“京车京人”的规定，此外细则还规定网约车司机的驾驶证件需为北京市核发，接入网约车平台的个人和车辆必须经过审核，具备相关资质后方可上路参与营运。 　　新政发布后，网约车将与传统出租车在价格、营运车辆规格、营运规范等方面进行错位发展，形成差异化经营。 　　北京市交通委运输局副局长 马瑞：主要是推动巡游车(出租车)和网约车融合发展，促进巡游车（出租车）转型升级，规范网约车经营，提高服务和管理水平，为社会公众提供个性化的、安全的、便捷的出行服务。 　　征求意见 新政做出多处调整 　　今年10月8号，北京市公开发布网约车管理实施细则的征求意见稿，面向大众征求意见和建议。那么，大众都反映了哪些意见，管理实施细则前后都做出了哪些修改呢？ 　　据了解，在征求意见阶段，北京市政府共收集社会公众有效反馈意见和建议9246条。其中，“网约车车辆资质条件及管理”受关注度最高，占总条数的36%。对网约车营运车辆必须为北京牌照的意见，超过90%的人表示赞同；对车辆排量条件要求的意见，表示质疑和提出修改意见的占到54%。 　　之后，经过多番讨论研判，在新版管理实施细则中对旧有条目进行修改的内容主要包括：由“5座三厢小客车排量不小于2.0升或1.8T”改为“5座三厢小客车排量不小于1.8升”；“车辆轴距不小于2700毫米”调整为“车辆轴距不小于2650毫米”；并去掉了原条目中“7座乘用车车长大于5100毫米”的限制性条件等，共计39处修改。 　　北京市交通委运输局副局长 马瑞：这回调整完了以后，与征求意见稿相比，在车辆的轴距和排量上有所降低，跟传统的巡游车（出租车）接近，但是又高于传统的巡游车（出租车），这样会为两个行业融合发展打下基础。 　　同时，与网约车管理实施细则一同发布的《北京市关于深化改革推进出租汽车行业健康发展的实施意见》和《北京市私人小客车合乘出行指导意见》，与其相对应的征求意见稿相比，同样有政策规定上的新增或修改共83条。 　　另外，在此次征求意见阶段、关注度同样较高的，“网约车司机必须为北京户籍”的意见，表示质疑的只占25%，因此在最终正式发布的文件中没有进行调整。（央视记者 何畅 王丰 胡亚利 危家煦） 　　沪版网约车细则正式落地 仍坚持“沪人沪牌” 　　经过两个多月时间的酝酿，备受瞩目的上海版网约车细则在今天（21日）下午正式发布并开始实施。跟10月8号公布的草案相比，其中原则性的规定并未改变，上海市仍坚持网约车需“沪人沪牌”。 　　1. 仍坚持“上海人”“ 上海车”。明确“网约车应在上海市注册登记，达到上海市规定的机动车排放标准”、且“驾驶员应为本市户籍”。 　　2. 网约车车辆轴距条件放宽为“达到2600毫米以上”，不再区分燃油车辆和新能源车辆。 　　3. 不再要求网约车驾驶员“持有本市公安机关核发的”机动车驾驶证。 　　4. 规定网约车驾驶员不能“扫马路”和站点候客――“不得巡游揽客，不得在机场、火车站巡游车营业站区域内揽客”。网约车，则只能通过预约方式提供服务。 　　5. 针对合乘车，对“人车绑定”的要求调整为以家庭为单位。 　　6. 为解决“打车难”问题，将综合考虑人口数量、车辆里程利用率、城市交通拥堵状况等因素，定期评估并动态调整出租汽车运力的投放规模。 　　7. 新政不设缓冲期，即日起正式实施。（央视记者 魏然）";
        String text2="（原标题：京沪网约车细则正式公布 仍坚持京籍京牌沪籍沪牌） 北京发布网约车管理细则 继今年7月28日交通运输部联合多部门公布《网络预约出租汽车经营服务管理暂行办法》后，经过几个月的研究和制定，今天，北京市《网约预约出租车经营服务管理细则》正式对外发布。 在国家规定和授权的基础上，北京网约车细则细化了网约车平台公司、车辆、驾驶员的相关许可和要求，规定网约车平台经营期为4年，到期经审核合格后可延期，网约车平台要依法承担承运人责任、安全生产责任和相应的社会责任，承担车辆、驾驶员的安全管理职责以及为人员和车辆购买相关保险。 车辆准入方面，北京网约车细则要求车辆需在北京市登记，满足北京市公布实施的最新机动车排放标准，对车辆的排量、轴距也做了明确的规定。要求，5座三厢小客车排气量不小于1.8L，车辆轴距不小于2650毫米，含新能源车；驾驶员准入方面，北京网约车细则规定从事网约车的驾驶员需具有北京市户籍，也就是在车辆和司机准入方面，北京市依旧延续了此前“京车京人”的规定，此外细则还规定网约车司机的驾驶证件需为北京市核发，接入网约车平台的个人和车辆必须经过审核，具备相关资质后方可上路参与营运。 新政发布后，网约车将与传统出租车在价格、营运车辆规格、营运规范等方面进行错位发展，形成差异化经营。 北京市交通委运输局副局长 马瑞：主要是推动巡游车(出租车)和网约车融合发展，促进巡游车（出租车）转型升级，规范网约车经营，提高服务和管理水平，为社会公众提供个性化的、安全的、便捷的出行服务。 征求意见 新政做出多处调整 今年10月8号，北京市公开发布网约车管理实施细则的征求意见稿，面向大众征求意见和建议。那么，大众都反映了哪些意见，管理实施细则前后都做出了哪些修改呢？ 据了解，在征求意见阶段，北京市政府共收集社会公众有效反馈意见和建议9246条。其中，“网约车车辆资质条件及管理”受关注度最高，占总条数的36%。对网约车营运车辆必须为北京牌照的意见，超过90%的人表示赞同；对车辆排量条件要求的意见，表示质疑和提出修改意见的占到54%。 之后，经过多番讨论研判，在新版管理实施细则中对旧有条目进行修改的内容主要包括：由“5座三厢小客车排量不小于2.0升或1.8T”改为“5座三厢小客车排量不小于1.8升”；“车辆轴距不小于2700毫米”调整为“车辆轴距不小于2650毫米”；并去掉了原条目中“7座乘用车车长大于5100毫米”的限制性条件等，共计39处修改。 北京市交通委运输局副局长 马瑞：这回调整完了以后，与征求意见稿相比，在车辆的轴距和排量上有所降低，跟传统的巡游车（出租车）接近，但是又高于传统的巡游车（出租车），这样会为两个行业融合发展打下基础。 同时，与网约车管理实施细则一同发布的《北京市关于深化改革推进出租汽车行业健康发展的实施意见》和《北京市私人小客车合乘出行指导意见》，与其相对应的征求意见稿相比，同样有政策规定上的新增或修改共83条。 另外，在此次征求意见阶段、关注度同样较高的，“网约车司机必须为北京户籍”的意见，表示质疑的只占25%，因此在最终正式发布的文件中没有进行调整。（央视记者 何畅 王丰 胡亚利 危家煦） 沪版网约车细则正式落地 仍坚持“沪人沪牌” 经过两个多月时间的酝酿，备受瞩目的上海版网约车细则在今天（21日）下午正式发布并开始实施。跟10月8号公布的草案相比，其中原则性的规定并未改变，上海市仍坚持网约车需“沪人沪牌”。 1. 仍坚持“上海人”“ 上海车”。明确“网约车应在上海市注册登记，达到上海市规定的机动车排放标准”、且“驾驶员应为本市户籍”。 2. 网约车车辆轴距条件放宽为“达到2600毫米以上”，不再区分燃油车辆和新能源车辆。 3. 不再要求网约车驾驶员“持有本市公安机关核发的”机动车驾驶证。 4. 规定网约车驾驶员不能“扫马路”和站点候客——“不得巡游揽客，不得在机场、火车站巡游车营业站区域内揽客”。网约车，则只能通过预约方式提供服务。 5. 针对合乘车，对“人车绑定”的要求调整为以家庭为单位。 6. 为解决“打车难”问题，将综合考虑人口数量、车辆里程利用率、城市交通拥堵状况等因素，定期评估并动态调整出租汽车运力的投放规模。 7. 新政不设缓冲期，即日起正式实施。 本文来源：央视 责任编辑：钟齐鸣_NF5619";
        Map<String, Integer> list1 = segment.segmentFrequency(text1);
        Map<String, Integer> list2 = segment.segmentFrequency(text2);

        System.out.println("textLen:"+list1.size());
        System.out.println("list1:"+ JSONObject.toJSONString(list1));
        System.out.println("list2:"+ JSONObject.toJSONString(list2));
        SimHash simHash1=new SimHash(text1,64);
        SimHash simHash2=new SimHash(text2,64);
        int dist=simHash1.getDistance(simHash1.strSimHash,simHash2.strSimHash);
        System.out.println(dist);

    }
}