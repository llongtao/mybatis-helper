package com.llt.mybatishelper.core.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LILONGTAO
 * @date 2019-07-31
 */
@Getter
@AllArgsConstructor
public enum  FieldKey {

    /**
     *  .key 可选 表示该field为主键
     *  .column 可选 自定义列名,默认使用field名的下划线格式
     *  .jdbcType xxx 可选 自定义jdbcType
     *  .len 32 可选 自定义长度
     *  .desc xxx 可选 自定义字段描述
     *  .notNull 可选 非空
     *  .default xxx 可选 默认值
     *  .ignore 可选 忽略该字段
     *  .enum 可选 表示枚举,否则枚举类不生成
     */
    KEY(".key"),
    COLUMN(".column"),
    JDBC_TYPE(".jdbcType"),
    LEN(".len"),
    DESC(".desc"),
    NO_NULL(".notNull"),
    DEFAULT(".default"),
    IGNORE(".ignore"),
    ENUM(".enum"),
    ;

    String code;
}
