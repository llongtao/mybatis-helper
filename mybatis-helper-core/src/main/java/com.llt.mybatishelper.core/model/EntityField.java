package com.llt.mybatishelper.core.model;

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

    private Boolean isEnum;

    private Integer length;

    private String defaultValue;

    private Boolean nullable;

    private String description;


    public EntityField() {
    }

    public EntityField(EntityField item) {
        this.name = item.getName();
        this.columnName = item.getColumnName();
        this.type = item.getType();
        this.length = item.getLength();
        this.defaultValue = item.getDefaultValue();
        this.description = item.getDescription();
    }
}
