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
 * @author LILONGTAO
 */
@AllArgsConstructor
public abstract class BaseMybatisHelper implements MybatisHelper {


    private static final String XML = ".xml";

    private static final String JAVA = ".java";

    private static final String SLASH_BASE = "\\base";

    private static final String SLASH_BASE_SLASH = "\\base\\";


    @Override
    public void run(Config config) {
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

    /**
     * 更新数据库表
     *
     * @param entityModel   实体模型
     * @param dataSourceUrl 当前模型使用的dataSourceUrl
     */
    protected abstract void updateTable(EntityModel entityModel, String dataSourceUrl);

    private void buildMapper(EntityModel entityModel, BuildConfig buildConfig) {
        CompilationUnit compilationUnit = MapperBuilder.build(entityModel, buildConfig);

        try {
            File file = new File(buildConfig.getMapperFolder() + SLASH_BASE);
            mkdir(file);
            FileWriter fileWriter = new FileWriter(new File(buildConfig.getMapperFolder() + SLASH_BASE_SLASH + entityModel.getMapperName() + JAVA));
            fileWriter.write(compilationUnit.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildXml(EntityModel entityModel, BuildConfig buildConfig) {
        Document document = XmlBuilder.build(entityModel, buildConfig);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setTrimText(false);
        XMLWriter writer;

        try {
            File file = new File(buildConfig.getXmlFolder() +SLASH_BASE);
            mkdir(file);
            writer = new XMLWriter(new FileOutputStream(new File(buildConfig.getXmlFolder() + SLASH_BASE_SLASH + entityModel.getMapperName() + XML)), format);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mkdir(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
    }


}
