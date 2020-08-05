package com.llt.mybatishelper.core.start;

import com.github.javaparser.ast.*;
import com.llt.mybatishelper.core.builder.entity.DefaultEntityBuilder;
import com.llt.mybatishelper.core.builder.mapper.DefaultMapperBuilder;
import com.llt.mybatishelper.core.builder.xml.DefaultXmlBuilder;
import com.llt.mybatishelper.core.data.DataSourceHolder;
import com.llt.mybatishelper.core.exception.MybatisHelperException;
import com.llt.mybatishelper.core.log.ResultLog;
import com.llt.mybatishelper.core.model.*;
import com.llt.mybatishelper.core.utils.FileUtils;
import com.llt.mybatishelper.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LILONGTAO
 */
public abstract class BaseMybatisHelper implements MybatisHelper {


    private static final String XML = ".xml";

    private static final String JAVA = ".java";

    private static final String SLASH_BASE = "\\base";

    private static final String SLASH_BASE_SLASH = "\\base\\";

    private String charset = "utf-8";

    @Override
    public BuildResult run(Config config) {
        try{
            return BuildResult.succeed(start(config));
        }catch (Exception e){
            return BuildResult.error(e);
        }
    }

    private Integer start(Config config) {
        this.charset = config.getCharset();
        //config db
        boolean useDb = Objects.equals(config.getUseDb(), true);
        ResultLog.info("useDb:"+useDb);
        if (Objects.equals(config.getUseDb(),true)) {
            String dbUrl = "jdbc:" + config.getDbType() + "://" + config.getBaseDbUrl();
            ResultLog.info("dbUrl:"+dbUrl);
            DataSourceHolder.addDataSource(config.getBaseDbDriverClassName(), dbUrl, config.getBaseDbUsername(), config.getBaseDbPassword());
        }


        List<EntityField> baseEntityFieldList = config.getBaseEntityFieldList();
        AtomicInteger sum = new AtomicInteger();

        List<BuildConfig> buildConfigList = config.getBuildConfigList();
        for (BuildConfig buildConfig : buildConfigList) {
            if (Objects.equals(buildConfig.getDisable() ,true)) {
                continue;
            }

            List<String> allFilePath = FileUtils.getAllFilePath(buildConfig.getEntityFolder());
            allFilePath.forEach(filePath -> {

                String entityClassStr = FileUtils.readJavaFileToString(filePath,charset);
                ResultLog.info("readFile:"+filePath);

                if (entityClassStr != null) {
                    EntityModel entityModel = DefaultEntityBuilder.build(entityClassStr,buildConfig,baseEntityFieldList);
                    ResultLog.info("buildEntityModel success");

                    if (entityModel != null) {
                        if (useDb) {
                            buildDbTable(buildConfig, entityModel);
                        }
                        buildMapper(entityModel, buildConfig);

                        buildXml(entityModel, buildConfig);

                        sum.incrementAndGet();
                    }
                }
            });
        }


        DataSourceHolder.clear();
        return sum.get();
    }

    private void buildDbTable(BuildConfig buildConfig, EntityModel entityModel) {
        String schema = buildConfig.getDb();
        if (StringUtils.isEmpty(schema)) {
            throw new MybatisHelperException("若生成表结构数据库名不能为空");
        }
        Connection connection = DataSourceHolder.getConnection();
        try {
            connection.setCatalog(schema);
        } catch (SQLException e) {
            throw new MybatisHelperException("切库异常:"+e.getMessage(),e);
        }
        updateTable(entityModel,connection);
        ResultLog.info("updateTable "+ entityModel.getTableName()+" success");
    }

    /**
     * 更新数据库表
     *
     * @param entityModel   实体模型
     * @param connection 当前模型使用的数据库连接
     */
    protected abstract void updateTable(EntityModel entityModel, Connection connection) ;

    private void buildMapper(EntityModel entityModel, BuildConfig buildConfig) {
        CompilationUnit baseMapper = buildMapperClass(entityModel);

        String mapperClassStr =  FileUtils.readJavaFileToString(buildConfig.getMapperFolder() + "\\"+entityModel.getMapperName()+JAVA,charset);
        CompilationUnit mapper;
        if (mapperClassStr != null) {
            //同名mapper已存在,增加extend
             mapper = DefaultMapperBuilder.addExtend(mapperClassStr, entityModel.getBaseMapperName());
        }else {
            //mapper不存在,创建mapper
            mapper = DefaultMapperBuilder.buildEmpty(entityModel);
        }
        try {
            File file = new File(buildConfig.getMapperFolder() + SLASH_BASE);
            mkdir(file);
            if (mapper != null) {
                String fileName = buildConfig.getMapperFolder() + "\\" + entityModel.getMapperName() + JAVA;
                FileUtils.writerString2File(fileName,mapper.toString(),charset);
            }

            String baseFileName = buildConfig.getMapperFolder() + SLASH_BASE_SLASH + entityModel.getBaseMapperName() + JAVA;
            FileUtils.writerString2File(baseFileName,baseMapper.toString(),charset);
        } catch (IOException e) {
            throw new RuntimeException("生成mapper类异常:"+e.getMessage(),e);
        }
        ResultLog.info("buildMapper "+entityModel.getTableName()+" success");
    }



    private void buildXml(EntityModel entityModel, BuildConfig buildConfig) {
        Document baseXml = buildXmlDoc(entityModel);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setTrimText(false);
        XMLWriter writer;

        File xmlFile = new File(buildConfig.getXmlFolder() +"\\"+entityModel.getMapperName()+XML);
        Document xml = null;
        if (!xmlFile.exists()) {
            //xml不存在时创建
            xml = DefaultXmlBuilder.buildEmpty(entityModel);
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
        ResultLog.info("buildXml "+entityModel.getTableName()+" success");
    }


    /**
     * 用实体定义构建class
     * @param entityModel 实体定义
     * @return CompilationUnit
     */
    protected CompilationUnit buildMapperClass(EntityModel entityModel) {
        return DefaultMapperBuilder.build(entityModel);
    }

    /**
     *  用实体定义构建xml文档
     * @param entityModel 实体定义
     * @return Document
     */
    protected Document buildXmlDoc(EntityModel entityModel) {
        return DefaultXmlBuilder.build(entityModel," ");
    }

    private void mkdir(File file) {
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new MybatisHelperException("创建文件夹失败");
            }
        }
    }


}
