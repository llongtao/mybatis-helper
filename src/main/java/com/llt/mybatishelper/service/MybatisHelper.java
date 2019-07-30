package com.llt.mybatishelper.service;

import com.github.javaparser.ast.*;
import com.llt.mybatishelper.builder.entity.EntityBuilder;
import com.llt.mybatishelper.builder.mapper.MapperBuilder;
import com.llt.mybatishelper.builder.xml.XmlBuilder;
import com.llt.mybatishelper.data.DataSourceHolder;
import com.llt.mybatishelper.model.BuildConfig;
import com.llt.mybatishelper.model.Config;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.utils.FileUtils;
import com.llt.mybatishelper.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @author lilongtao
 */
@AllArgsConstructor
public class MybatisHelper {


    public static void run(Config config) {
        String baseDbUrl = config.getBaseDbUrl();
        String baseDbUsername = config.getBaseDbUsername();
        String baseDbPassword = config.getBaseDbPassword();
        String baseDbDriverClassName = config.getBaseDbDriverClassName();


        config.getBuildConfigList().forEach(buildConfig -> {
            String dbUrl = buildConfig.getDbUrl();
            boolean useBaseDb = StringUtils.isEmpty(dbUrl);
            String dataSourceUrl;
            if (useBaseDb) {
                dataSourceUrl = baseDbUrl;
                DataSourceHolder.addDataSource(baseDbDriverClassName, baseDbUrl, baseDbUsername, baseDbPassword);
            } else {
                dataSourceUrl = buildConfig.getDbUrl();
                DataSourceHolder.addDataSource(baseDbDriverClassName, buildConfig.getDbUrl(), buildConfig.getDbUsername(), buildConfig.getDbPassword());
            }

            List<String> allFilePath = FileUtils.getAllFilePath(buildConfig.getEntityFolder());
            allFilePath.forEach(filePath -> {
                String entityClassStr = FileUtils.readFileToString(filePath);
                EntityModel entityModel = EntityBuilder.build(entityClassStr);
                if (entityModel != null) {
                    updateTable(entityModel, dataSourceUrl);
                    buildMapper(entityModel, buildConfig);
                    buildXml(entityModel, buildConfig);
                }
            });
        });
    }

    private static void updateTable(EntityModel entityModel, String dataSourceUrl) {
        Connection connection = DataSourceHolder.getConnection(dataSourceUrl);
        String tableName = entityModel.getTableName();
        Set<String> columnSet = new HashSet<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = '" + tableName + "'");
            while (resultSet.next()) {
                columnSet.add(resultSet.getString(1).trim().toLowerCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        entityModel.setExistsColumn(columnSet);

        String sql = entityModel.toSql();

        if (sql != null) {
            System.out.println(sql);
            try {
                Statement statement = connection.createStatement();
                statement.execute(entityModel.toSql());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void buildMapper(EntityModel entityModel, BuildConfig buildConfig) {
        CompilationUnit compilationUnit = MapperBuilder.build(entityModel, buildConfig);

        try {
            FileWriter fileWriter = new FileWriter(new File(buildConfig.getMapperFolder() + "\\" + entityModel.getMapperName() + ".java"));
            fileWriter.write(compilationUnit.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buildXml(EntityModel entityModel, BuildConfig buildConfig) {
        Document document = XmlBuilder.build(entityModel, buildConfig);
        OutputFormat format = OutputFormat.createPrettyPrint();
//        format.setTrimText(false);
//        format.setIndent(true);
//        //format.setIndent("    ");
//        format.setNewlines(true);
//        format.setNewLineAfterDeclaration(true);
//        format.setNewLineAfterNTags(1);
//        format.setPadText(true);
//
        format.setIndentSize(4);
//        format.setNewlines(true);
        format.setTrimText(false);
//        format.setPadText(true);

        XMLWriter writer = null;

        try {
            writer = new XMLWriter(new FileOutputStream(new File(buildConfig.getXmlFolder() + "\\base\\" + entityModel.getMapperName() + ".xml")), format);
            writer.write(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
