package com.chouti.analyse.train;

import com.chouti.analyse.configure.CommonParams;
import com.chouti.analyse.model.NbcWordsMap;
import com.chouti.analyse.segment.ChoutiSegment;
import com.chouti.analyse.utils.NBClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-18.
 *******************************************************************************/
public class TestClass {
    private static Logger logger = LoggerFactory.getLogger(TestClass.class);
    private static Map<Integer,String> categoryMap1 = new HashMap<>();
    private static List<NbcWordsMap> allCategoryWordList = new ArrayList<>();
    private static ChoutiSegment choutiSegment = new ChoutiSegment();

    private static String wordsFileDir ="/home/xiaoming/newsLearn/news_train_chouti/";
//    private static String wordsFileDir ="/home/xiaoming/newsLearn/news_train_fudan/";

    public static void main(String args[]){
        categoryMap1.put(1,"财经");
        categoryMap1.put(3,"教育");
        categoryMap1.put(4,"军事");
        categoryMap1.put(5,"汽车");
        categoryMap1.put(6,"体育");
        categoryMap1.put(7,"娱乐");


        categoryMap1.put(8,"科技");
        categoryMap1.put(9,"游戏");

        for(Iterator iterator = categoryMap1.entrySet().iterator(); iterator.hasNext();){
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer cateGoryId = (Integer) entry.getKey();
            loadCategoryNbcPositiveWords(cateGoryId);
        }
        loadCategoryNbcNegativeWords();

        String content="【导语】旧时情人狭路相逢，竞争对手明争暗斗，爱情宿敌一朝相遇，仇人见面分外眼红。娱乐圈里三大敏感关系――老情人、死对头、恶情敌，物是人非尴尬来袭，情非得已被迫同台，他们是以优雅的相逢一笑泯恩仇还是面面相觑欲言又止，沦为最熟悉的陌生人。人世间最尴尬事不过如此，怕见却又转角遇爱恨，倾尽一生苦。惯看春花秋月潮起潮落，那些年为拼事业为谈情感不幸反目成仇的结怨明星，重逢之时话感伤。纵使发誓今生今世老死不相往来，却又此生此世机缘巧合。周杰伦、蔡依林若干年后居然也会\"双J\"同台；刘德华、陈玉莲抛弃爱恨情真意切；刘嘉玲、曾华倩洗去铅华视如知音；章子怡、范冰冰、周迅、赵薇群星争艳，范冰冰疑似遭孤立；不能同台的全同台了，感叹时光留住了尴尬的极致经典。[点击查看高清组图] 下一个 周杰伦&蔡依林 周杰伦与蔡依林2001年彼此互帮互助共谋发展，2002年出双入对传出\"双J\"恋情成型，接二连三以歌曲形式互表情意，而后感情突然走了下坡路，有人说他们经不住第三者介入，也有人说也经不起父母反对，还有算命师指出他们彼此是对方的事业大敌，重压之下蔡依林与周杰伦的爱情走到尽头。谈到伤心处，蔡依林失恋之初孤独痛苦半年之久，需要闺蜜陪伴才能忍住不与他联系，睹物思人时常狂哭。时间是良药，2013年7月6日第24届金曲奖颁奖典礼，他们惊艳地同台，台下人不断呼唤抱一下，可是内向的他们害羞地拒绝了，抱一下都不肯，那么在一起就更是不可能的事了，真正放下的人才能坦然重逢，而真正爱过的人不可能轻易放下。[视频：周杰伦蔡依林双J同台颁奖 被起哄抱一下] 上一个 下一个 林心如&林志颖 掀起尘封已久的往事，很少有人清楚林志颖与林心如的那段感情瓜葛。1995年她还未成名，他已是当红巨星。演绎《校园敢死队》纯情男女主角时他们相识相知，可惜万人迷林志颖经常被女生追求，害的林心如醋意大发，每次争吵后都向好友诉苦，好友看不惯支持二林分手，心软的她一直舍不得。1998年拍摄《还珠格格》，林心如也开启她的绯闻之旅，与苏有朋频传来电，渐渐她与林志颖情感破裂，其实她与苏有朋不过是朋友，为此痛失旧爱多少有些遗憾。2012年12月安徽卫视国剧盛典，林心如林志颖同台相邻，关系炸开锅，成全场焦点。他们比邻而坐，中间隔着一个灯泡钟汉良，却还是忍不住交头接耳眉目传情。而今男已婚女未嫁，林心如贵为一线女神，被众男鼎鼎膜拜，乃娱乐圈的中流砥柱，这位女强人势必也被旧情人暗地眷顾和怀念着。[视频：林心如依下巴选情郎 难忘旧爱林志颖] 上一个 下一个 李嘉欣&黎明 黎明有幸被李嘉欣纠缠却不予珍惜，1992年《原振侠》成就这对荧屏绯闻情侣，黎明回避着\"金童玉女\"的称谓，不愿与李嘉欣走的太近，惧怕孤独需要呵护的李嘉欣却毫不掩饰个人情感，高调示爱主动示好，这让低调的他非常反感，心灰意冷的她觉得自己跟他不会有好结果，黯然放弃，2008年黎明与乐基儿结婚，而当年李嘉欣也嫁入豪门与许晋亨生喜结连理，缘分就这样飘散，他们却是相知多年的好友。他们的缘分结束了，交集并没有结束，2009年倾情出演《十月围城》，2010年为玉树赈灾义卖募捐，同台共事心情愉悦，这其中也许暗藏玄机。试想如果许晋亨为人心胸狭隘，那李嘉欣与旧情人相遇必然遭到豪门不满，不避嫌正说明她的生活何等滋润洒脱自由自在。而乐基儿也非常知趣，知道他俩要同台特地跑到一边装作不知道，给他们留足面子。[视频：黎明携手旧爱李嘉欣 黑脸不提太太乐基儿] 上一个 下一个 刘德华&陈玉莲 刘德华十年前公开陈玉莲是自己的暗恋对象，断臂也在所不辞的最爱；二十年前他对她朝思暮想，入戏容易出戏难，荧屏情侣戏假情真单相思。一部神雕侠侣，他是杨过，她是他的小龙女，暗恋痛并快乐，偷偷跟对方表白，却惨遭拒绝，后来得知她是自己的偶像周润发的女友之后才死心。可惜陈玉莲命运坎坷，周润发爱到为她自杀却还是有缘无分，嫁作商人妇匆匆离婚，近年来又传同性恋，曲折凄凉冷暖自知。刘德华对陈玉莲属于单相思，他是重情重义的老好人，害怕尴尬也害怕另一半吃醋，因此他们同台的机会就更可遇而不可求。终于在2011年，刘德华演唱会力邀陈玉莲合唱《情义两心坚》，见面第一句话就是问她是否回到了绝情谷，陈玉莲红颜苍老徒留恬淡，刘德华皱纹丛生无限酸涩。二十年后想见，恐怕相顾无言，一腔热血都化为歌词了吧。[视频：刘德华曾向陈玉莲表白 可惜对方没回应] 上一个 下一个 黄宗泽&胡杏儿 黄宗泽胡杏儿，TVB众多绯闻情侣中的一对儿，他们惺惺相惜，2005年拍《野蛮婆婆》互生好感，他以600个短信的强烈攻势追到胡杏儿，这已成圈内奇闻，他毁掉胡杏儿与当时相恋多年男友的好前程。可惜后来黄宗泽在情感中占据了主动权就开始变心，在公众面前并不承认胡杏儿为正牌女友，抱着不承认也不否认的态度，加上他改不掉花心的习惯，与众多女艺人传绯闻，拍戏聚少离多，感情转淡一系列问题，忍无可忍的胡杏儿大发雷霆，与他终结八年恋情。TVB艺人会面的机会过多，年度台庆以及各种新片宣传和商业活动，都免不了抬头不见低头见的尴尬。2013年5月，工作原因致使300多位TVB艺人需要同台合影，胡杏儿黄宗泽见面了，却零交流，为挽颜面避免交集，胡杏儿与紧挨着的马国明交流，黄宗泽和一旁的徐子珊搭讪，才令气氛缓解，他们都在为自己找台阶下，这也透露出他们没有将对方在心底完全放下。[视频：逃不过的旧爱话题黄宗泽否认与胡杏儿零交流] 上一个 下一个 李冰冰&范冰冰 内地女星中四旦双冰体系的建立，双冰争艳是最耀眼的比美。双冰争艳的开端是一个萍水相逢的故事，早在1999年两个冰冰初出茅庐情同姐妹，合作拍戏毫无芥蒂。她们同名不同姓，自有一段天生默契。可惜从2000年范冰冰签约华谊兄弟之后，双冰争艳的桥段便被公司炮制，闹不和的传闻愈演愈烈，2005年达到顶峰。她们攀比年龄互称姐姐，无休止地比美貌比才艺比成绩，连博客的文字功底都要比来比去，关系冰到极点，乃至她们后来走的路子也在针锋相对，一个自称范爷一个自称李爷，都将自己装扮成称王称霸的纯爷们，都往国际化路线靠拢，暗示\"华谊一姐\"斗争何其激烈，也暗示她们在各自商业化道路上如何勾心斗角抢夺代言人的不争事实。这段斗争最终以范冰冰离巢自组工作室、李冰冰留在华谊延续金牌地位而暂告一段。若干年后她们依然常常碰面，一碰面便是记者镜头咔嚓高谈阔论时。2013年2月奥斯卡颁奖典礼，范冰冰以条纹礼服与李冰冰的荷叶边西服套装碰出新火药味，要说关系，她们其实并不疏远，她们还会迎风而上手挽手亲上加亲，打破常规使劲斗艳。看得出越被曝光就越愿意配合出亲人的感觉，只是表情方面透露着蛛丝马迹被观众捕捉出玄机：范爷努力摆出气场强大艳压群芳的姿态，李冰冰则流露出几分尴尬，想以淡定成熟的沉稳表现女星强大的魅力。[视频：范冰冰李冰冰奥斯卡相遇] 上一个 下一个 章子怡&巩俐 张艺谋慧眼识珠，80年代挖出一代“谋女郎”巩俐， 90年代挖出与巩俐容颜相近的二代“谋女郎”章子怡。两位女星都被他一手捧成国际巨星，她们同样有野心，同样国际范儿，同样才貌双全，同样能为中国女演员代言。2005年两人首度跨越谋女郎的距离，合作《艺伎回忆录》大飙演技，姜还是老的辣，巩俐使尽浑身解数终于占尽先机。那一年后她们很少有交集，更没有合作。2012年4月中国电影导演协会评出的年度女演员奖正是章子怡，而巩俐作为特邀嘉宾出场，那老牌女星的hold住之精神，那香艳之吻温柔牵手，不知道元芳会怎么看，张艺谋会怎么看。看尽繁花似锦，被同一位导演宠溺过的御用演员，她们貌似有种穿越时代的深沉友谊。[视频：巩俐 章子怡拥抱粉碎\"不和传闻\"] 上一个 下一个 李宇春&张靓颖 疯狂的“玉米”与潮流的“凉粉”各自在为偶像尽心尽意如痴如醉，2005年湖南卫视超女选秀中的冠军李宇春与季军张靓颖，两个草根出身的超女巨星，表面比出了高下，实则后续发展之中她们各有套路，谁都非常了不起。李宇春标新立异，声音、身材、长相较为中性化，拓宽多元化之路，转战影视圈；而张靓颖功底不输给李宇春，她形象很女人，声音很海豚，选秀结束后签约华谊熬出了多首大片经典名曲，《画心》伴随她人气飙升，她的国际化路线风生水起。往往同一届超女猛将，谁都不想沦为对方的陪衬，她们不愿再当台前的对手，幕后也不愿被粉丝拿去硬生生的比对，她们却逃脱不了宿命的追击。没有对比就失去娱乐价值，每一位艺人内心都有一份傲气，当她们都达到了一个高度，傲气便悄然消逝。李宇春、张靓颖的2011年，北京出席中歌榜活动，台上两个人那份两两相望百感交集的心情，青涩的小草根们成熟了，褪去浮华，唯有努力才不被时代抛弃。[视频：李宇春张靓颖齐头并进 昔日战友没联系] 上一个 下一个 佘诗曼&杨怡 香港无线花旦们的风起云涌从未停休，花旦花名册中，通过港姐竞选成名的佘诗曼于1997年进入TVB，起点高，名气大，一上戏便捞得翻拍的重点金庸剧《雪山飞狐》《碧血剑》中的角色；而1999年出道，通过无线艺员培训入驻TVB的杨怡，多年龙套咬牙挣扎在底层，一步步得以晋升。佘诗曼曾经因拍《金枝欲孽》而被观众拿来和黎姿蔡少芬做对比，斗的死去活来，后来黎姿息影蔡少芬转战内地，佘诗曼又成公司最有前途的花旦，缺乏人才的TVB有意力捧新人重塑新花旦，佘诗曼、杨怡奉天承运决斗于《宫心计》，开启新花旦战役。佘诗曼扮演顺风顺水的刘三好，而杨怡饰演由善转恶的心机奸妃姚金铃，不得不承认杨怡眉眼高挑心机沉沉，仿佛表现出对刘三好的各种羡慕嫉妒恨，恨到牙根都痒痒，戏里戏外关系难辨真假。2009年佘诗曼杨怡斗法斗到顶峰造极，她们简直比宫心计本身还要艳光四射，温度升腾火药味十足，大家都在揣测这对儿当年台庆的封后竞争对手的微妙关系，她们没有避讳，反而大胆同台！杨怡靠奸角上位，拿下当年我最喜爱的电视女角色奖，佘诗曼亲自为之颁奖，以往那些面和心不合的传闻，她们要狠狠将之击碎，她们表现的情同姐妹，杨怡泪流满面。直到2012年压抑了十三年的杨怡终获无线视后扬眉吐气，洗刷了与佘诗曼之间的怨气。 上一个 下一个 赵雅芝&叶童 千年等一回，他和她，居然也有解不开的千年宿怨。二十年前轰动一时的《新白娘子传奇》，西湖畔白蛇小牧童情定三生，怎知台上笑脸台下斗狠。传说娘子不满官人住的好，官人嫉妒娘子长的美，只要镜头一走她们立即互不理睬，镜头一来立马神奇地和好，这传闻雷倒观众，不知秒杀了多少资深新白迷，撕碎多少纯洁的心灵。在任何时代女演员片酬竞争都无比激烈，赵雅芝叶童关系敏感疑似与片酬有关。当时的赵雅芝刚刚拍完《戏说乾隆》，在台湾女星中地位几乎无人能及，而叶童也以实力派的名头稳稳崛起，不屈居人下，她的许仙反串很出色，但赵雅芝的美貌也很厉害，她们戏里恩爱，拼的是演技；戏外冷脸，比的是排场，旁观者们完全可以理解。两人二十年没有公开解释过这段关系，留给观众一片谜团，但凡新白再聚首、新白西湖游等等纪念性事件，总不见她们\"破镜重圆\"。好在上天有德缘分天定，给了白蛇许仙一次重聚的机会。2013年江苏卫视春晚，许仙与白娘子好的不得了，叶童开诚布公地表达看到赵雅芝那媚眼一抛就会魂飞魄散，爱她的美还来不及，怎么舍得闹别扭让她伤心难过，而赵雅芝对叶童这位戏里夫君、戏外姐妹感动的直掉眼泪，携带丫鬟小青，三个人好的一塌糊涂，对此新白续曲，你不抛眼泪也无由。[视频：赵雅芝、叶童联手演唱《千年等一回》] 上一个 下一个 赵薇&周迅 娱乐圈里没有真正的友谊，即便是有也难以长久，这句话好像在赵薇周迅身上得以验证。2001年赵薇、周迅被陈坤极力撮合而结识，那时她们事业刚起步不久，可以心无芥蒂敞开心扉地交往，她们有相似之处为根基，又有陈坤穿针引线，大有相见恨晚之感，经常被拍到深夜买醉秉烛夜谈的友情场景。然而岁月变迁，她们关系逆转，发生了不可思议的质变：争夺画皮女一号名分、抢夺好友陈坤、片场互相踢凳子撒泼斗气、争夺影后、比拼片酬人气，乃至那么好脾气的赵薇在周迅面前变得凶巴巴，低调无所谓的周迅变得高姿态不言败。《画皮》宣传阶段，人气王赵薇正室，而周迅饰演的是小三，她的角色呼声也远远高过周迅，以演技派著称的周迅怎能平衡。陈坤为撑周迅与赵薇日渐疏远，今年赵薇执导的电影上映都没能得到他的支持，连带黄晓明对陈坤都产生了不满，犹如友情版\"四角恋\"难以捉摸这一切被媒体看在眼里不断放大，写出诸多报道，影响了三个人之间纯洁的友情。踢凳子的事件传到赵薇周迅的耳朵里，她们笑言没事就玩踢凳子来互相调侃，从此凳子成为她们之间开玩笑的工具，赵薇特地发出闺蜜照证实感情未破裂，还在画皮宣传中微笑同台，化解恩怨情仇，让故意找茬的狗仔无言以对。现在的她们很少同台了，是为了避嫌，也为了少一些是非争端，台前不能形影不离幕后就偷偷会面吧，十几年的情感就这样终结的话也太残酷了。[视频：赵薇周迅被传不和 《画皮2》片场互踢座椅] 上一个 下一个 小S&曾宝仪 1996年时十八岁的小S情牵黄子佼，2000年黄子佼忽然提出分手让她猝不及防，原因居然是曾宝仪的插足，在某次偶遇中，花心男黄子佼对曾宝仪一见钟情干脆甩掉小S，主动提出分手，并且默认新欢正是曾宝仪，小S的心顿时七零八落痛苦不堪，那段时间上节目还因控制不住情绪而大哭，一度傻傻地跑到曾宝仪面前质问，对方当然打死也不肯承认，她俩就这样断绝了朋友关系。十年不敢同台的人喜迎双姝会面，2010年第四十七届台湾金马奖颁奖晚会，这对非常不可能同台的昔日情敌同台拥抱，小S戏言抱着曾宝仪的时候她在掐她，笑称她不是自己的仇人而是恩人，言外之意感激曾宝仪。她让她看清了一个人的真面目，没有与他的分手怎么可能等到今生的缘分，将许雅钧这位高富帅的豪门绅士泡到手，怎么会有这么幸福讨人爱的女儿们，福兮祸兮难以测量。[视频：金马奖颁奖礼现场 小S与曾宝仪紧紧相拥] 上一个 下一个 蒙嘉慧&邵美琪 邵美琪是郑伊健第一任圈内女友，蒙嘉慧是郑伊健最后一任女友，但郑伊健情感世界里只能有一个老婆，崭新的蒙嘉慧便与古老的邵美琪结成情敌。但她们真正的共同敌人恐怕是梁咏琪更恰当，当年梁咏琪从邵美琪手里将他夺走，又被蒙嘉慧从手里夺走了他，毕竟她俩之间的恩怨还有跨度七年的距离可供缓和。多变的郑伊健如此不坚定移情别恋，在邵美琪心底留下伤疤，看到蒙嘉慧她往事重上心头，心理状态必然好不到哪里去。2007年她俩合拍了香港无线的《通天干探》，且为宣传而同台竞艳，为工作不得已，却也无话可说，尴尬之下保持着距离。蕙质兰心的黎姿怕她们产生交集不愉快，特地站在两人中间周旋，消除她们的尴尬，从洞察人情世故这一点来看，黎姿也是位真是用心良苦的善良好姑娘。[视频：郑伊健蒙嘉慧东京完婚 邵美琪祝福引人关注] 上一个 下一个 刘嘉玲&曾华倩 年少相识的刘嘉玲与曾华倩，是昔日的TVB培训班同学同事兼无话不谈的闺蜜，却因一个梁朝伟绝交。1982年至1989年，曾华倩与梁朝伟金童玉女数度分分合合，情人间竟也遭遇到七年之痒，前来协调两人关系的知己刘嘉玲，意外的横刀夺爱将好朋友的男朋友梁朝伟撬走。一个是知己，一个是爱人，曾华倩俨然夹心人，六年光阴原来是在为他人做嫁衣裳，黯然神伤之余，她做出了一个艰难的决定，决定和他们恩断情绝。刘嘉玲能抢走梁朝伟与性格绝对有关，她豪爽成熟又大姐大，不似曾华倩那么任性，梁朝伟不善言辞性格孤僻，遇到成熟的女子更有安全感，于是他矢志不移地恋上刘嘉玲。时光是医治伤口的良药，时光冲淡了爱恨绵绵，时光也制造了失而复得。2004年由吴君如周密安排，刘嘉玲曾华倩重相见，相逢一笑泯恩仇，2011年曾华倩主持节目，刘嘉玲担任特邀嘉宾，她们手拉手面对镜头，前仇旧恨随风而去。人到中年遇一旧知己推杯换盏何等逍遥。刘嘉玲戏言没有嫁给梁朝伟这样的神经质演员是曾华倩的福分，善解人意的她还传达出多年来曾华倩一直藏在梁朝伟的心底这一事实，她们不再为感情困扰不再为谁输谁赢而纠结，大度释怀完成历史性的牵手，享受友情失而复得的美好。[视频：曾华倩附和刘嘉玲挺梁朝伟 昔日情敌共赞\"一夫\"] 上一个 下一个 赵薇&周迅&章子怡&范冰冰&李冰冰 这一组人物关系千丝万缕，让人想绕都绕不出来，只得将节操碎在他们中间。赵薇周迅为拍《画皮》关系紧张，赵薇、黄晓明曾经的绯闻情侣，赵薇章子怡的一线女星之争，赵薇范冰冰的格格丫鬟之旧恨，范冰冰李冰冰的双冰争艳卷土重来，简直谁都不服谁。奇怪的是，由黄晓明亲自将范冰冰分隔出来，她被孤立了，而其他女星之间在一边拉家常，她则视而不见地扮演着女神，手拢头发、双手合一，小动作不断，心理学角度解释，她内心不安，在使劲掩饰心底的尴尬，而抱着章子怡的是赵薇，她暗示着，女星的心境有何等重要，那些幕后的故事都让它滚粗负分去吧。[视频：内地四大花旦博弈大银幕 明争暗斗暗潮汹涌] 上一个 下一个 上一个 下一个";
        compareNewsCategory(content);
    }

    /**
     * 加载所有分类正文本词-词频
     * @param categoryId
     */
    public static void loadCategoryNbcPositiveWords(Integer categoryId){
        try{
            Map<String,Integer> nbcPositiveMap = new HashMap<>();
            String categoryWordFreFile = wordsFileDir + CommonParams.CATEGORY_WORD_FEATURE_IG_PRIFIX+categoryId+".dat";
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
            nbcWordsMap.setCategoryName(categoryMap1.get(categoryId));
            nbcWordsMap.setCategoryId(categoryId);
            nbcWordsMap.setNbcPositiveMap(nbcPositiveMap);
            nbcWordsMap.setPositiveDocsNum(docNums);
            allCategoryWordList.add(nbcWordsMap);
        }catch(Exception err){
            logger.error("加载分类正文本词-词频异常，categoryId="+categoryId);
        }

    }


    /**
     * 加载所有分类负文本词-词频
     */
    public static void loadCategoryNbcNegativeWords(){
        try{
            for(int i = 0;i < allCategoryWordList.size();i++){
                NbcWordsMap nbcWordsMap = allCategoryWordList.get(i);
                Map<String,Integer> nbcNegativeMap = new HashMap<>();
                Integer negativeDocsNum = 0;
                for(int j = 0; j < allCategoryWordList.size();j++){
                    NbcWordsMap nbcWordsMap1 = allCategoryWordList.get(j);
                    if(nbcWordsMap.getCategoryId() == nbcWordsMap1.getCategoryId()){
                        continue;
                    }
                    negativeDocsNum += nbcWordsMap1.getPositiveDocsNum();
                    Map<String,Integer> nbcPositiveMap = nbcWordsMap1.getNbcPositiveMap();
                    for (Iterator iterator = nbcPositiveMap.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String word = (String) entry.getKey();
                        if(nbcNegativeMap.containsKey(word)){
                            nbcNegativeMap.put(word,nbcNegativeMap.get(word)+nbcPositiveMap.get(word));
                        }else{
                            nbcNegativeMap.put(word,nbcPositiveMap.get(word));
                        }
                    }
                }
                nbcWordsMap.setNbcNegativeMap(nbcNegativeMap);
                nbcWordsMap.setNegativeDocsNum(negativeDocsNum);
            }
        }catch(Exception err){
            logger.error("加载分类负文本词-词频异常");
        }

    }

    /**
     * 比较新闻跟哪个分类最接近
     * @return
     */
    public static Integer compareNewsCategory(String content){
        content="广东省东莞市公安局官方微博截图 　　中新网12月2日电 据广东省东莞市公安局官方微博消息，东莞市公安局今日召开新闻发布会，通报2016年10月27日，东莞市长安镇发生一男子追砸运钞车被押运员开枪击中致死案的侦办情况。经东莞警方深入侦查，在死者黄某第一次打砸运钞车前，双方行进路线没有交集，排除运钞车与黄某发生过碰撞、摩擦的情况。";
        Integer nearCategoryId = null;
        for(int i = 0;i < allCategoryWordList.size();i++){
            NbcWordsMap nbcWordsMap = allCategoryWordList.get(i);
//            Map<String,Integer> newsWordMap = choutiSegment.segmentFrequency(content);
            Map<String,Integer> newsWordMap = choutiSegment.segmentTextRank(content,30);
            if(null == newsWordMap || newsWordMap.size() <= 0){
                return null;
            }
            logger.info("-----cagegoryName---------:"+categoryMap1.get(nbcWordsMap.getCategoryId()));
            NBClassifier classifier = new NBClassifier(nbcWordsMap.getNbcPositiveMap(),nbcWordsMap.getPositiveDocsNum(), nbcWordsMap.getNbcNegativeMap(), nbcWordsMap.getNegativeDocsNum());
            double[] classProb = classifier.classify(newsWordMap,true);
            if(classProb[0] > classProb[1]){
                nearCategoryId = nbcWordsMap.getCategoryId();
                if(null != nearCategoryId && nearCategoryId > 0){
                    logger.info("该内容属于分类:"+nearCategoryId);
                    break;
                }
            }
        }
        logger.info("改新闻属于分类:"+categoryMap1.get(nearCategoryId));
        return nearCategoryId;
    }
}
