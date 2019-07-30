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
        Element resultMap = root.addElement("resultMap")
                .addAttribute("id", "BaseResultMap")
                .addAttribute("type", entityClassName);

        entityModel.getPrimaryKeyList().forEach(primaryKey -> {
            resultMap.addElement("id")
                    .addAttribute("column", primaryKey.getColumnName())
                    .addAttribute("jdbcType", primaryKey.getJdbcType())
                    .addAttribute("property", primaryKey.getName());
        });
        entityModel.getColumnList().forEach(column -> {
            resultMap.addElement("result")
                    .addAttribute("column", column.getColumnName())
                    .addAttribute("jdbcType", column.getJdbcType())
                    .addAttribute("property", column.getName());
        });

        List<EntityField> entityFieldList = new ArrayList<>();
        entityFieldList.addAll(entityModel.getPrimaryKeyList());
        entityFieldList.addAll(entityModel.getColumnList());

        StringBuilder baseColumn = new StringBuilder();
        entityFieldList.forEach(entityField -> baseColumn.append("\n\t\t`").append(entityField.getColumnName()).append("`,"));

        root.addElement("sql")
                .addAttribute("id", "BaseColumn")
                .addText(baseColumn.toString())
                .addText("\n\t");

        //构建select
        Element selectByPrimaryKey = root.addElement("select")
                .addAttribute("id", "queryByPrimaryKey")
                .addAttribute("resultMap", "BaseResultMap")
                .addText("\n\t\tselect");
        selectByPrimaryKey.addElement("include")
                .addAttribute("refid", "BaseColumn");
        StringBuilder whereId = new StringBuilder();
        entityModel.getPrimaryKeyList().forEach(primaryKey -> whereId.append("\n\t\tand ").append(primaryKey.getColumnName()).append(" = ").append("#{").append(primaryKey.getName()).append("}"));
        selectByPrimaryKey.addText("\n\t\tfrom")
                .addText("\n\t\t`" + entityModel.getTableName() + "`");
        Element where = selectByPrimaryKey.addElement("where").addText(whereId.toString()).addText("\n\t\t");

        //构建delete
        root.addElement("delete")
                .addAttribute("id", "deleteByPrimaryKey")
                .addText("\n\t\tdelete\n\t\tfrom")
                .addText("\n\t\t`" + entityModel.getTableName() + "`").add(where.createCopy());

        //构建insert
        Element insert = root.addElement("insert")
                .addAttribute("id", "insert" + entityName)
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tinsert into")
                .addText("\n\t\t`" + entityModel.getTableName() + "`")
                .addText("\n\t\t(");
        entityFieldList.forEach(entityField -> insert.addElement("if")
                .addAttribute("test", entityField.getName() + "!=null")
                .addText("\n\t\t\t`" + entityField.getColumnName() + "`,")
                .addText("\n\t\t"));
        insert.addText("\n\t\t)VALUES\n\t\t(");
        entityFieldList.forEach(entityField -> insert.addElement("if")
                .addAttribute("test", entityField.getName() + "!=null")
                .addText("\n\t\t\t#{" + entityField.getName() + "},")
                .addText("\n\t\t"));
        insert.addText("\n\t\t)");
        insert.addText("\n\t\t");

        //构建updateSelective
        Element updateSelective = root.addElement("update")
                .addAttribute("id", "updateSelective")
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tupdate")
                .addText("\n\t\t`" + entityModel.getTableName() + "`")
                .addText("\n\t\tset");
        entityFieldList.forEach(entityField -> updateSelective.addElement("if")
                .addAttribute("test", entityField.getName() + " != null" + ("String".equals(entityField.getType()) ? " and " + entityField.getName() + " !=''" : ""))
                .addText("\n\t\t\t`" + entityField.getColumnName() + "` = #{" + entityField.getName() + "},")
                .addText("\n\t\t"));
        updateSelective.add(where.createCopy());
        updateSelective.addText("\n\t\t");

        //构建update
        Element update = root.addElement("update")
                .addAttribute("id", "update" + entityName)
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tupdate")
                .addText("\n\t\t`" + entityModel.getTableName() + "`")
                .addText("\n\t\tset");
        entityFieldList.forEach(entityField -> update.addText("\n\t\t`" + entityField.getColumnName() + "` = #{" + entityField.getName() + ",jdbcType=" + entityField.getJdbcType() + "},"));
        update.add(where.createCopy());
        update.addText("\n\t\t");


        //构建query
        Element query = root.addElement("select")
                .addAttribute("id", "query" + entityName)
                .addAttribute("parameterType", entityClassName)
                .addAttribute("resultMap", "BaseResultMap")
                .addText("\n\t\tselect");
        query.addElement("include").addAttribute("refid", "BaseColumn");
        Element queryWhere = query.addElement("where");
        entityFieldList.forEach(entityField -> queryWhere.addElement("if")
                .addAttribute("test", entityField.getName() + " != null" + ("String".equals(entityField.getType()) ? " and " + entityField.getName() + " !=''" : ""))
                .addText("\n\t\t\t\tand `" + entityField.getColumnName() + "` = #{" + entityField.getName() + "}")
                .addText("\n\t\t\t"));
        updateSelective.addText("\n\t\t");

        //构建insertList
        Element insertList = root.addElement("insert")
                .addAttribute("id", "insert" + entityName + "List")
                .addAttribute("parameterType", entityClassName)
                .addText("\n\t\tinsert into")
                .addText("\n\t\t`" + entityModel.getTableName() + "`")
                .addText("\n\t\t(");
        entityFieldList.forEach(entityField -> insertList.addText("\n\t\t`" + entityField.getColumnName() + "`,"));
        insertList.addText("\n\t\t)VALUES\n\t\t(");
        Element values = insertList.addElement("foreach")
                .addAttribute("open", "(")
                .addAttribute("close", ")")
                .addAttribute("collection", "list")
                .addAttribute("item", "item")
                .addAttribute("separator", ",");
        entityFieldList.forEach(entityField -> values.addText("\n\t\t\t#{" + entityField.getName() + ",jdbcType=" + entityField.getJdbcType() + "},"));
        values.addText("\n\t\t");
        insertList.addText("\n\t\t");

        return document;
    }
}
