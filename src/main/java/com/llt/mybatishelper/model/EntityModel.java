package com.llt.mybatishelper.model;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class EntityModel {

    private String packageName;

    private String mapperClassName;

    private String mapperName;

    private String entityClassName;

    private String entityName;

    private String tableName;

    private String description;

    private List<EntityField> primaryKeyList;

    private List<EntityField> columnList;

    private boolean isNew = true;

    private Set<String> existsSet;

    public String toSql() {
        if (existsSet == null || existsSet.size() == 0) {
            return buildCreate();
        } else {
            return buildAlter();
        }
    }

    private String buildAlter() {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ")
                .append("`").append(tableName).append("` ")
                .append("ADD (");
        List<EntityField> entityFieldList = new ArrayList<>();
        entityFieldList.addAll(primaryKeyList);
        entityFieldList.addAll(columnList);
        entityFieldList.forEach(column -> {
            if (!existsSet.contains(column.getColumnName())) {
                sb.append("`").append(column.getColumnName()).append("` ").append(column.getJdbcType()).append(" ");
                if (!column.isNullable()) {
                    sb.append("NOT NULL ");
                }
                sb.append(",");
            }

        });
        if (44 != sb.charAt(sb.length() - 1)) {
            return null;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");
        return sb.toString();
    }

    private String buildCreate() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append("`").append(tableName).append("`")
                .append("(");
        List<EntityField> entityFieldList = new ArrayList<>();
        entityFieldList.addAll(columnList);
        entityFieldList.addAll(primaryKeyList);
        entityFieldList.forEach(column -> {
            sb.append("`").append(column.getColumnName()).append("` ").append(column.getJdbcType()).append(" ");
            if (!column.isNullable()) {
                sb.append("NOT NULL ");
            }
            sb.append(",");
        });
        if (primaryKeyList != null && primaryKeyList.size() > 0) {
            sb.append("PRIMARY KEY ( ");
            primaryKeyList.forEach(primaryKey -> {
                sb.append("`").append(primaryKey.getColumnName()).append("` ,");
            });
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        } else {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(");");
        return sb.toString();

    }

    public void setExistsColumn(Set<String> columnSet) {
        existsSet = columnSet;
    }
}
