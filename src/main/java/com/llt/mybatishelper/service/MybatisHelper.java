package com.llt.mybatishelper.service;

import com.llt.mybatishelper.model.Config;

/**
 * @author LILONGTAO
 * @date 2019-08-01
 */
public interface MybatisHelper {
    /**
     * 启动入口
     *
     * @param config 配置文件
     */
    void run(Config config);
}
