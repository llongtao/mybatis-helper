package com.llt.mybatishelper.demo.enums;

/**
 * @author LILONGTAO
 * @date 2020-09-28
 */
public enum Sex {
    male("M"),
    female("F"),
    unknown("X"),
    ;

    String code;

    Sex(String code) {
        this.code = code;
    }
}
