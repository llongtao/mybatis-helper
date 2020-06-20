package com.llt.mybatishelper.core.service;

import com.github.javaparser.ast.*;
import com.llt.mybatishelper.core.builder.entity.EntityBuilder;
import com.llt.mybatishelper.core.builder.mapper.MapperBuilder;
import com.llt.mybatishelper.core.builder.xml.XmlBuilder;
import com.llt.mybatishelper.core.model.BuildConfig;
import com.llt.mybatishelper.core.model.Config;
import com.llt.mybatishelper.core.model.EntityField;
import com.llt.mybatishelper.core.model.EntityModel;
import com.llt.mybatishelper.core.utils.FileUtils;
import lombok.AllArgsConstructor;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    public int run(Config config) {
        List<EntityField> baseEntityFieldList = config.getBaseEntityFieldList();
        AtomicInteger sum = new AtomicInteger();
        config.getBuildConfigList().forEach(buildConfig -> {
            List<String> allFilePath = FileUtils.getAllFilePath(buildConfig.getEntityFolder());
            allFilePath.forEach(filePath -> {
                String entityClassStr = FileUtils.readJavaFileToString(filePath);
                if (entityClassStr != null) {
                    EntityModel entityModel = EntityBuilder.build(entityClassStr,buildConfig,baseEntityFieldList);
                    if (entityModel != null) {
                        sum.incrementAndGet();
                        updateTable(entityModel, buildConfig.getDb());
                        buildMapper(entityModel, buildConfig);
                        buildXml(entityModel, buildConfig);
                    }
                }

            });
        });
        return sum.get();
    }

    /**
     * 更新数据库表
     *
     * @param entityModel   实体模型
     * @param dataSourceUrl 当前模型使用的dataSourceUrl
     */
    protected abstract void updateTable(EntityModel entityModel, String dataSourceUrl) ;

    private void buildMapper(EntityModel entityModel, BuildConfig buildConfig) {
        CompilationUnit baseMapper = MapperBuilder.build(entityModel);

        String mapperClassStr =  FileUtils.readJavaFileToString(buildConfig.getMapperFolder() + "\\"+entityModel.getMapperName()+JAVA);
        CompilationUnit mapper;
        if (mapperClassStr != null) {
            //同名mapper已存在,增加extend
             mapper = MapperBuilder.addExtend(mapperClassStr, entityModel.getBaseMapperName());
        }else {
            //mapper不存在,创建mapper
            mapper = MapperBuilder.buildEmpty(entityModel);
        }
        try {
            File file = new File(buildConfig.getMapperFolder() + SLASH_BASE);
            mkdir(file);
            if (mapper != null) {
                OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(new File(buildConfig.getMapperFolder() + "\\" + entityModel.getMapperName() + JAVA)), StandardCharsets.UTF_8);
                fileWriter.write(mapper.toString());
                fileWriter.close();
            }
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(new File(buildConfig.getMapperFolder() + SLASH_BASE_SLASH + entityModel.getBaseMapperName() + JAVA)), StandardCharsets.UTF_8);
            fileWriter.write(baseMapper.toString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("生成mapper类异常:"+e.getMessage(),e);
        }
    }

    private void buildXml(EntityModel entityModel, BuildConfig buildConfig) {
        Document baseXml = XmlBuilder.build(entityModel);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setTrimText(false);
        XMLWriter writer;

        File xmlFile = new File(buildConfig.getXmlFolder() +"\\"+entityModel.getMapperName()+XML);
        Document xml = null;
        if (!xmlFile.exists()) {
            //xml不存在时创建
            xml = XmlBuilder.buildEmpty(entityModel);
        }

        try {
            File file = new File(buildConfig.getXmlFolder() +SLASH_BASE);
            mkdir(file);
            if (xml != null) {
                writer = new XMLWriter(new FileOutputStream(new File(buildConfig.getXmlFolder() + "\\" + entityModel.getMapperName() + XML)), format);
                writer.write(xml);
                writer.close();
            }

            writer = new XMLWriter(new FileOutputStream(new File(buildConfig.getXmlFolder() + SLASH_BASE_SLASH + entityModel.getBaseMapperName() + XML)), format);
            writer.write(baseXml);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("生成xml异常:"+e.getMessage(),e);
        }
    }

    private void mkdir(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
    }


}
