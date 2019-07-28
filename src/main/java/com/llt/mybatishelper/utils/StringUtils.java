package com.llt.mybatishelper.utils;

public class StringUtils {
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
        if (end >= start) {
            return content.substring(start, end);
        } else {
            return content.substring(start);
        }

    }

    public static String transformUnderline(String str) {
        char[] chars = str.toCharArray();
        int length = chars.length;
        StringBuilder sb = new StringBuilder();
        sb.append((chars[0]+"").toLowerCase());
        for (int i = 1; i < length; i++) {
            if (chars[i] >= 'A' && chars[i] <= 'Z') {
                sb.append("_").append((char)(chars[i] + 32));
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String s = transformUnderline("aEntityField");
        System.out.println(s);
        System.out.println(getValue(".主键", "/**\n" +
                " * .主键\n" +
                " */\n" +
                "@Id\n" +
                "private Long id;"));
    }
}
