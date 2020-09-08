package com.llt.mybatishelper.core.start;


import com.llt.mybatishelper.core.file.FileHandler;
import com.llt.mybatishelper.core.model.BuildResult;
import com.llt.mybatishelper.core.model.Config;

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
    BuildResult run(Config config);

    /**
     * 定义文件处理器
     * 若不定义默认使用
     * @see com.llt.mybatishelper.core.file.DefaultFileHandler
     *
     *
     * @param fileHandler 文件处理器
     */
    MybatisHelper withFileHandler(FileHandler fileHandler);
}
