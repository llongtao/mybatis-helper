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
     * @param dbUrl 数据库连接
     * @return MybatisHelper对应实现
     */
    public static MybatisHelper getMybatisHelper(String dbUrl) {
        String database = StringUtils.getByColonCount(dbUrl, 1);

        MybatisHelper mybatisHelper;
        if (database != null) {
            mybatisHelper = mybatisHelperMap.get(database.toLowerCase());
        }else {
            mybatisHelper = mybatisHelperMap.get(null);
        }
        if (mybatisHelper == null) {
            throw new IllegalArgumentException("没有" + database + "的实现");
        }
        return mybatisHelper;
    }

}
