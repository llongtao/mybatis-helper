package com.llt.mybatishelper.core.builder.mapper;

import com.github.javaparser.ast.CompilationUnit;
import com.llt.mybatishelper.core.model.EntityModel;

/**
 * @author LILONGTAO
 * @date 2020-09-27
 */
public interface MapperBuilder {
    String MAPPER = "Mapper";

    String LIST = "List";

    String INSERT = "insert";

    String UPDATE = "update";

    String UPDATE_SELECTIVE = "updateSelective";

    String QUERY_BY_PRIMARY_KEY = "queryByPrimaryKey";

    String DELETE_BY_PRIMARY_KEY = "deleteByPrimaryKey";

    String QUERY = "query";

    String IMPORT_JAVA_LANG = "java.lang.*";

    String IMPORT_LIST = "java.util.List";

    String IMPORT_ANNOTATIONS_MAPPER = "org.apache.ibatis.annotations.Mapper";

    String TIPS = "自己的查询请写在这里,更新时这个类不会被覆盖";


    /**
     * 构建BaseMapper.class文件
     * @param entityModel 实体模型
     * @return CompilationUnit类对象
     */
    CompilationUnit build(EntityModel entityModel);

    /**
     * Mapper.class文件不存在的情况下构建Mapper.class
     * @param entityModel 实体模型
     * @return CompilationUnit类对象
     */
    CompilationUnit buildEmpty(EntityModel entityModel);


    /**
     * Mapper.class文件存在的情况添加继承BaseMapper.class
     * @param mapperClassStr Mapper.class文件
     * @param baseMapperName 继承BaseMapper类名
     * @return CompilationUnit类对象
     */
    CompilationUnit addExtend(String mapperClassStr, String baseMapperName);
}
