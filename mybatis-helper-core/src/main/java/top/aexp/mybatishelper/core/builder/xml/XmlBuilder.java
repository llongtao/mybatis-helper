package top.aexp.mybatishelper.core.builder.xml;

import top.aexp.mybatishelper.core.model.EntityModel;

/**
 * @author LILONGTAO
 * @date 2020-09-27
 */
public interface XmlBuilder {

    String TIPS = "自己的查询请写在这里,更新时这个文件不会被覆盖";


    /**
     * 构建base  xml Document
     * @param entityModel 实体模型
     * @param split 分隔符
     * @return xml Document 对象
     */
    String build(EntityModel entityModel, String split);

    /**
     * 当xml不存在时构建的空 xml Document
     * @param entityModel 实体模型
     * @return xml Document 对象
     */
    String buildEmpty(EntityModel entityModel);
}
