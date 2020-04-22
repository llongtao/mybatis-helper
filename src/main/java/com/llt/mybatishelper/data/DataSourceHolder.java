package com.llt.mybatishelper.data;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class DataSourceHolder {

    private static DruidDataSource dataSource;

    public static void addDataSource(String driverClassName, String url, String username, String password) {
        if (dataSource == null) {
            try {
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.setDriverClassName(driverClassName);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                dataSource.setUrl(url);
                Properties properties = new Properties();
                properties.setProperty("useSSL", "false");
                properties.setProperty("allowPublicKeyRetrieval", "true");
                properties.setProperty("serverTimezone", "GMT+8");
                dataSource.setConnectProperties(properties);
                DataSourceHolder.dataSource = dataSource;
            } catch (Exception e) {
                throw new IllegalArgumentException("db配置不正确:" + e.getMessage());
            }
        }
    }

    public static Connection getConnection() {
        if (dataSource == null) {
            throw new IllegalArgumentException("未配置数据源");
        }
        Callable<Connection> callable = () -> dataSource.getConnection();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Connection> submit = executor.submit(callable);
        try {
            return submit.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            dataSource.close();
            dataSource = null;
            throw new RuntimeException("数据库连接超时");
        }
    }

    public static void clear() {
        if (dataSource != null) {
            try {
                dataSource.close();
                dataSource = null;
            } catch (Exception ignore) {
            }
        }
    }
}
