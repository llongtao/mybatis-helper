package com.llt.mybatishelper.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.llt.mybatishelper.model.Config;
import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.utils.FileUtils;
import com.llt.mybatishelper.utils.StringUtils;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MybatisHelper {


    private static final Map<String, String> TYPE_MAP;

    static {
        TYPE_MAP = new HashMap<>();
        TYPE_MAP.put("String", "varchar");
        TYPE_MAP.put("Integer", "int");
        TYPE_MAP.put("Long", "bigint");

    }

    private static final Map<String, Integer> DEFAULT_LENGTH;

    static {
        DEFAULT_LENGTH = new HashMap<>();
        DEFAULT_LENGTH.put("varchar", 255);

    }


    private String driverClass;
    private String url;
    private String username;
    private String password;

    public static void run(Config config) {
        String baseDbUrl = config.getBaseDbUrl();
        String baseDbUsername = config.getBaseDbUsername();
        String baseDbPassword = config.getBaseDbPassword();
        String baseDbDriverClassName = config.getBaseDbDriverClassName();


        config.getBuildConfigList().forEach(buildConfig -> {
            String dbUrl = buildConfig.getDbUrl();
            boolean useBaseDB = StringUtils.isEmpty(dbUrl);
            MybatisHelper mybatisHelper;
            if (useBaseDB) {
                mybatisHelper = new MybatisHelper(baseDbDriverClassName, baseDbUrl, baseDbUsername, baseDbPassword);
            } else {
                mybatisHelper = new MybatisHelper(baseDbDriverClassName, buildConfig.getDbUrl(), buildConfig.getDbUsername(), buildConfig.getDbPassword());
            }
            List<String> allFilePath = FileUtils.getAllFilePath(buildConfig.getEntityFolder());
            allFilePath.forEach(filePath -> {
                String entityClassStr = FileUtils.readFileToString(filePath);
                String sql = mybatisHelper.buildTableSql(entityClassStr);
                //System.out.println(entityClassStr);
                Connection connection = mybatisHelper.getConnection();
                try {
                    Statement statement = connection.createStatement();
                    statement.execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });




        });


    }

    private String buildTableSql(String classStr) {
        EntityModel entityModel = new EntityModel();
        CompilationUnit compilationUnit = StaticJavaParser.parse(classStr);
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();

        entityModel.setPackageName(packageDeclaration.get().getName().toString());

        List<Node> childNodes = compilationUnit.getChildNodes();
        ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) childNodes.stream().filter(node -> node instanceof ClassOrInterfaceDeclaration).collect(Collectors.toList()).get(0);

        String content = null;
        try {
            content = classDeclaration.getComment().get().getContent();
        } catch (Exception ignore) {
        }
        String tableDescription = null;
        String tableName = null;
        if (content != null) {
            tableDescription = StringUtils.getValue(".描述", content);
            tableName = StringUtils.getValue(".表名", content);
        }
        entityModel.setDescription(tableDescription);
        String className = classDeclaration.getName().toString();
        entityModel.setTableName(tableName == null ? StringUtils.transformUnderline(className) : tableName);

        List<Node> fieldList = classDeclaration.getChildNodes().stream().filter(node -> node instanceof FieldDeclaration).collect(Collectors.toList());

        List<EntityField> primaryKeyList = new ArrayList<>();

        List<EntityField> columnList = new ArrayList<>();

        for (Node field : fieldList) {
            String fieldComment =null;
            try{
                fieldComment= field.getComment().get().getContent();
            }catch (Exception ignore){
            }
            String lengthStr = null;
            String primaryKey = null;
            if (fieldComment != null) {
                primaryKey = StringUtils.getValue(".主键", fieldComment);
                lengthStr = StringUtils.getValue(".长度", fieldComment);
            }
            boolean isPrimaryKey = null != primaryKey;
            Integer size = null;
            if (!StringUtils.isEmpty(lengthStr)) {
                try {
                    size = Integer.parseInt(lengthStr);
                } catch (Exception ignore) {
                }
            }

            Boolean nullable = null == StringUtils.getValue(".非空", fieldComment);
            String jdbcType = StringUtils.getValue(".类型", fieldComment);
            String defaultValue = StringUtils.getValue(".默认", fieldComment);
            String description = StringUtils.getValue(".描述", fieldComment);
            String columnName = StringUtils.getValue(".列名", fieldComment);

            String name = ((FieldDeclaration) field).getVariables().get(0).getName().toString();
            if (columnName == null) {
                columnName = StringUtils.transformUnderline(name);
            }


            String type = ((FieldDeclaration) field).getVariables().get(0).getType().toString();
            if (jdbcType == null) {
                jdbcType = TYPE_MAP.get(type);
            }
            if (size == null) {
                size = DEFAULT_LENGTH.get(jdbcType);
            }
            if (size != null) {
                jdbcType = jdbcType + "(" + size + ")";
            }


            EntityField entityField = new EntityField(name, columnName, type, jdbcType, defaultValue, nullable, description);
            if (isPrimaryKey) {
                primaryKeyList.add(entityField);
            } else {
                columnList.add(entityField);
            }
        }

        entityModel.setColumnList(columnList);
        entityModel.setPrimaryKeyList(primaryKeyList);


        System.out.println(entityModel.toSql());
        return entityModel.toSql();
    }


    private Connection getConnection() {
        try {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(driverClass);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setUrl(url);
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
