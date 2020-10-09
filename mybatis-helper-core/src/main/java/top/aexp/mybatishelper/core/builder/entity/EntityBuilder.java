package top.aexp.mybatishelper.core.builder.entity;

import top.aexp.mybatishelper.core.model.BuildConfig;
import top.aexp.mybatishelper.core.model.EntityField;
import top.aexp.mybatishelper.core.model.EntityModel;

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
