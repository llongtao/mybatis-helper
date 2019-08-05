package com.llt.mybatishelper.service.Impl;

import com.llt.mybatishelper.data.DataSourceHolder;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.service.BaseMybatisHelper;
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
    protected void updateTable(EntityModel entityModel, String dataSourceUrl) {

        Connection connection = DataSourceHolder.getConnection(dataSourceUrl);
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
