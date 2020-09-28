package com.llt.mybatishelper.core.builder.entity;

import com.llt.mybatishelper.core.model.BuildConfig;
import com.llt.mybatishelper.core.model.EntityField;
import com.llt.mybatishelper.core.model.EntityModel;

import java.util.List;

/**
 * @author LILONGTAO
 * @date 2020-09-27
 */
public interface EntityBuilder {


    /**
     * 通过class构建实体模型定义
     * @param classStr class字符串文件
     * @param buildConfig 构建配置
     * @param baseEntityFieldList 基类字段
     * @return
     */
    EntityModel build(String classStr, BuildConfig buildConfig, List<EntityField> baseEntityFieldList);
}
