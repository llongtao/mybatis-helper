package com.llt.mybatishelper.core.start.impl;

import com.llt.mybatishelper.core.exception.EntityBuildException;
import com.llt.mybatishelper.core.model.EntityField;
import com.llt.mybatishelper.core.model.EntityModel;
import com.llt.mybatishelper.core.start.BaseMybatisHelper;
import com.llt.mybatishelper.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;

import java.sql.*;
import java.util.*;

import static com.llt.mybatishelper.core.constants.Constants.DOT;
import static com.llt.mybatishelper.core.constants.Constants.MYSQL_DRIVER;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */
@Slf4j
public class MysqlMybatisHelper extends BaseMybatisHelper {


    private static final Map<JDBCType, String> columnTypeMap;

    static {
        columnTypeMap = new HashMap<>();
        columnTypeMap.put(JDBCType.BIGINT, "BIGINT");
        columnTypeMap.put(JDBCType.BIT, "BIT");
        columnTypeMap.put(JDBCType.BLOB, "BLOB");
        columnTypeMap.put(JDBCType.CHAR, "CHAR");
        columnTypeMap.put(JDBCType.CLOB, "TEXT");
        columnTypeMap.put(JDBCType.DATE, "DATE");
        columnTypeMap.put(JDBCType.DECIMAL, "DECIMAL");
        columnTypeMap.put(JDBCType.DOUBLE, "DOUBLE");
        columnTypeMap.put(JDBCType.FLOAT, "FLOAT");
        columnTypeMap.put(JDBCType.INTEGER, "INTEGER");
        columnTypeMap.put(JDBCType.NUMERIC, "NUMERIC");
        columnTypeMap.put(JDBCType.REAL, "REAL");
        columnTypeMap.put(JDBCType.SMALLINT, "SMALLINT");
        columnTypeMap.put(JDBCType.TIME, "TIME");
        columnTypeMap.put(JDBCType.TIMESTAMP, "TIMESTAMP");
        columnTypeMap.put(JDBCType.TINYINT, "TINYINT");
        columnTypeMap.put(JDBCType.VARCHAR, "VARCHAR");
        columnTypeMap.put(JDBCType.LONGVARCHAR, "TEXT");

    }


    @Override
    protected String getTableExistColumnSql(String schema, String tableName) {
        return "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = '" + tableName + "'";
    }

    @Override
    protected Document buildXmlDoc(EntityModel entityModel) {
        return xmlBuilder.build(entityModel, "`");
    }



    @Override
    protected String buildModifyColumnSql(EntityModel entityModel, List<EntityField> modifyColumns) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ")
                .append("`").append(entityModel.getTableName()).append("` ");
        modifyColumns.forEach(column -> sb.append("MODIFY COLUMN ").append(getColumnDefine(column)).append(","));
        if (DOT != sb.charAt(sb.length() - 1)) {
            return null;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }

    @Override
    protected String buildDropColumnSql(EntityModel entityModel, Set<String> dropColumnSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append("`").append(entityModel.getTableName()).append("` ");
        dropColumnSet.forEach(column -> sb.append("DROP COLUMN ").append("`").append(column).append("`").append(","));
        if (DOT != sb.charAt(sb.length() - 1)) {
            return null;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }

    @Override
    protected String buildAddColumnSql(EntityModel entityModel, List<EntityField> addColumns) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ")
                .append("`").append(entityModel.getTableName()).append("` ")
                .append("ADD (");
        addColumns.forEach(column -> sb.append(getColumnDefine(column)).append(","));

        if (DOT != sb.charAt(sb.length() - 1)) {
            return null;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");
        return sb.toString();
    }


    @Override
    protected String getDropTableSql(String schema, String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    @Override
    protected String getDbDriverClassName() {
        return MYSQL_DRIVER;
    }

    @Override
    protected String buildCreateSql(EntityModel entityModel) {
        //主键只有一个且为数字的时候,自动设置自增
        List<EntityField> primaryKeyList = entityModel.getPrimaryKeyList();

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append("`").append(entityModel.getTableName()).append("`")
                .append("(");
        if (primaryKeyList != null) {
            primaryKeyList.forEach(primaryKey -> {
                String define = primaryKey.getDefine();
                if (StringUtils.isNotEmpty(define)) {
                    sb.append(" ").append(define).append(" ,");
                }else {
                    String description = primaryKey.getDescription();
                    sb.append("`").append(primaryKey.getColumnName()).append("` ")
                            .append(getColumnType(primaryKey.getJdbcType(), primaryKey.getLength())).append(" ");
                    if (!primaryKey.getNullable()) {
                        sb.append("NOT NULL ");
                    }
                    if (Objects.equals(primaryKey.getIncr(),true)) {
                        sb.append("AUTO_INCREMENT ");
                    }
                    if (description != null) {
                        sb.append("COMMENT '").append(description).append("' ");
                    }
                    sb.append(",");
                }
            });
        }

        entityModel.getColumnList().forEach(column -> {
            sb.append(getColumnDefine(column));
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
        if (entityModel.getDescription() != null) {
            sb.append(") COMMENT = '").append(entityModel.getDescription()).append("';");
        } else {
            sb.append(");");
        }

        return sb.toString();

    }

    private String getColumnDefine(EntityField column) {
        if (StringUtils.isNotBlank(column.getDefine()) ) {
            return column.getDefine();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(column.getColumnName()).append("` ")
                .append(getColumnType(column.getJdbcType(), column.getLength())).append(" ");
        Boolean nullable = column.getNullable();
        String defaultValue = column.getDefaultValue();
        if (Objects.equals(nullable,false)) {
            sb.append("NOT NULL ");
            if (!StringUtils.isBlank(defaultValue)) {
                sb.append("DEFAULT '").append(defaultValue).append("' ");
            } else {
                sb.append("DEFAULT '' ");
            }
        }else {
            if (!StringUtils.isBlank(defaultValue)) {
                sb.append("DEFAULT '").append(defaultValue).append("' ");
            } else {
                sb.append("NULL DEFAULT NULL ");
            }
        }
        String description = column.getDescription();
        if (description != null) {
            sb.append("COMMENT '").append(description).append("' ");
        }
        return sb.toString();
    }

    private String getColumnType(JDBCType jdbcType, String len) {
        String columnType = columnTypeMap.get(jdbcType);
        if (columnType == null) {
            throw new EntityBuildException("mysql不支持jdbcType:" + jdbcType);
        }
        if (!StringUtils.isBlank(len) && !"0".equals(len)) {
            return columnType + "(" + len + ")";
        }
        return columnType;
    }
}
