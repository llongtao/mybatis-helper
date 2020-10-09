package top.aexp.mybatishelper.core.utils;

/**
 * @author LILONGTAO
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final char SPACE = ' ';

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static String getValue(String key, String content) {
        if (content == null) {
            return null;
        }
        int i = content.indexOf(key);
        if (i < 0) {
            return null;
        }
        int end = content.indexOf("\n", i);
        int start = i + key.length();
        String value;
        if (end >= start) {
            value = content.substring(start, end);
        } else {
            value = content.substring(start);
        }
        return value.trim();
    }

    public static String transformUnderline(String str) {
        char[] chars = str.trim().toCharArray();
        int length = chars.length;
        StringBuilder sb = new StringBuilder();
        sb.append((chars[0] + "").toLowerCase());
        for (int i = 1; i < length; i++) {
            if (chars[i] >= 'A' && chars[i] <= 'Z') {
                sb.append("_").append((char) (chars[i] + 32));
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    public static String transformHump(String str) {
        char[] chars = str.trim().toLowerCase().toCharArray();
        int length = chars.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (chars[i] == '_') {
                i++;
                if (i < length) {
                    sb.append((char)(chars[i] - 32));
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {

        System.out.println("123\\456\\789".replace("\\", "."));

        System.out.println(getStringByDot("com.llt.aa.", 2));

        String s = transformUnderline(" aEntityField");
        System.out.println(s);
        System.out.println(getValue(".主键", "/**\n" +
                " * .主键\n" +
                " */\n" +
                "@Id\n" +
                "private Long id;"));
    }

    public static String getAfterDot(String str) {
        if (str == null) {
            return null;
        }
        int i = str.lastIndexOf(".");
        if (i < 0) {
            return str;
        }
        return str.substring(i + 1);
    }

    public static String getStringByDot(String str, int s) {
        int index = 0;
        for (int i = 0; i < s; i++) {

            index = str.indexOf(".", index + 1);
            if (index < 0) {
                break;
            }
        }
        if (index > 0) {
            return str.substring(0, index);
        } else {
            return str;
        }
    }

    public static String getByColonCount(String str, int s) {
        if (str == null) {
            return null;
        }
        String[] split = str.split(":");
        if (split.length > s && s >= 0) {
            return split[s];
        } else {
            return null;
        }
    }


    public static String getAfterString(String fullStr, String preStr) {
        int i = fullStr.indexOf(preStr);
        if (i < 0) {
            throw new IllegalArgumentException("未包含前缀:"+preStr);
        }
        return fullStr.substring(i);
    }

    public static String firstToLower(String str) {
        if (str == null) {
            return null;
        }
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        } else {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
    }
}
