package com.llt.mybatishelper.generator;

import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.model.EntityModel;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Generator {
    public static void main(String[] args)  {
//        List<String> warnings = new ArrayList<String>();
//        Configuration config = new Configuration();
//        DefaultShellCallback callback = new DefaultShellCallback(false);
//        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
//        myBatisGenerator.generate(null);


        try {
            // 创建Document
            Document document = DocumentHelper.createDocument();
            document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
            // 添加根节点
            Element root = document.addElement("mapper");
            root.addAttribute("namespace", "com.llt.aexp.mapper.ArticlesMapper");
            // 在根节点下添加第一个子节点
            Element resultMap = root.addElement("resultMap")
                    .addAttribute("id", "BaseResultMap")
                    .addAttribute("type", "com.llt.aexp.model.dto.ArticlesDTO");

            resultMap.addElement("id")
                    .addAttribute("column", "id")
                    .addAttribute("jdbcType", "INTEGER")
                    .addAttribute("property", "id");
            resultMap.addElement("result")
                    .addAttribute("column", "article_url")
                    .addAttribute("jdbcType", "VARCHAR")
                    .addAttribute("property", "article_url");
            root.addElement("sql")
                    .addAttribute("id", "BaseColumn")
                    .addText("id,title, article_url, user_id, last_comment_user_id, category_id, view_count,\n" +
                            "            comments_count, likes_count, close_comment, is_hidden, is_excellent, `order`, tags_ids,\n" +
                            "                    last_comment_time, created_at, updated_at,`rank`");
            Element element = root.addElement("select")
                    .addAttribute("id", "selectByPrimaryKey")
                    .addAttribute("resultMap", "BaseResultMap")
                    .addText("select");
            element.addElement("include")
                    .addAttribute("refid", "BaseColumn");
            element.addText("from articles \n")
                    .addText("where id = #{id}");


            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File("C:\\Users\\llt11\\Desktop\\a.xml")), format);
            writer.write(document);

            System.out.println("dom4j CreateDom4j success!");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    void buildXml(EntityModel entityModel) throws IOException {
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
        entityFieldList.forEach(entityField -> baseColumn.append(entityField.getColumnName()).append(",\n"));
        baseColumn.deleteCharAt(baseColumn.length()-3);
        root.addElement("sql")
                .addAttribute("id", "BaseColumn")
                .addText(baseColumn.toString());
        Element selectByPrimaryKey = root.addElement("select")
                .addAttribute("id", "selectByPrimaryKey")
                .addAttribute("resultMap", "BaseResultMap")
                .addText("select");
        selectByPrimaryKey.addElement("include")
                .addAttribute("refid", "BaseColumn");

        StringBuilder whereId = new StringBuilder();
        entityModel.getPrimaryKeyList().forEach(primaryKey->{
            whereId.append("and ").append(primaryKey.getColumnName()).append(" = ").append("#{").append(primaryKey.getName()).append("}\n");
        });
        selectByPrimaryKey.addText("from \n")
                .addText("`"+entityModel.getTableName()+"`\n");
        Element where = selectByPrimaryKey.addElement("where").addText(whereId.toString());

        root.addElement("delete")
                .addAttribute("id","deleteByPrimaryKey")
        .addText("delete\nfrom\n")
        .addText("`"+entityModel.getTableName()+"`\n").add(where);

        root.addElement("delete")
                .addAttribute("id","deleteByPrimaryKey")
                .addText("delete\nfrom\n")
                .addText("`"+entityModel.getTableName()+"`\n").add(where);

        root.addElement("insert")
                .addAttribute("id","insert")
                .addAttribute("parameterType",entityClassName)
                .addText("insert into\n")
                .addText("`"+entityModel.getTableName()+"`\n")
                .add(where);


//        <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">
//                delete from articles
//        where id = #{id,jdbcType=INTEGER}
//  </delete>



        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileOutputStream(new File("C:\\Users\\llt11\\Desktop\\a.xml")), format);
        writer.write(document);

    }
}
