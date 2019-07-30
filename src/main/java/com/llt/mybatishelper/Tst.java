package com.llt.mybatishelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tst {
    public static void main(String[] args) {
        String str = "/** aaa \n */";

        Pattern patt = Pattern.compile("^/\\*\\*([\\w\\W]*)\\*/$");
        Matcher matcher = patt.matcher(str);
        //System.out.println(split1[0]);
        if (matcher.find()) {
            System.out.println(1);
            System.out.println(matcher.group());
            int size = matcher.groupCount();
            for (int i = 0; i < size; i++) {
                System.out.println(matcher.group(i));
            }
        }
    }
}
