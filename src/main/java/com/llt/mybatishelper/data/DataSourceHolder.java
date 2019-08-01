package com.llt.mybatishelper.data;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class DataSourceHolder {

    private static Map<String, DruidDataSource> DATA_SOURCE_MAP = new HashMap<>();

    public static void addDataSource(String driverClassName,String url, String username,String password) {
        DruidDataSource druidDataSource = DATA_SOURCE_MAP.get(url);
        if (druidDataSource == null) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(driverClassName);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setUrl(url);
            DATA_SOURCE_MAP.put(url,dataSource);
        }
    }

    public static Connection getConnection(String url) {
        DruidDataSource druidDataSource = DATA_SOURCE_MAP.get(url);
        if (druidDataSource == null) {
            throw new IllegalArgumentException("未配置数据源");
        }
        try {
            return druidDataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
