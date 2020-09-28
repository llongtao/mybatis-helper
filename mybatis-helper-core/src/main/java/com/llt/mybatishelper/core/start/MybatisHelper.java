package com.llt.mybatishelper.core.start;


import com.llt.mybatishelper.core.builder.entity.DefaultEntityBuilder;
import com.llt.mybatishelper.core.builder.entity.EntityBuilder;
import com.llt.mybatishelper.core.builder.mapper.DefaultMapperBuilder;
import com.llt.mybatishelper.core.builder.mapper.MapperBuilder;
import com.llt.mybatishelper.core.builder.xml.XmlBuilder;
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
     * 默认使用
     * @see com.llt.mybatishelper.core.file.DefaultFileHandler
     * @param fileHandler 文件处理器
     */
    MybatisHelper fileHandler(FileHandler fileHandler);

    /**
     * 定义实体构建器
     * 默认使用
     * @see DefaultEntityBuilder
     * @param entityBuilder 实体构建器
     */
    MybatisHelper entityBuilder(EntityBuilder entityBuilder);

    /**
     * 定义mapper构建器
     * 默认使用
     * @see DefaultMapperBuilder
     * @param mapperBuilder mapper构建器
     */
    MybatisHelper mapperBuilder(MapperBuilder mapperBuilder);

    /**
     * 定义xml构建器
     * 默认使用
     * @see DefaultMapperBuilder
     * @param xmlBuilder xml构建器
     */
    MybatisHelper xmlBuilder(XmlBuilder xmlBuilder);
}
