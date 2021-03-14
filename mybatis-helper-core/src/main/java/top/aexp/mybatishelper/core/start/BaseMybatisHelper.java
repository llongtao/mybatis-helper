package top.aexp.mybatishelper.core.start;

import com.alibaba.fastjson.JSON;
import com.github.javaparser.ast.*;
import top.aexp.mybatishelper.core.builder.entity.DefaultEntityBuilder;
import top.aexp.mybatishelper.core.builder.entity.EntityBuilder;
import top.aexp.mybatishelper.core.builder.mapper.DefaultMapperBuilder;
import top.aexp.mybatishelper.core.builder.mapper.MapperBuilder;
import top.aexp.mybatishelper.core.builder.xml.DefaultXmlBuilder;
import top.aexp.mybatishelper.core.builder.xml.XmlBuilder;
import top.aexp.mybatishelper.core.data.DataSourceHolder;
import top.aexp.mybatishelper.core.exception.MybatisHelperException;
import top.aexp.mybatishelper.core.file.DefaultFileHandler;
import top.aexp.mybatishelper.core.file.FileHandler;
import top.aexp.mybatishelper.core.log.ResultLog;
import top.aexp.mybatishelper.core.model.*;
import top.aexp.mybatishelper.core.model.*;
import top.aexp.mybatishelper.core.utils.CollectionUtils;
import top.aexp.mybatishelper.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 */
@Slf4j
public abstract class BaseMybatisHelper implements MybatisHelper {


    private static final String XML = ".xml";

    private static final String JAVA = ".java";

    private static final String SLASH_BASE = "/base";

    private static final String SLASH_BASE_SLASH = "/base/";

    private String charset = "utf-8";

    protected FileHandler fileHandler;

    protected MapperBuilder mapperBuilder;

    protected XmlBuilder xmlBuilder;

    protected EntityBuilder entityBuilder;

    @Override
    public BuildResult run(Config config) {
        pre();
        try {
            return BuildResult.succeed(start(config));
        } catch (Exception e) {
            return BuildResult.error(e);
        }
    }

    private void pre() {
        if (fileHandler == null) {
            fileHandler = new DefaultFileHandler();
        }
        if (mapperBuilder == null) {
            mapperBuilder = new DefaultMapperBuilder();
        }
        if (xmlBuilder == null) {
            xmlBuilder = new DefaultXmlBuilder();
        }
        if (entityBuilder == null) {
            entityBuilder = new DefaultEntityBuilder();
        }
    }

    @Override
    public MybatisHelper fileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        return this;
    }


    @Override
    public MybatisHelper entityBuilder(EntityBuilder entityBuilder) {
        this.entityBuilder = entityBuilder;
        return this;
    }

    @Override
    public MybatisHelper mapperBuilder(MapperBuilder mapperBuilder) {
        this.mapperBuilder = mapperBuilder;
        return this;
    }

    @Override
    public MybatisHelper xmlBuilder(XmlBuilder xmlBuilder) {
        this.xmlBuilder = xmlBuilder;
        return this;
    }

    private Integer start(Config config) {
        this.charset = config.getCharset();
        //config db
        boolean useDb = initDb(config);

        List<EntityField> baseEntityFieldList = config.getBaseEntityFieldList();
        AtomicInteger sum = new AtomicInteger();

        List<BuildConfig> buildConfigList = config.getBuildConfigList();
        for (BuildConfig buildConfig : buildConfigList) {
            if (Objects.equals(buildConfig.getDisable(), true)) {
                continue;
            }

            List<String> allFilePath = fileHandler.getAllFilePath(buildConfig.getEntityFolder());
            allFilePath.forEach(filePath -> {
                filePath = filePath.replace("\\","/");
                String entityClassStr = fileHandler.readJavaFileToString(filePath, charset);
                ResultLog.info("readFile:" + filePath);

                if (entityClassStr != null) {
                    EntityModel entityModel = entityBuilder.build(entityClassStr, buildConfig, baseEntityFieldList);
                    ResultLog.info("buildEntityModel success");

                    if (entityModel != null) {
                        if (useDb) {
                            buildDbTable(buildConfig, entityModel, config);
                        }else {
                            ResultLog.sql(buildCreateSql(entityModel));
                        }

                        buildMapper(entityModel, buildConfig);

                        buildXml(entityModel, buildConfig);

                        buildSqlLog(buildConfig.getXmlFolder());

                        sum.incrementAndGet();
                    }
                }
            });
        }


        DataSourceHolder.clear();
        return sum.get();
    }

    private void buildSqlLog( String xmlFolder) {
        List<String> sqlList = ResultLog.pollSqlList();
        if (sqlList.isEmpty()) {
            return;
        }
        String logPath = xmlFolder + "/sql/log.sql";

        StringBuilder sb = new StringBuilder();

        if (fileHandler.exists(logPath)) {
            //log存在
            String s = fileHandler.readFileToString(logPath, charset);
            sb.append(s);
        } else {
            //log不存在
            fileHandler.mkdir(xmlFolder + "/sql");
        }
        sb.append("\r\n\r\n").append("-- ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        sqlList.forEach(sql -> sb.append("\r\n").append(sql));
        try {
            fileHandler.writerString2File(logPath, sb.toString(), charset);
        } catch (IOException e) {
            log.error("写入sql日志失败",e);
            ResultLog.error("写入sql日志失败:"+e.getMessage());
        }
    }

    private boolean initDb(Config config) {
        boolean useDb = Objects.equals(config.getUseDb(), true);
        ResultLog.info("useDb:" + useDb);
        if (Objects.equals(config.getUseDb(), true)) {
            String dbUrl = getDbUrl(config.getBaseDbUrl());
            ResultLog.info("dbUrl:" + dbUrl);
            DataSourceHolder.addDataSource(getDbDriverClassName(), dbUrl, config.getBaseDbUsername(), config.getBaseDbPassword());
        }
        return useDb;
    }

    /**
     * 获取数据库连接串
     * @param baseDbUrl ip:port
     * @return 连接串
     */
    protected abstract String getDbUrl(String baseDbUrl) ;


    private void buildDbTable(BuildConfig buildConfig, EntityModel entityModel, Config config) {


        String schema = buildConfig.getDb();
        if (StringUtils.isEmpty(schema)) {
            throw new MybatisHelperException("若生成表结构数据库名不能为空");
        }
        Connection connection = DataSourceHolder.getConnection(schema);
        try {
            connection.setCatalog(schema);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            rollback(connection);
            throw new MybatisHelperException("切库异常:" + e.getMessage(), e);
        }

        Statement statement;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            rollback(connection);
            throw new MybatisHelperException("获取 statement 失败:" + e.getMessage(), e);
        }

        String tableName = entityModel.getTableName();

        Boolean dropTable = config.getDropTable();
        try {
            if (Objects.equals(dropTable, true)) {
                reCreateTable(statement, schema, entityModel);
            } else {
                alterTable(statement, schema, entityModel);
            }
            commit(connection);
        } catch (SQLException e) {
            ResultLog.warn("执行" + tableName + "sql失败:" + e.getMessage());
            log.error("获取" + entityModel.getTableName() + "列失败", e);
            rollback(connection);
        }
        ResultLog.info("buildTable " + tableName + " success");
    }

    private void alterTable(Statement statement, String schema, EntityModel entityModel) throws SQLException {
        //获取存在列
        Set<String> columnSet = getExistsColumns(statement, schema, entityModel);

        if (columnSet.isEmpty()) {
            List<String> createSqlList = buildCreateSql(entityModel);
            log.info("createSqlList:" + createSqlList);
            if (!CollectionUtils.isEmpty(createSqlList)) {
                createSqlList.forEach(sql-> execute(statement,sql));
            }
        } else {
            Set<String> newColumnSet = new HashSet<>();
            List<EntityField> entityFieldList = new ArrayList<>();
            entityFieldList.addAll(entityModel.getColumnList());
            entityFieldList.addAll(entityModel.getPrimaryKeyList());
            entityFieldList.forEach(column -> newColumnSet.add(column.getColumnName()));
            Set<String> addColumnSet = new HashSet<>(newColumnSet);
            Set<String> dropColumnSet = new HashSet<>(columnSet);
            Set<String> modifyColumnSet = new HashSet<>(newColumnSet);
            addColumnSet.removeAll(columnSet);
            modifyColumnSet.retainAll(columnSet);
            dropColumnSet.removeAll(newColumnSet);

            List<EntityField> addSet = entityFieldList.stream().filter(item -> addColumnSet.contains(item.getColumnName())).collect(Collectors.toList());
            List<EntityField> modifySet = entityFieldList.stream().filter(item -> modifyColumnSet.contains(item.getColumnName())).collect(Collectors.toList());

            addSet.forEach(add-> execute(statement, buildAddColumnSql(entityModel, add)));
            dropColumnSet.forEach(drop-> execute(statement, buildDropColumnSql(entityModel, drop)));
            modifySet.forEach(modify-> execute(statement, buildModifyColumnSql(entityModel, modify)));
        }

    }

    private void execute(Statement statement, String sql) {
        if (sql == null) {
            return;
        }
        try {
            statement.execute(sql);
            log.error("执行sql成功:{}",sql);
            ResultLog.sql(sql);
        } catch (SQLException e) {
            log.error("执行sql失败:{}",sql,e);
            ResultLog.error("执行sql失败:"+sql);
        }
    }

    protected abstract String buildModifyColumnSql(EntityModel entityModel, EntityField modifyColumn);

    protected abstract String buildDropColumnSql(EntityModel entityModel, String dropColumn);

    protected abstract String buildAddColumnSql(EntityModel entityModel, EntityField addColumn);

    private Set<String> getExistsColumns(Statement statement, String schema, EntityModel entityModel) throws SQLException {
        Set<String> columnSet = new HashSet<>();
        ResultSet resultSet = statement.executeQuery(getTableExistColumnSql(schema, entityModel.getTableName()));
        while (resultSet.next()) {
            columnSet.add(resultSet.getString(1).trim().toLowerCase());
        }
        ResultLog.info("获取" + entityModel.getTableName() + "列:" + JSON.toJSONString(columnSet));
        return columnSet;
    }

    /**
     * 删除后重新建表
     *
     * @param statement   statement
     * @param schema      schema
     * @param entityModel 实体类
     */
    private void reCreateTable(Statement statement, String schema, EntityModel entityModel) throws SQLException {
        String dropTableSql = getDropTableSql(schema, entityModel.getTableName());
        log.info("sql:" + dropTableSql);
        statement.execute(dropTableSql);
        ResultLog.info("删除表成功");
        ResultLog.sql(dropTableSql);
        List<String> createTableSqlList = buildCreateSql(entityModel);
        createTableSqlList.forEach(sql-> execute(statement,sql));
    }

    private void commit(Connection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getDropTableSql(String schema, String tableName);

    private void buildMapper(EntityModel entityModel, BuildConfig buildConfig) {


        CompilationUnit baseMapper = buildMapperClass(entityModel);

        List<EntityField> primaryKeyList = entityModel.getPrimaryKeyList();
        String pkType = "Integer";
        if (!CollectionUtils.isEmpty(primaryKeyList)) {
            pkType = primaryKeyList.get(0).getType();
        }

        String mapperClassStr = fileHandler.readJavaFileToString(buildConfig.getMapperFolder() + "/" + entityModel.getMapperName() + JAVA, charset);
        CompilationUnit mapper;
        if (mapperClassStr != null) {
            //同名mapper已存在,增加extend
            mapper = mapperBuilder.addExtend(mapperClassStr, MapperBuilder.MAPPER_NAME,entityModel.getEntityName(),pkType);
        } else {
            //mapper不存在,创建mapper
            mapper = mapperBuilder.buildEmpty(entityModel,pkType);
        }
        try {
            String path = buildConfig.getMapperFolder() + SLASH_BASE;
            fileHandler.mkdir(path);
            if (mapper != null) {
                String fileName = buildConfig.getMapperFolder() + "/" + entityModel.getMapperName() + JAVA;
                fileHandler.writerString2File(fileName, mapper.toString(), charset);
            }

            String baseFileName = buildConfig.getMapperFolder() + SLASH_BASE_SLASH + MapperBuilder.MAPPER_NAME + JAVA;
            fileHandler.writerString2File(baseFileName, baseMapper.toString(), charset);
        } catch (IOException e) {
            throw new RuntimeException("生成mapper类异常:" + e.getMessage(), e);
        }
        ResultLog.info("buildMapper " + entityModel.getTableName() + " success");
    }

    private void buildXml(EntityModel entityModel, BuildConfig buildConfig) {
        Document baseXml = buildXmlDoc(entityModel);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setTrimText(false);
        XMLWriter writer;

        String xmlPath = buildConfig.getXmlFolder() + "/" + entityModel.getMapperName() + XML;
        Document xml = null;
        if (!fileHandler.exists(xmlPath)) {
            //xml不存在时创建
            xml = xmlBuilder.buildEmpty(entityModel);
        }

        try {
            fileHandler.mkdir(buildConfig.getXmlFolder() + SLASH_BASE);

            if (xml != null) {
                OutputStream outputStream = fileHandler.getOutputStream(buildConfig.getXmlFolder() + "/" + entityModel.getMapperName() + XML);
                writer = new XMLWriter(outputStream, format);
                writer.write(xml);
                writer.close();
            }
            OutputStream baseFileOutputStream = fileHandler.getOutputStream(buildConfig.getXmlFolder() + SLASH_BASE_SLASH + entityModel.getBaseMapperName() + XML);
            writer = new XMLWriter(baseFileOutputStream, format);
            writer.write(baseXml);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("生成xml异常:" + e.getMessage(), e);
        }
        ResultLog.info("buildXml " + entityModel.getTableName() + " success");
    }

    /**
     * 获取数据库驱动全类名
     *
     * @return 全类名
     */
    protected abstract String getDbDriverClassName();

    /**
     * 构建建表语句
     *
     * @param entityModel 实体模型
     * @return 建表sql
     */
    protected abstract List<String> buildCreateSql(EntityModel entityModel);


    /**
     * 获取指定表第一列为列名的的sql
     *
     * @param schema    schema
     * @param tableName tableName
     * @return sql
     */
    protected abstract String getTableExistColumnSql(String schema, String tableName);


    /**
     * 用实体定义构建class
     *
     * @param entityModel 实体定义
     * @return CompilationUnit
     */
    protected CompilationUnit buildMapperClass(EntityModel entityModel) {
        return mapperBuilder.build(entityModel.getMapperPackage()+".base");
    }

    /**
     * 用实体定义构建xml文档
     *
     * @param entityModel 实体定义
     * @return Document
     */
    protected Document buildXmlDoc(EntityModel entityModel) {
        return xmlBuilder.build(entityModel, " ");
    }


}
