package com.llt.mybatishelper.core.data;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.llt.mybatishelper.core.exception.MybatisHelperException;
import com.llt.mybatishelper.core.log.ResultLog;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
@Slf4j
public class DataSourceHolder {

    private static final Properties PROPERTIES = new Properties();
    static {
        PROPERTIES.setProperty("useSSL", "false");
        PROPERTIES.setProperty("allowPublicKeyRetrieval", "true");
        PROPERTIES.setProperty("serverTimezone", "GMT+8");
    }


    private static DruidDataSource dataSource;

    private static  Connection connection ;

    public static void addDataSource(String driverClassName, String url, String username, String password) {
        if (dataSource == null) {
            try {
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.setDriverClassName(driverClassName);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                dataSource.setUrl(url);
                dataSource.setConnectProperties(PROPERTIES);
                DataSourceHolder.dataSource = dataSource;
            } catch (Exception e) {
                log.error("db配置不正确",e);
                throw new MybatisHelperException("db配置不正确:" + e.getMessage());
            }
        }
    }

    public static Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException e) {
            ResultLog.warn("检查数据库连接失败:"+e.getMessage());
            log.error("检查数据库连接失败",e);
        }
        if (dataSource == null) {
            ResultLog.error("未配置数据源");
            throw new MybatisHelperException("未配置数据源");
        }

        Callable<Connection> callable = () -> {
            log.info("getConn");
            ResultLog.info("正在获取数据库连接");
            DruidPooledConnection conn = dataSource.getConnection();
            log.info("getConnSuccess:{}",conn);
            ResultLog.info("获取数据库连接成功");
            connection = conn;
            return conn;
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Connection> submit = executor.submit(callable);
        try {
            return submit.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dataSource.close();
            dataSource = null;
            log.error("数据库连接超时",e);
            ResultLog.error("数据库连接超时:"+e.getMessage());
            throw new MybatisHelperException("数据库连接超时");
        }
    }

    public static void clear() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception e) {
                log.error("清理数据库连接异常",e);
            }
            dataSource = null;
        }
    }
}
