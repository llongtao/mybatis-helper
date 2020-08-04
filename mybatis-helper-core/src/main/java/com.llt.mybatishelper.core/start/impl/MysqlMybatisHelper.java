package com.llt.mybatishelper.core.start.impl;

import com.alibaba.fastjson.JSON;
import com.llt.mybatishelper.core.builder.xml.DefaultXmlBuilder;
import com.llt.mybatishelper.core.data.DataSourceHolder;
import com.llt.mybatishelper.core.log.ResultLog;
import com.llt.mybatishelper.core.model.EntityField;
import com.llt.mybatishelper.core.model.EntityModel;
import com.llt.mybatishelper.core.start.BaseMybatisHelper;
import com.llt.mybatishelper.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.llt.mybatishelper.core.constants.Constants.DOT;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */
@Slf4j
public class MysqlMybatisHelper extends BaseMybatisHelper {

    @Override
    protected void updateTable(EntityModel entityModel, Connection connection)  {

        String tableName = entityModel.getTableName();
        Set<String> columnSet = new HashSet<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = '" + tableName + "'");
            while (resultSet.next()) {
                columnSet.add(resultSet.getString(1).trim().toLowerCase());
            }
            ResultLog.info("获取"+entityModel.getTableName()+"列:"+ JSON.toJSONString(columnSet));
        } catch (SQLException e) {
            ResultLog.warn("获取"+entityModel.getTableName()+"列失败:"+e.getMessage());
            e.printStackTrace();
        }

        String sql = toSql(entityModel,columnSet);

        if (sql != null) {
            log.info("sql:"+sql);
            ResultLog.info("sql:"+sql);
            try {
                Statement statement = connection.createStatement();
                statement.execute(sql);
                ResultLog.info("sql执行成功");
            } catch (SQLException e) {
                ResultLog.warn("sql失败:"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Document buildXmlDoc(EntityModel entityModel) {
        return DefaultXmlBuilder.build(entityModel,"`");
    }

    private String toSql(EntityModel entityModel, Set<String> existsSet) {
        if (existsSet == null || existsSet.size() == 0) {
            return buildCreate(entityModel);
        } else {
            return buildAlter(entityModel,existsSet);
        }
    }

    private String buildAlter(EntityModel entityModel, Set<String> existsSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ")
                .append("`").append(entityModel.getTableName()).append("` ")
                .append("ADD (");
        List<EntityField> entityFieldList = new ArrayList<>();
        entityFieldList.addAll(entityModel.getPrimaryKeyList());
        entityFieldList.addAll(entityModel.getColumnList());
        entityFieldList.forEach(column -> {
            if (!existsSet.contains(column.getColumnName())) {
                sb.append("`").append(column.getColumnName()).append("` ").append(column.getFullJdbcType()).append(" ");
                if (!column.getNullable()) {
                    sb.append("NOT NULL ");
                }else {
                    sb.append("NULL DEFAULT NULL ");
                }
                sb.append(",");
            }

        });
        if (DOT!= sb.charAt(sb.length() - 1)) {
            return null;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");
        return sb.toString();
    }

    private String buildCreate(EntityModel entityModel) {
        //主键只有一个且为数字的时候,自动设置自增
        List<EntityField> primaryKeyList = entityModel.getPrimaryKeyList();
        boolean useAutoIncrement = primaryKeyList != null && primaryKeyList.size() == 1
                && (JDBCType.INTEGER == primaryKeyList.get(0).getJdbcType() || JDBCType.BIGINT == primaryKeyList.get(0).getJdbcType());


        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append("`").append(entityModel.getTableName()).append("`")
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

        entityModel.getColumnList().forEach(column -> {
            sb.append("`").append(column.getColumnName()).append("` ").append(column.getFullJdbcType()).append(" ");
            if (!column.getNullable()) {
                sb.append("NOT NULL ");
            }
            String defaultValue = column.getDefaultValue();
            if (defaultValue != null) {
                sb.append("DEFAULT '").append(defaultValue).append("' ");
            }else {
                sb.append("NULL DEFAULT NULL ");
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
        if (entityModel.getDescription() != null) {
            sb.append(") COMMENT = '").append(entityModel.getDescription()).append("';");
        } else {
            sb.append(");");
        }

        return sb.toString();

    }

}
