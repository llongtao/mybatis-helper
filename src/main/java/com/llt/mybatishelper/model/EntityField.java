package com.llt.mybatishelper.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityField {

    private String name;
    private String columnName;
    private String type;
    private String jdbcType;
    private String fullJdbcType;
    private String defaultValue;
    private boolean nullable;
    private String description;

}
