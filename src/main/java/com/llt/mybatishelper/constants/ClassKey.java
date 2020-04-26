package com.llt.mybatishelper.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LILONGTAO
 * @date 2019-07-31
 */
@Getter
@AllArgsConstructor
public enum ClassKey {
    /**
     *  .auto 必选 包含该字段才会自动生成
     *
     *  .tableName xxx 可选 自定义表名,若不自定义使用类名下划线形式
     *
     *  .desc xxx 可选 自定义该表描述
     *
     *  .keyType xxx 可选 自定义主键类型，仅对基类id生效
     */
    AUTO(".auto"),
    TABLE_NAME(".tableName"),
    DESC(".desc"),
    KEY_TYPE(".keyType"),
    ;

    String code;
}
