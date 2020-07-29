package com.llt.mybatishelper.core.service;

import com.llt.mybatishelper.core.service.impl.MysqlMybatisHelper;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */
public class MyBatisHelperFactory {

    private static final Map<String,MybatisHelper> MYBATIS_HELPER_MAP = new HashMap<>();

    static {
        MYBATIS_HELPER_MAP.put("mysql", new MysqlMybatisHelper());
    }

    /**
     * 通过 dbUrl 获取对应数据库的实现
     *
     * @param dbType 数据库类型
     * @return MybatisHelper对应实现
     */
    public static MybatisHelper getMybatisHelper(String dbType) {
        MybatisHelper mybatisHelper =null;
        if (dbType != null) {
            mybatisHelper = MYBATIS_HELPER_MAP.get(dbType.toLowerCase());
        }
        if (mybatisHelper == null) {
            throw new IllegalArgumentException("没有" + dbType + "的实现");
        }
        return mybatisHelper;
    }

}
