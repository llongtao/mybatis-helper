package com.llt.mybatishelper.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.JDBCType;

/**
 * @author LILONGTAO
 */
@Data
@AllArgsConstructor
public class EntityField {

    private String name;

    private String columnName;

    private String type;

    private JDBCType jdbcType;

    private String fullJdbcType;

    private String defaultValue;

    private boolean nullable;

    private String description;

}
