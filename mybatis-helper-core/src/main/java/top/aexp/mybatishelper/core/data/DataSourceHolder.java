package top.aexp.mybatishelper.core.data;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import top.aexp.mybatishelper.core.exception.MybatisHelperException;
import top.aexp.mybatishelper.core.log.ResultLog;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
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
        PROPERTIES.setProperty("useUnicode", "true");
        PROPERTIES.setProperty("characterEncoding", "UTF-8");

    }

    private static DruidDataSource dataSource;

    private static Map<String,Connection> connectionMap = new HashMap<>();

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

    public static Connection getConnection(String catalog) {
        Connection connection = connectionMap.get(catalog);
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
        DruidDataSource catalogDataSource = new DruidDataSource();
        catalogDataSource.setDriverClassName(dataSource.getDriverClassName());
        catalogDataSource.setUsername(dataSource.getUsername());
        catalogDataSource.setPassword(dataSource.getPassword());
        catalogDataSource.setUrl(dataSource.getUrl()+"/"+catalog);
        catalogDataSource.setConnectProperties(PROPERTIES);
        Callable<Connection> callable = () -> {
            log.info("getConn");
            ResultLog.info("正在获取数据库连接");

            DruidPooledConnection conn = catalogDataSource.getConnection();
            log.info("getConnSuccess:{}",conn);
            ResultLog.info("获取数据库连接成功");
            connectionMap.put(catalog,conn);
            return conn;
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Connection> submit = executor.submit(callable);
        try {
            return submit.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            catalogDataSource.close();
            log.error("数据库连接超时",e);
            ResultLog.error("数据库连接超时:"+e.getMessage());
            throw new MybatisHelperException("数据库连接超时");
        }
    }

    public static void clear() {
        if (connectionMap!=null ) {
            connectionMap.values().forEach(conn->{
                try {
                    conn.close();
                } catch (Exception e) {
                    log.error("清理数据库连接异常",e);
                }
            });
            dataSource = null;
        }
    }
}
