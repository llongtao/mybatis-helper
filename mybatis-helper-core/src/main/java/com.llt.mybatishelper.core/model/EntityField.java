package com.llt.mybatishelper.core.model;

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

    private Boolean isEnum;

    private String length;

    private String defaultValue;

    private Boolean nullable;

    private String description;



    public EntityField(EntityField item) {
        this.name = item.getName();
        this.columnName = item.getColumnName();
        this.type = item.getType();
        this.length = item.getLength();
        this.defaultValue = item.getDefaultValue();
        this.description = item.getDescription();
    }
}
