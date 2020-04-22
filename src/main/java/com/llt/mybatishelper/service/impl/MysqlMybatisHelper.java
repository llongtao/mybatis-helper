package com.llt.mybatishelper.service.impl;

import com.llt.mybatishelper.data.DataSourceHolder;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.service.BaseMybatisHelper;
import com.llt.mybatishelper.utils.StringUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */
public class MysqlMybatisHelper extends BaseMybatisHelper {

    @Override
    protected void updateTable(EntityModel entityModel, String schema)  {
        if (StringUtils.isEmpty(schema)) {
            throw new RuntimeException("若生成表结构数据库名不能为空");
        }
        Connection connection = DataSourceHolder.getConnection();
        try {
            connection.setCatalog(schema);
        } catch (SQLException e) {
            throw new RuntimeException("切库异常:"+e.getMessage(),e);
        }
        String tableName = entityModel.getTableName();
        Set<String> columnSet = new HashSet<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = '" + tableName + "'");
            while (resultSet.next()) {
                columnSet.add(resultSet.getString(1).trim().toLowerCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        entityModel.setExistsColumn(columnSet);

        String sql = entityModel.toSql();

        if (sql != null) {
            System.out.println(sql);
            try {
                Statement statement = connection.createStatement();
                statement.execute(entityModel.toSql());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
