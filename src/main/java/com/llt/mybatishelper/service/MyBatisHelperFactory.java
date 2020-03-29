package com.llt.mybatishelper.service;

import com.llt.mybatishelper.service.Impl.MysqlMybatisHelper;
import com.llt.mybatishelper.service.Impl.NoDbMybatisHelper;
import com.llt.mybatishelper.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */
public class MyBatisHelperFactory {

    private static Map<String, MybatisHelper> mybatisHelperMap = new HashMap<>();

    static {
        mybatisHelperMap.put("mysql", new MysqlMybatisHelper());
        NoDbMybatisHelper noDbMybatisHelper = new NoDbMybatisHelper();
        mybatisHelperMap.put(null,noDbMybatisHelper );
        mybatisHelperMap.put("",noDbMybatisHelper);
    }

    /**
     * 通过 dbUrl 获取对应数据库的实现
     *
     * @param dbType 数据库类型
     * @return MybatisHelper对应实现
     */
    public static MybatisHelper getMybatisHelper(String dbType) {
        MybatisHelper mybatisHelper;
        if (dbType != null) {
            mybatisHelper = mybatisHelperMap.get(dbType.toLowerCase());
        }else {
            mybatisHelper = mybatisHelperMap.get(null);
        }
        if (mybatisHelper == null) {
            throw new IllegalArgumentException("没有" + dbType + "的实现");
        }
        return mybatisHelper;
    }

}
