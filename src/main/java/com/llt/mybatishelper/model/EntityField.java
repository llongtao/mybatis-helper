package com.llt.mybatishelper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.JDBCType;

/**
 * @author LILONGTAO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityField {

    private String name;

    private String columnName;

    private String type;

    private JDBCType jdbcType;

    private String fullJdbcType;

    private Integer length;

    private String defaultValue;

    private Boolean nullable;

    private String description;

}
