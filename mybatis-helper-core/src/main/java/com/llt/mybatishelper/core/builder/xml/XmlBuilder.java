package com.llt.mybatishelper.core.builder.xml;

import com.llt.mybatishelper.core.model.EntityModel;
import org.dom4j.Document;

/**
 * @author LILONGTAO
 * @date 2020-09-27
 */
public interface XmlBuilder {
    String MAPPER = "mapper";

    String MAPPER_PUBLIC_ID = "-//mybatis.org//DTD Mapper 3.0//EN";

    String MAPPER_SYSTEM_ID = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";

    String NAMESPACE = "namespace";

    String SELECT = "select";

    String ID = "id";

    String QUERY_BY_PRIMARY_KEY = "queryByPrimaryKey";

    String WHERE = "where";

    String BASE_COLUMN = "BaseColumn";

    String BASE_NO_PK_COLUMN = "BaseNoPkColumn";

    String SQL = "sql";

    String DELETE = "delete";

    String DELETE_BY_PRIMARY_KEY = "deleteByPrimaryKey";

    String INSERT = "insert";

    String TRIM = "trim";

    String PREFIX = "prefix";

    String SUFFIX = "suffix";

    String SUFFIX_OVERRIDES = "suffixOverrides";

    String FOREACH = "foreach";

    String OPEN = "open";

    String CLOSE = "close";

    String COLLECTION = "collection";

    String ITEM = "item";

    String SEPARATOR = "separator";

    String UPDATE = "update";

    String UPDATE_SELECTIVE = "updateSelective";

    String PARAMETER_TYPE = "parameterType";

    String SET = "set";

    String IF = "if";

    String LIST = "list";

    String TEST = "test";

    String REF_ID = "refid";

    String INCLUDE = "include";

    String RESULT_MAP = "resultMap";

    String BASE_RESULT_MAP = "BaseResultMap";

    String QUERY = "query";

    String STRING = "String";

    String ONE_TAB = "\n\t";

    String TWO_TAB = "\n\t\t";

    String THREE_TAB = "\n\t\t\t";

    String FOUR_TAB = "\n\t\t\t\t";

    String FIVE_TAB = "\n\t\t\t\t\t";

    String FROM = "from";



    String AND = "and";

    String SPACE = " ";

    String COMMA = ",";

    String COLUMN = "column";

    String TYPE = "type";

    String RESULT = "result";

    String JDBC_TYPE = "jdbcType";

    String PROPERTY = "property";

    String INTO = "into";

    String INSERT_INTO = "insert into";

    String LEFT_PARENTHESIS = "(";

    String RIGHT_PARENTHESIS = ")";

    String VALUES = "values";

    String DIFFER_NULL = "!=null";

    String DIFFER_EMPTY = "!=''";

    String EMPTY = "";

    String LEFT_BRACKET = "#{";

    String RIGHT_BRACKET = "}";

    String EQ = " = ";

    String DOT = ".";

    String SEMICOLON = ";";

    String TYPE_HANDLER = "typeHandler";

    String TIPS = "自己的查询请写在这里,更新时这个文件不会被覆盖";


    /**
     * 构建base  xml Document
     * @param entityModel 实体模型
     * @param split 分隔符
     * @return xml Document 对象
     */
    Document build(EntityModel entityModel, String split);

    /**
     * 当xml不存在时构建的空 xml Document
     * @param entityModel 实体模型
     * @return xml Document 对象
     */
    Document buildEmpty(EntityModel entityModel);
}
