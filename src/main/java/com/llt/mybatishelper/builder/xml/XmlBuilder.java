package com.llt.mybatishelper.builder.xml;

import com.llt.mybatishelper.model.BuildConfig;
import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.utils.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 * @Description
 */
public class XmlBuilder {


    public static Document build(EntityModel entityModel, BuildConfig buildConfig) {
        String entityName = entityModel.getEntityName();
        // 创建Document
        Document document = DocumentHelper.createDocument();
        document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        // 添加根节点
        Element root = document.addElement("mapper");
        String mapperClassName = entityModel.getMapperClassName();
        String entityClassName = entityModel.getEntityClassName();
        root.addAttribute("namespace", mapperClassName);
        // 在根节点下添加第一个子节点
        buildResult(entityModel, root, entityClassName);

        List<EntityField> entityFieldList = new ArrayList<>();
        entityFieldList.addAll(entityModel.getPrimaryKeyList());
        entityFieldList.addAll(entityModel.getColumnList());

        StringBuilder baseColumn = new StringBuilder();
        entityFieldList.forEach(entityField -> baseColumn.append("\n\t\t`").append(entityField.getColumnName()).append("`,"));
        baseColumn.deleteCharAt(baseColumn.length() - 1);

        buildBaseColumn(root, baseColumn);

        //构建select
        Element selectByPrimaryKey = root.addElement("select")
                .addAttribute("id", "queryByPrimaryKey")
                .addAttribute("resultMap", "BaseResultMap")
                .addText("\n\t\tselect");
        Element BaseColumn = selectByPrimaryKey.addElement("include")
                .addAttribute("refid", "BaseColumn");
        StringBuilder whereId = new StringBuilder();
        entityModel.getPrimaryKeyList().forEach(primaryKey -> whereId.append("\n\t\t\tand ").append(primaryKey.getColumnName()).append(" = ").append("#{").append(primaryKey.getName()).append("}"));
        selectByPrimaryKey.addText("\n\t\tfrom")
                .addText("\n\t\t`" + entityModel.getTableName() + "`");
        Element where = selectByPrimaryKey.addElement("where").addText(whereId.toString()).addText("\n\t\t");

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
        buildInsertList(entityModel, entityName, root, entityClassName, entityFieldList, BaseColumn);

        return document;
    }

    private static void buildBaseColumn(Element root, StringBuilder baseColumn) {
        root.addElement("sql")
                .addAttribute("id", "BaseColumn")
                .addText(baseColumn.toString())
                .addText("\n\t");
    }

    private static void buildResult(EntityModel entityModel, Element root, String entityClassName) {
        Element resultMap = root.addElement("resultMap")
                .addAttribute("id", "BaseResultMap")
                .addAttribute("type", entityClassName);

        entityModel.getPrimaryKeyList().forEach(primaryKey -> resultMap.addElement("id")
                .addAttribute("column", primaryKey.getColumnName())
                .addAttribute("jdbcType", primaryKey.getJdbcType())
                .addAttribute("property", primaryKey.getName()));
        entityModel.getColumnList().forEach(column -> resultMap.addElement("result")
                .addAttribute("column", column.getColumnName())
                .addAttribute("jdbcType", column.getJdbcType())
                .addAttribute("property", column.getName()));
    }

    private static void buildDelete(EntityModel entityModel, Element root, Element where) {
        root.addElement("delete")
                .addAttribute("id", "deleteByPrimaryKey")
                .addText("\n\t\tdelete\n\t\tfrom")
                .addText("\n\t\t`" + entityModel.getTableName() + "`").add(where.createCopy());
    }

    private static void buildInsert(EntityModel entityModel, String entityName, Element root, String entityClassName, List<EntityField> entityFieldList) {
        Element insert = root.addElement("insert")
                .addAttribute("id", "insert" + entityName)
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tinsert into")
                .addText("\n\t\t`" + entityModel.getTableName() + "`");

        Element trimColumn = insert.addElement("trim")
                .addAttribute("prefix", "(")
                .addAttribute("suffix", ")")
                .addAttribute("suffixOverrides", ",");
        entityFieldList.forEach(entityField -> trimColumn.addElement("if")
                .addAttribute("test", entityField.getName() + "!=null")
                .addText("\n\t\t\t\t`" + entityField.getColumnName() + "`,")
                .addText("\n\t\t\t"));
        insert.addText("\n\t\tVALUES");
        Element trimValues = insert.addElement("trim")
                .addAttribute("prefix", "(")
                .addAttribute("suffix", ")")
                .addAttribute("suffixOverrides", ",");
        entityFieldList.forEach(entityField -> trimValues.addElement("if")
                .addAttribute("test", entityField.getName() + "!=null")
                .addText("\n\t\t\t\t#{" + entityField.getName() + "},")
                .addText("\n\t\t\t"));
    }

    private static void buildUpdate(EntityModel entityModel, String entityName, Element root, String entityClassName, Element where) {
        Element update = root.addElement("update")
                .addAttribute("id", "update" + entityName)
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tupdate")
                .addText("\n\t\t`" + entityModel.getTableName() + "`");
        Element updateSet = update.addElement("set");
        entityModel.getColumnList().forEach(entityField -> updateSet.addText("\n\t\t\t`" + entityField.getColumnName() + "` = #{" + entityField.getName() + ",jdbcType=" + entityField.getJdbcType() + "},"));
        updateSet.addText("\n\t\t");
        update.add(where.createCopy());
    }

    private static void buildQuery(EntityModel entityModel, String entityName, Element root, String entityClassName, List<EntityField> entityFieldList, Element updateSelective) {
        Element query = root.addElement("select")
                .addAttribute("id", "query" + entityName)
                .addAttribute("parameterType", entityClassName)
                .addAttribute("resultMap", "BaseResultMap")
                .addText("\n\t\tselect");
        query.addElement("include").addAttribute("refid", "BaseColumn");
        query.addText("\n\t\tfrom").addText("\n\t\t`" + entityModel.getTableName() + "`");
        Element queryWhere = query.addElement("where");
        entityFieldList.forEach(entityField -> queryWhere.addElement("if")
                .addAttribute("test", entityField.getName() + " != null" + ("String".equals(entityField.getType()) ? " and " + entityField.getName() + " !=''" : ""))
                .addText("\n\t\t\t\tand `" + entityField.getColumnName() + "` = #{" + entityField.getName() + "}")
                .addText("\n\t\t\t"));
        updateSelective.addText("\n\t\t");
    }

    private static void buildInsertList(EntityModel entityModel, String entityName, Element root, String entityClassName, List<EntityField> entityFieldList, Element baseColumn) {
        Element insertList = root.addElement("insert")
                .addAttribute("id", "insert" + entityName + "List")
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tinsert into")
                .addText("\n\t\t`" + entityModel.getTableName() + "`");
        Element insertListTrim = insertList.addElement("trim")
                .addAttribute("prefix", "(")
                .addAttribute("suffix", ")")
                .addAttribute("suffixOverrides", ",");
        insertListTrim.add(baseColumn.createCopy());
        insertList.addText("\n\t\tvalues");
        Element values = insertList.addElement("foreach")
                .addAttribute("open", "(")
                .addAttribute("close", ")")
                .addAttribute("collection", "list")
                .addAttribute("item", "item")
                .addAttribute("separator", ",");
        StringBuilder items = new StringBuilder();
        entityFieldList.forEach(entityField -> items.append("\n\t\t\t#{item." + entityField.getName() + ",jdbcType=" + entityField.getJdbcType() + "},"));
        items.deleteCharAt(items.length() - 1);
        values.addText(items.toString()).addText("\n\t\t");
        insertList.addText("\n\t\t");
    }


    private static Element buildUpdateSelective(EntityModel entityModel, Element root, String entityClassName, Element where) {
        Element updateSelective = root.addElement("update")
                .addAttribute("id", "updateSelective")
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tupdate")
                .addText("\n\t\t`" + entityModel.getTableName() + "`");
        Element updateSelectiveSet = updateSelective.addElement("set");
        entityModel.getColumnList().forEach(entityField -> updateSelectiveSet.addElement("if")
                .addAttribute("test", entityField.getName() + " != null" + ("String".equals(entityField.getType()) ? " and " + entityField.getName() + " !=''" : ""))
                .addText("\n\t\t\t\t`" + entityField.getColumnName() + "` = #{" + entityField.getName() + "},")
                .addText("\n\t\t\t"));
        updateSelective.add(where.createCopy());
        return updateSelective;
    }
}
