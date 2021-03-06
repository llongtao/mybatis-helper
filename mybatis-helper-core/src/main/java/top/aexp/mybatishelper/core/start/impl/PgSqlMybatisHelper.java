package top.aexp.mybatishelper.core.start.impl;

import top.aexp.mybatishelper.core.exception.EntityBuildException;
import top.aexp.mybatishelper.core.model.EntityField;
import top.aexp.mybatishelper.core.model.EntityModel;
import top.aexp.mybatishelper.core.start.BaseMybatisHelper;
import top.aexp.mybatishelper.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import top.aexp.mybatishelper.core.constants.Constants;

import java.sql.*;
import java.util.*;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */

@Slf4j
public class PgSqlMybatisHelper extends BaseMybatisHelper {

    private static final Map<JDBCType, String> columnTypeMap;

    static {
        columnTypeMap = new HashMap<>();
        columnTypeMap.put(JDBCType.INTEGER, "INT");
        columnTypeMap.put(JDBCType.SMALLINT, "SMALLINT");
        columnTypeMap.put(JDBCType.TINYINT, "SMALLINT");
        columnTypeMap.put(JDBCType.BIGINT, "BIGINT");
        columnTypeMap.put(JDBCType.BIT, "BIT");
        columnTypeMap.put(JDBCType.BOOLEAN, "BOOLEAN");
        columnTypeMap.put(JDBCType.FLOAT, "REAL");
        columnTypeMap.put(JDBCType.DOUBLE, "DOUBLE PRECISION");
        columnTypeMap.put(JDBCType.DECIMAL, "DECIMAL");
        columnTypeMap.put(JDBCType.CHAR, "CHAR");
        columnTypeMap.put(JDBCType.DATE, "DATE");
        columnTypeMap.put(JDBCType.TIME, "TIME");
        columnTypeMap.put(JDBCType.TIMESTAMP, "TIMESTAMP");
        columnTypeMap.put(JDBCType.BLOB, "BYTEA");
        columnTypeMap.put(JDBCType.CLOB, "TEXT");
        columnTypeMap.put(JDBCType.NUMERIC, "NUMERIC");
        columnTypeMap.put(JDBCType.REAL, "REAL");
        columnTypeMap.put(JDBCType.VARCHAR, "VARCHAR");
        columnTypeMap.put(JDBCType.LONGVARCHAR, "TEXT");

    }

    @Override
    protected String getDbUrl(String baseDbUrl) {
        return "jdbc:postgresql://" + baseDbUrl;
    }

    @Override
    protected String buildModifyColumnSql(EntityModel entityModel, EntityField modifyColumn) {
//        return "ALTER TABLE " +
//                "\"" + entityModel.getTableName() + "\" " +
//                "MODIFY " + getColumnDefine(modifyColumn) + ";";
        return null;
    }

    @Override
    protected String buildDropColumnSql(EntityModel entityModel, String dropColumn) {

        return "ALTER TABLE " + "\"" + entityModel.getTableName() + "\" " +
                "DROP COLUMN " + "\"" + dropColumn + "\"" + ";";
    }

    @Override
    protected String buildAddColumnSql(EntityModel entityModel, EntityField addColumn) {
        return "ALTER TABLE " +
                "\"" + entityModel.getTableName() + "\" " +
                "ADD " +
                getColumnDefine(addColumn) + ";";
    }

    @Override
    protected String getDropTableSql(String schema, String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    @Override
    protected String getDbDriverClassName() {
        return Constants.PGSQL_DRIVER;
    }

    @Override
    protected List<String> buildCreateSql(EntityModel entityModel) {

        List<String> sqlList = new ArrayList<>();
        List<String> commentList = new ArrayList<>();
        List<EntityField> primaryKeyList = entityModel.getPrimaryKeyList();

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append("\"").append(entityModel.getTableName()).append("\"")
                .append("(");
        if (primaryKeyList != null) {
            primaryKeyList.forEach(primaryKey -> {
                String define = primaryKey.getDefine();
                String description = primaryKey.getDescription();
                if (description != null) {
                    commentList.add("COMMENT ON COLUMN \""+ entityModel.getTableName()+"\".\""+ primaryKey.getColumnName()+"\" IS '"+description+"';");
                }
                if (StringUtils.isNotBlank(define)) {
                    sb.append(" ").append(define).append(" ,");
                } else {
                    boolean incr = Objects.equals(primaryKey.getIncr(), true);
                    sb.append("\"").append(primaryKey.getColumnName()).append("\" ");
                    if (!incr) {
                        sb.append(getColumnType(primaryKey.getJdbcType(), primaryKey.getLength())).append(" ");
                    }
                    if (!primaryKey.getNullable()) {
                        sb.append("NOT NULL ");
                    }
                    if (incr) {
                        sb.append("SERIAL ");
                    }
                    sb.append(",");
                }
            });
        }

        entityModel.getColumnList().forEach(column -> sb.append(getColumnDefine(column)).append(","));
        entityModel.getColumnList().stream().filter(item->item.getDescription()!=null).forEach(column->
                commentList.add("COMMENT ON COLUMN \""+ entityModel.getTableName()+"\".\""+ column.getColumnName()+"\" IS '"+column.getDescription()+"';"));
        if (primaryKeyList != null && primaryKeyList.size() > 0) {
            sb.append("PRIMARY KEY ( ");
            primaryKeyList.forEach(primaryKey -> sb.append("\"").append(primaryKey.getColumnName()).append("\" ,"));
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        } else {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(");");
        if (entityModel.getDescription() != null) {
            commentList.add("COMMENT ON TABLE \""+ entityModel.getTableName()+"\" IS '"+entityModel.getDescription()+"';");
        }
        sqlList.add(sb.toString());
        sqlList.addAll(commentList);
        return sqlList;
    }


    @Override
    protected String getTableExistColumnSql(String schema, String tableName) {
        return "select COLUMN_NAME from information_schema.columns where table_catalog='" + schema + "' and table_name='" + tableName + "';";
    }

    @Override
    protected Document buildXmlDoc(EntityModel entityModel) {
        return xmlBuilder.build(entityModel, "\"");
    }

    private String buildCreate(EntityModel entityModel) {
        //主键只有一个且为数字的时候,自动设置自增
        List<EntityField> primaryKeyList = entityModel.getPrimaryKeyList();
        boolean useAutoIncrement = primaryKeyList != null && primaryKeyList.size() == 1
                && (JDBCType.INTEGER == primaryKeyList.get(0).getJdbcType() || JDBCType.BIGINT == primaryKeyList.get(0).getJdbcType());


        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append("\"").append(entityModel.getTableName()).append("\"")
                .append("(");
        if (primaryKeyList != null) {
            primaryKeyList.forEach(primaryKey -> {
                String description = primaryKey.getDescription();
                sb.append("\"").append(primaryKey.getColumnName()).append("\" ").append(getColumnType(primaryKey.getJdbcType(), primaryKey.getLength())).append(" ");
                if (!primaryKey.getNullable()) {
                    sb.append("NOT NULL ");
                }
                if (useAutoIncrement) {
                    sb.append("AUTO_INCREMENT ");
                }

                if (description != null) {
                    sb.append("COMMENT '").append(description).append("' ");
                }
                sb.append(",");
            });
        }

        entityModel.getColumnList().forEach(column -> sb.append(getColumnDefine(column)).append(","));
        if (primaryKeyList != null && primaryKeyList.size() > 0) {
            sb.append("PRIMARY KEY ( ");
            primaryKeyList.forEach(primaryKey -> sb.append("\"").append(primaryKey.getColumnName()).append("\" ,"));
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
        if (StringUtils.isNotBlank(column.getDefine())) {
            return column.getDefine();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(column.getColumnName()).append("\" ")
                .append(getColumnType(column.getJdbcType(), column.getLength())).append(" ");
        Boolean nullable = column.getNullable();
        String defaultValue = column.getDefaultValue();
        if (Objects.equals(nullable, false)) {
            sb.append("NOT NULL ");
            if (!StringUtils.isBlank(defaultValue)) {
                sb.append("DEFAULT '").append(defaultValue).append("' ");
            } else {
                sb.append("DEFAULT '' ");
            }
        } else {
            if (!StringUtils.isBlank(defaultValue)) {
                sb.append("DEFAULT '").append(defaultValue).append("' ");
            } else {
                sb.append("NULL DEFAULT NULL ");
            }
        }

        return sb.toString();
    }

    private String getColumnType(JDBCType jdbcType, String len) {
        String columnType = columnTypeMap.get(jdbcType);
        if (columnType == null) {
            throw new EntityBuildException("pgsql不支持jdbcType:" + jdbcType);
        }
        if (!StringUtils.isBlank(len) && !"0".equals(len)) {
            return columnType + "(" + len + ")";
        }
        return columnType;
    }

}
