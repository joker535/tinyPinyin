package com.guye.tinypinyin;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;


public class PinyinUtils {

    private static PinyinUtils      sInstance;

    private static short[]          sPinyinIndex;
    private final static String[]   PINYIN               = new String[] { "a", "ai", "an", "ang",
            "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao",
            "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "cen",
            "ceng", "cha", "chai", "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong",
            "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou",
            "cu", "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng",
            "di", "dia", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du", "duan", "dui",
            "dun", "duo", "e", "ei", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo",
            "fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong",
            "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha", "hai", "han",
            "hang", "hao", "he", "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan",
            "huang", "hui", "hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin",
            "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang", "kao",
            "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun",
            "kuo", "la", "lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian",
            "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "luan", "lun",
            "luo", "lv", "lve", "m", "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng",
            "mi", "mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai",
            "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ng", "ni", "nian", "niang", "niao",
            "nie", "nin", "ning", "niu", "none", "nong", "nou", "nu", "nuan", "nuo", "nv", "nve",
            "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian",
            "piao", "pie", "pin", "ping", "po", "pou", "pu", "qi", "qia", "qian", "qiang", "qiao",
            "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao",
            "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa",
            "sai", "san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang",
            "shao", "she", "shei", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan",
            "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun",
            "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie",
            "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang",
            "wei", "wen", "weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie", "xin",
            "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya", "yan", "yang", "yao", "ye",
            "yi", "yiao", "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za",
            "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan",
            "zhang", "zhao", "zhe", "zhei", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua",
            "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan",
            "zui", "zun", "zuo"                         };

    private static volatile boolean isLoad               = false;

    private static final char       SPECIAL_HANZI        = '\u3007';
    private static final String     SPECIAL_HANZI_PINYIN = "LING";

    private static final char       FIRST_CHINA          = '\u4E00';
    private static final char       LAST_CHINA           = '\u9FA5';

    public static class MatchedResult {
        public int start = -1;
        public int end   = -1;
    }

    private PinyinUtils() {
    }

    public static synchronized PinyinUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PinyinUtils();
        }
        loadData(context);
        return sInstance;
    }

    private static void loadData(Context context ) {
        InputStream input = null;
        DataInputStream dataInput = null;
        try {
            if (isLoad) {
                return;
            }
            input= context.getAssets().open("pinyinindex");
            dataInput = new DataInputStream(input);
            long length = dataInput.available() >> 1;
            sPinyinIndex = new short[(int) length];
            for (int i = 0; i < sPinyinIndex.length; i++) {
                sPinyinIndex[i] = dataInput.readShort();
            }
            isLoad = true;
        } catch (IOException e) {
            isLoad = false;
        } catch (Exception e) {
            isLoad = false;
        } finally {
            try {
                if (dataInput != null) {
                    dataInput.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private String getPinyin( char ch ) {
        if (!isLoad) {
            return "";
        }

        String pinyin = "";
        if (ch == SPECIAL_HANZI) {
            return SPECIAL_HANZI_PINYIN;
        }

        if (ch < FIRST_CHINA || ch > LAST_CHINA) {
            return String.valueOf(ch);
            // return null;
        }

        int pos = ch - FIRST_CHINA;
        pinyin = PINYIN[sPinyinIndex[pos]];
        if (pinyin == null) {
            pinyin = "";
        }

        return pinyin;
    }

    public String getPinyin( String s ) {
        if (isEmpty(s)) {
            return "";
        }
        if (!isLoad) {
            throw new IllegalStateException("pinyin index file not loaded");
        }
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            sb.append(getPinyin(c));
        }
        return sb.toString();
    }

    private boolean isEmpty( String s ) {
        if(s == null || s.length() == 0){
            return true;
        }
        return false;
    }

    public String[] getPinyinArray( String s ) {
        if (isEmpty(s)) {
            return new String[0];
        }
        if (!isLoad) {
            throw new IllegalStateException("pinyin index file not loaded");
        }
        String[] sb = new String[s.length()];
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            sb[i] = getPinyin(c);
        }
        return sb;
    }

}