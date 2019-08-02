package com.llt.mybatishelper.builder.xml;

import com.llt.mybatishelper.model.BuildConfig;
import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.model.EntityModel;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class XmlBuilder {

    private static final String MAPPER = "mapper";

    private static final String MAPPER_PUBLIC_ID = "-//mybatis.org//DTD Mapper 3.0//EN";

    private static final String MAPPER_SYSTEM_ID = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";

    private static final String NAMESPACE = "namespace";

    private static final String SELECT = "select";

    private static final String ID = "id";

    private static final String QUERY_BY_PRIMARY_KEY = "queryByPrimaryKey";

    private static final String WHERE = "where";

    private static final String BASE_COLUMN = "BaseColumn";

    private static final String SQL = "sql";

    private static final String DELETE = "delete";

    private static final String DELETE_BY_PRIMARY_KEY = "deleteByPrimaryKey";

    private static final String INSERT = "insert";

    private static final String TRIM = "trim";

    private static final String PREFIX = "prefix";

    private static final String SUFFIX = "suffix";

    private static final String SUFFIX_OVERRIDES = "suffixOverrides";

    private static final String FOREACH = "foreach";

    private static final String OPEN = "open";

    private static final String CLOSE = "close";

    private static final String COLLECTION = "collection";

    private static final String ITEM = "item";

    private static final String SEPARATOR = "separator";

    private static final String UPDATE = "update";

    private static final String UPDATE_SELECTIVE = "updateSelective";

    private static final String PARAMETER_TYPE = "parameterType";

    private static final String SET = "set";

    private static final String IF = "if";

    private static final String LIST = "list";

    private static final String TEST = "test";

    private static final String REF_ID = "refid";

    private static final String INCLUDE = "include";

    private static final String RESULT_MAP = "resultMap";

    private static final String BASE_RESULT_MAP = "BaseResultMap";

    private static final String QUERY = "query";

    private static final String STRING = "String";

    private static final String ONE_TAB = "\n\t";

    private static final String TWO_TAB = "\n\t\t";

    private static final String THREE_TAB = "\n\t\t\t";

    private static final String FOUR_TAB = "\n\t\t\t\t";

    private static final String FROM = "from";

    private static final String SPLIT = "`";

    private static final String AND = "and";

    private static final String SPACE = " ";

    private static final String COMMA = ",";

    private static final String COLUMN = "column";

    private static final String TYPE = "type";

    private static final String RESULT = "result";

    private static final String JDBC_TYPE = "jdbcType";

    private static final String PROPERTY = "property";

    private static final String INTO = "into";

    private static final String INSERT_INTO = "insert into";

    private static final String LEFT_PARENTHESIS = "(";

    private static final String RIGHT_PARENTHESIS = ")";

    private static final String VALUES = "values";

    private static final String DIFFER_NULL = "!=null";

    private static final String DIFFER_EMPTY = "!=''";

    private static final String EMPTY = "";

    private static final String LEFT_BRACKET = "#{";

    private static final String RIGHT_BRACKET = "}";

    private static final String EQ = " = ";

    private static final String DOT = ".";

    private static final String TIPS = "自己的查询请写在这里,更新时这个文件不会被覆盖";


    public static Document build(EntityModel entityModel, BuildConfig buildConfig) {
        String entityName = entityModel.getEntityName();
        // 创建Document
        Document document = DocumentHelper.createDocument();
        document.addDocType(MAPPER, MAPPER_PUBLIC_ID, MAPPER_SYSTEM_ID);
        // 添加根节点
        Element root = document.addElement(MAPPER);
        String mapperClassName = entityModel.getMapperClassName();
        String entityClassName = entityModel.getEntityClassName();
        root.addAttribute(NAMESPACE, mapperClassName);
        // 在根节点下添加第一个子节点
        buildResult(entityModel, root, entityClassName);

        List<EntityField> entityFieldList = new ArrayList<>();
        entityFieldList.addAll(entityModel.getPrimaryKeyList());
        entityFieldList.addAll(entityModel.getColumnList());

        StringBuilder baseColumn = new StringBuilder();
        entityFieldList.forEach(entityField -> baseColumn.append(TWO_TAB + SPLIT).append(entityField.getColumnName()).append(SPLIT + COMMA));
        baseColumn.deleteCharAt(baseColumn.length() - 1);

        buildBaseColumn(root, baseColumn);

        //构建select
        Element selectByPrimaryKey = root.addElement(SELECT)
                .addAttribute(ID, QUERY_BY_PRIMARY_KEY)
                .addAttribute(RESULT_MAP, BASE_RESULT_MAP)
                .addText(TWO_TAB + SELECT);
        Element baseColumnElement = selectByPrimaryKey.addElement(INCLUDE)
                .addAttribute(REF_ID, BASE_COLUMN);
        StringBuilder whereId = new StringBuilder();
        entityModel.getPrimaryKeyList().forEach(primaryKey -> whereId.append(THREE_TAB + AND + SPACE).append(primaryKey.getColumnName()).append(" = ").append("#{").append(primaryKey.getName()).append("}"));
        selectByPrimaryKey.addText(TWO_TAB + FROM)
                .addText(TWO_TAB + SPLIT + entityModel.getTableName() + SPLIT);
        Element where = selectByPrimaryKey.addElement(WHERE).addText(whereId.toString()).addText(TWO_TAB);

        //构建delete
        buildDelete(entityModel, root, where);

        //构建insert
        buildInsert(entityModel, entityName, root, entityClassName, entityFieldList);

        //构建updateSelective
        Element updateSelective = buildUpdateSelective(entityModel, root, entityClassName, where);

        //构建update
        buildUpdate(entityModel, entityName, root, entityClassName, where);

        //构建query
        buildQuery(entityModel, entityName, root, entityClassName, entityFieldList, updateSelective);

        //构建insertList
        buildInsertList(entityModel, entityName, root, entityClassName, entityFieldList, baseColumnElement);

        return document;
    }

    private static void buildBaseColumn(Element root, StringBuilder baseColumn) {
        root.addElement(SQL)
                .addAttribute(ID, BASE_COLUMN)
                .addText(baseColumn.toString())
                .addText(ONE_TAB);
    }

    private static void buildResult(EntityModel entityModel, Element root, String entityClassName) {
        Element resultMap = root.addElement(RESULT_MAP)
                .addAttribute(ID, BASE_RESULT_MAP)
                .addAttribute(TYPE, entityClassName);

        entityModel.getPrimaryKeyList().forEach(primaryKey -> resultMap.addElement(ID)
                .addAttribute(COLUMN, primaryKey.getColumnName())
                .addAttribute(JDBC_TYPE, primaryKey.getJdbcType().getName())
                .addAttribute(PROPERTY, primaryKey.getName()));
        entityModel.getColumnList().forEach(column -> resultMap.addElement(RESULT)
                .addAttribute(COLUMN, column.getColumnName())
                .addAttribute(JDBC_TYPE, column.getJdbcType().getName())
                .addAttribute(PROPERTY, column.getName()));
    }

    private static void buildDelete(EntityModel entityModel, Element root, Element where) {
        root.addElement(DELETE)
                .addAttribute(ID, DELETE_BY_PRIMARY_KEY)
                .addText(TWO_TAB + DELETE)
                .addText(TWO_TAB + FROM)
                .addText(TWO_TAB + SPLIT + entityModel.getTableName() + SPLIT)
                .add(where.createCopy());
    }

    private static void buildInsert(EntityModel entityModel, String entityName, Element root, String entityClassName, List<EntityField> entityFieldList) {
        Element insert = root.addElement(INSERT)
                .addAttribute(ID, INSERT + entityName)
                .addAttribute(PARAMETER_TYPE, entityClassName)
                .addText(TWO_TAB + INSERT + SPACE + INTO)
                .addText(TWO_TAB + SPLIT + entityModel.getTableName() + SPLIT);

        Element trimColumn = insert.addElement(TRIM)
                .addAttribute(PREFIX, LEFT_PARENTHESIS)
                .addAttribute(SUFFIX, RIGHT_PARENTHESIS)
                .addAttribute(SUFFIX_OVERRIDES, COMMA);
        entityFieldList.forEach(entityField -> trimColumn.addElement(IF)
                .addAttribute(TEST, entityField.getName() + DIFFER_NULL)
                .addText(FOUR_TAB + SPLIT + entityField.getColumnName() + SPLIT + COMMA)
                .addText(THREE_TAB));
        insert.addText(TWO_TAB + VALUES);
        Element trimValues = insert.addElement(TRIM)
                .addAttribute(PREFIX, LEFT_PARENTHESIS)
                .addAttribute(SUFFIX, RIGHT_PARENTHESIS)
                .addAttribute(SUFFIX_OVERRIDES, COMMA);
        entityFieldList.forEach(entityField -> trimValues.addElement(IF)
                .addAttribute(TEST, entityField.getName() + DIFFER_NULL)
                .addText(FOUR_TAB + LEFT_BRACKET + entityField.getName() + RIGHT_BRACKET + COMMA)
                .addText(THREE_TAB));
    }

    private static void buildUpdate(EntityModel entityModel, String entityName, Element root, String entityClassName, Element where) {
        Element update = root.addElement(UPDATE)
                .addAttribute(ID, UPDATE + entityName)
                .addAttribute(PARAMETER_TYPE, entityClassName)
                .addText(TWO_TAB + UPDATE)
                .addText(TWO_TAB + SPLIT + entityModel.getTableName() + SPLIT);
        Element updateSet = update.addElement(SET);
        entityModel.getColumnList().forEach(entityField -> updateSet.addText(THREE_TAB + SPLIT + entityField.getColumnName() + SPLIT + SPACE + EQ + SPACE + LEFT_BRACKET + entityField.getName() + COMMA + JDBC_TYPE + EQ + entityField.getJdbcType() + RIGHT_BRACKET + COMMA));
        updateSet.addText(TWO_TAB);
        update.add(where.createCopy());
    }

    private static void buildQuery(EntityModel entityModel, String entityName, Element root, String entityClassName, List<EntityField> entityFieldList, Element updateSelective) {
        Element query = root.addElement(SELECT)
                .addAttribute(ID, QUERY + entityName)
                .addAttribute(PARAMETER_TYPE, entityClassName)
                .addAttribute(RESULT_MAP, BASE_RESULT_MAP)
                .addText(TWO_TAB + SELECT);
        query.addElement(INCLUDE).addAttribute(REF_ID, BASE_COLUMN);
        query.addText(TWO_TAB + FROM).addText(TWO_TAB + SPLIT + entityModel.getTableName() + SPLIT);
        Element queryWhere = query.addElement(WHERE);
        entityFieldList.forEach(entityField -> queryWhere.addElement(IF)
                .addAttribute(TEST, entityField.getName() + SPACE + DIFFER_NULL + (STRING.equals(entityField.getType()) ? SPACE + AND + SPACE + entityField.getName() + SPACE + DIFFER_EMPTY : EMPTY))
                .addText(FOUR_TAB + AND + SPACE + SPLIT + entityField.getColumnName() + SPLIT + SPACE + EQ + SPACE + LEFT_BRACKET + entityField.getName() + RIGHT_BRACKET)
                .addText(THREE_TAB));
        updateSelective.addText(TWO_TAB);
    }

    private static void buildInsertList(EntityModel entityModel, String entityName, Element root, String entityClassName, List<EntityField> entityFieldList, Element baseColumn) {
        Element insertList = root.addElement(INSERT)
                .addAttribute(ID, INSERT + entityName + "List")
                .addAttribute(PARAMETER_TYPE, entityClassName)
                .addText(TWO_TAB + INSERT_INTO)
                .addText(TWO_TAB + SPLIT + entityModel.getTableName() + SPLIT);
        Element insertListTrim = insertList.addElement(TRIM)
                .addAttribute(PREFIX, LEFT_PARENTHESIS)
                .addAttribute(SUFFIX, RIGHT_PARENTHESIS)
                .addAttribute(SUFFIX_OVERRIDES, COMMA);
        insertListTrim.add(baseColumn.createCopy());
        insertList.addText(TWO_TAB + VALUES);
        Element values = insertList.addElement(FOREACH)
                .addAttribute(OPEN, LEFT_PARENTHESIS)
                .addAttribute(CLOSE, RIGHT_PARENTHESIS)
                .addAttribute(COLLECTION, LIST)
                .addAttribute(ITEM, ITEM)
                .addAttribute(SEPARATOR, COMMA);
        StringBuilder items = new StringBuilder();
        entityFieldList.forEach(entityField -> items.append(THREE_TAB + LEFT_BRACKET + ITEM + DOT).append(entityField.getName()).append(COMMA).append(JDBC_TYPE).append(EQ).append(entityField.getJdbcType()).append(RIGHT_BRACKET).append(COMMA));
        items.deleteCharAt(items.length() - 1);
        values.addText(items.toString()).addText(TWO_TAB);
        insertList.addText(TWO_TAB);
    }


    private static Element buildUpdateSelective(EntityModel entityModel, Element root, String entityClassName, Element where) {
        Element updateSelective = root.addElement(UPDATE)
                .addAttribute(ID, UPDATE_SELECTIVE)
                .addAttribute(PARAMETER_TYPE, entityClassName)
                .addText(TWO_TAB + UPDATE)
                .addText(TWO_TAB + SPLIT + entityModel.getTableName() + SPLIT);
        Element updateSelectiveSet = updateSelective.addElement(SET);
        entityModel.getColumnList().forEach(entityField -> updateSelectiveSet.addElement(IF)
                .addAttribute(TEST, entityField.getName() + SPACE + DIFFER_NULL + (STRING.equals(entityField.getType()) ? SPACE + AND + SPACE + entityField.getName() + SPACE + DIFFER_EMPTY : EMPTY))
                .addText(FOUR_TAB + SPLIT + entityField.getColumnName() + SPLIT + EQ + LEFT_BRACKET + entityField.getName() + RIGHT_BRACKET + COMMA)
                .addText(THREE_TAB));
        updateSelective.add(where.createCopy());
        return updateSelective;
    }

    public static Document buildEmpty(String mapperClassName) {
        // 创建Document
        Document document = DocumentHelper.createDocument();
        document.addDocType(MAPPER, MAPPER_PUBLIC_ID, MAPPER_SYSTEM_ID);
        // 添加根节点
        Element root = document.addElement(MAPPER);
        root.addAttribute(NAMESPACE, mapperClassName);
        root.addComment(TIPS);
        return document;
    }
}
