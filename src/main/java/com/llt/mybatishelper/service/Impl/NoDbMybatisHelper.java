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
public class NoDbMybatisHelper extends BaseMybatisHelper {

    @Override
    protected void updateTable(EntityModel entityModel, String dataSourceUrl) {
        //不对数据库做改动
    }
}
