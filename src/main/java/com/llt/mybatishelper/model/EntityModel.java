package com.llt.mybatishelper.model;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

@Data
public class EntityModel {

    private String packageName;

    private String mapperClassName;

    private String entityClassName;

    private String tableName;

    private String description;

    private List<EntityField> primaryKeyList;

    private List<EntityField> columnList;

    public String toSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append(tableName)
                .append("(");
        primaryKeyList.forEach(primaryKey->{
            sb.append(primaryKey.getColumnName()).append(" ").append(primaryKey.getJdbcType()).append(" ");
            if (!primaryKey.isNullable() ) {
                sb.append("NOT NULL ");
            }
            sb.append("PRIMARY KEY ,");
        });

        columnList.forEach(primaryKey->{
            sb.append(primaryKey.getColumnName()).append(" ").append(primaryKey.getJdbcType()).append(" ");
            if (!primaryKey.isNullable() ) {
                sb.append("NOT NULL ");
            }
            sb.append(" ,");
        });
        sb.deleteCharAt(sb.length()-1);
        sb.append(");");
        return sb.toString();

    }

}
