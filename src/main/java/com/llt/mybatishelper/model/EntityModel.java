package com.llt.mybatishelper.model;

import lombok.Data;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author LILONGTAO
 */
@Data
public class EntityModel {

    private String packageName;

    private String mapperClassName;

    private String mapperPackage;

    private String baseMapperClassName;

    private String baseMapperPackage;

    private String mapperName;

    private String baseMapperName;

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
                sb.append("`").append(column.getColumnName()).append("` ").append(column.getFullJdbcType()).append(" ");
                if (!column.getNullable()) {
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
        //主键只有一个且为数字的时候,自动设置自增
        boolean useAutoIncrement = primaryKeyList != null && primaryKeyList.size() == 1
                && (JDBCType.INTEGER == primaryKeyList.get(0).getJdbcType() || JDBCType.BIGINT == primaryKeyList.get(0).getJdbcType());


        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append("`").append(tableName).append("`")
                .append("(");
        if (primaryKeyList != null) {
            primaryKeyList.forEach(primaryKey -> {
                String description = primaryKey.getDescription();
                sb.append("`").append(primaryKey.getColumnName()).append("` ").append(primaryKey.getFullJdbcType()).append(" ");
                if (!primaryKey.getNullable()) {
                    sb.append("NOT NULL ");
                }
                if (useAutoIncrement ) {
                    sb.append("AUTO_INCREMENT ");
                }

                if (description != null) {
                    sb.append("COMMENT '").append(description).append("' ");
                }
                sb.append(",");
            });
        }

        columnList.forEach(column -> {
            sb.append("`").append(column.getColumnName()).append("` ").append(column.getFullJdbcType()).append(" ");
            if (!column.getNullable()) {
                sb.append("NOT NULL ");
            }
            String defaultValue = column.getDefaultValue();
            if (defaultValue != null) {
                sb.append("DEFAULT '").append(defaultValue).append("' ");
            }
            String description = column.getDescription();
            if (description != null) {
                sb.append("COMMENT '").append(description).append("' ");
            }
            sb.append(",");
        });
        if (primaryKeyList != null && primaryKeyList.size() > 0) {
            sb.append("PRIMARY KEY ( ");
            primaryKeyList.forEach(primaryKey -> sb.append("`").append(primaryKey.getColumnName()).append("` ,"));
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        } else {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (description != null) {
            sb.append(") COMMENT = '").append(description).append("';");
        } else {
            sb.append(");");
        }

        return sb.toString();

    }

    public void setExistsColumn(Set<String> columnSet) {
        existsSet = columnSet;
    }
}
