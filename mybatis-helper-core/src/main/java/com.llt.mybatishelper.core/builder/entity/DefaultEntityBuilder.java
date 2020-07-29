package com.llt.mybatishelper.core.builder.entity;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.llt.mybatishelper.core.constants.ClassKey;
import com.llt.mybatishelper.core.constants.FieldKey;
import com.llt.mybatishelper.core.model.BuildConfig;
import com.llt.mybatishelper.core.model.EntityField;
import com.llt.mybatishelper.core.model.EntityModel;
import com.llt.mybatishelper.core.utils.StringUtils;

import java.sql.JDBCType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class DefaultEntityBuilder {


    private static final String DOT = ".";

    private static final String BASE = "Base";

    private static final String MAPPER = "Mapper";

    private static final Map<String, JDBCType> TYPE_MAP;

    static {
        TYPE_MAP = new HashMap<>();
        TYPE_MAP.put("Enum", JDBCType.VARCHAR);
        TYPE_MAP.put("enum", JDBCType.VARCHAR);
        TYPE_MAP.put("String", JDBCType.VARCHAR);
        TYPE_MAP.put("Integer", JDBCType.INTEGER);
        TYPE_MAP.put("int", JDBCType.INTEGER);
        TYPE_MAP.put("Long", JDBCType.BIGINT);
        TYPE_MAP.put("long", JDBCType.BIGINT);
        TYPE_MAP.put("Boolean", JDBCType.BIT);
        TYPE_MAP.put("boolean", JDBCType.BIT);
        TYPE_MAP.put("Date", JDBCType.TIMESTAMP);
        TYPE_MAP.put("char", JDBCType.CHAR);
        TYPE_MAP.put("byte", JDBCType.TINYINT);
        TYPE_MAP.put("Character", JDBCType.CHAR);
        TYPE_MAP.put("LocalTime", JDBCType.TIME);
        TYPE_MAP.put("LocalDate", JDBCType.DATE);
        TYPE_MAP.put("LocalDateTime", JDBCType.TIMESTAMP);
        TYPE_MAP.put("double", JDBCType.DOUBLE);
        TYPE_MAP.put("Double", JDBCType.DOUBLE);
        TYPE_MAP.put("float", JDBCType.FLOAT);
        TYPE_MAP.put("Float", JDBCType.FLOAT);
        TYPE_MAP.put("BigDecimal", JDBCType.DECIMAL);
    }

    private static final Map<JDBCType, String> DEFAULT_LENGTH;

    static {
        DEFAULT_LENGTH = new HashMap<>();
        DEFAULT_LENGTH.put(JDBCType.VARCHAR, "255");
        DEFAULT_LENGTH.put(JDBCType.DECIMAL, "19,6");

    }

    private static final String DEFAULT_KEY = "id";

    public static EntityModel build(String classStr, BuildConfig buildConfig, List<EntityField> baseEntityFieldList) {
        EntityModel entityModel = new EntityModel();


        CompilationUnit compilationUnit = StaticJavaParser.parse(classStr);
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        String packageName = packageDeclaration.get().getName().toString();
        entityModel.setPackageName(packageName);


        List<Node> childNodes = compilationUnit.getChildNodes();
        ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) childNodes.stream().filter(node -> node instanceof ClassOrInterfaceDeclaration).collect(Collectors.toList()).get(0);

        String content = null;
        try {
            content = classDeclaration.getComment().get().getContent();
        } catch (Exception ignore) {
        }
        String tableDescription = null;
        String tableName = null;
        String entityName = null;
        String auto = null;
        String keyType = null;
        if (content != null) {
            tableDescription = StringUtils.getValue(ClassKey.DESC.getCode(), content);
            tableName = StringUtils.getValue(ClassKey.TABLE_NAME.getCode(), content);
            entityName = StringUtils.getValue(ClassKey.ENTITY_NAME.getCode(), content);
            auto = StringUtils.getValue(ClassKey.AUTO.getCode(), content);
            keyType = StringUtils.getValue(ClassKey.KEY_TYPE.getCode(), content);
        }
        if (auto == null) {
            return null;
        }
        entityModel.setDescription(tableDescription);
        String className = classDeclaration.getName().toString();
        if (tableName == null) {
            entityModel.setTableName(StringUtils.transformUnderline(className));
        } else {
            entityModel.setTableName(tableName);
        }

        entityModel.setClassName(className);
        String entityClassName = packageName + DOT + className;
        entityModel.setEntityClassName(entityClassName);
        String mapperPackage;
        try {
            mapperPackage = StringUtils.getAfterString(buildConfig.getMapperFolder().replace("\\", DOT), StringUtils.getStringByDot(entityModel.getPackageName(), 2));
        } catch (Exception e) {
            throw new IllegalArgumentException("请检查mapper文件夹是否正确:" + e.getMessage());
        }
        if (entityName == null) {
            entityName = className;
        }
        entityModel.setEntityName(entityName);
        String baseMapperPackage = mapperPackage + ".base";
        String baseMapperName = BASE + entityName + MAPPER;
        String mapperName = entityName + MAPPER;
        entityModel.setBaseMapperName(baseMapperName);
        entityModel.setMapperName(mapperName);
        entityModel.setBaseMapperClassName(baseMapperPackage + DOT + baseMapperName);
        entityModel.setMapperClassName(mapperPackage + DOT + mapperName);
        entityModel.setBaseMapperPackage(baseMapperPackage);
        entityModel.setMapperPackage(mapperPackage);


        List<Node> fieldList = classDeclaration.getChildNodes().stream().filter(node -> node instanceof FieldDeclaration).collect(Collectors.toList());

        List<EntityField> primaryKeyList = new ArrayList<>();

        List<EntityField> columnList = new ArrayList<>();

        if (!Objects.equals(buildConfig.getIgnoreBaseField(), true)) {
            buildBaseFieldList(baseEntityFieldList, columnList, keyType);
        }

        for (Node field : fieldList) {
            if (((FieldDeclaration) field).isStatic()) {
                continue;
            }
            String fieldComment = null;
            try {
                fieldComment = field.getComment().get().getContent();
            } catch (Exception ignore) {
            }
            String lengthStr = null;
            String primaryKey = null;
            String ignoreField = null;
            if (fieldComment != null) {
                primaryKey = StringUtils.getValue(FieldKey.KEY.getCode(), fieldComment);
                lengthStr = StringUtils.getValue(FieldKey.LEN.getCode(), fieldComment);
                ignoreField = StringUtils.getValue(FieldKey.IGNORE.getCode(), fieldComment);
            }
            if (ignoreField != null) {
                continue;
            }
            boolean isPrimaryKey = null != primaryKey;
            String size = lengthStr;

            boolean nullable = null == StringUtils.getValue(FieldKey.NO_NULL.getCode(), fieldComment);
            JDBCType jdbcType = null;
            String jdbcTypeStr = StringUtils.getValue(FieldKey.JDBC_TYPE.getCode(), fieldComment);
            if (jdbcTypeStr != null) {
                try {
                    jdbcType = JDBCType.valueOf(jdbcTypeStr);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            String defaultValue = StringUtils.getValue(FieldKey.DEFAULT.getCode(), fieldComment);
            String description = StringUtils.getValue(FieldKey.DESC.getCode(), fieldComment);
            String columnName = StringUtils.getValue(FieldKey.COLUMN.getCode(), fieldComment);
            boolean isEnum = StringUtils.getValue(FieldKey.ENUM.getCode(), fieldComment) != null;

            String name = ((FieldDeclaration) field).getVariables().get(0).getName().toString();
            if (columnName == null) {
                columnName = StringUtils.transformUnderline(name);
            }


            String type = StringUtils.getAfterDot(((FieldDeclaration) field).getVariables().get(0).getType().toString());
            if (jdbcType == null) {
                jdbcType = TYPE_MAP.get(type);
                if (jdbcType == null) {
                    if (isEnum) {
                        jdbcType = JDBCType.VARCHAR;
                    } else {
                        continue;
                    }
                }
            }
            if (size == null) {
                if (isEnum) {
                    size = "16";
                } else {
                    size = DEFAULT_LENGTH.get(jdbcType);
                }
            }
            String fullJdbcType = jdbcType.getName();
            if (!StringUtils.isEmpty(size) && !"0".equals(size)) {
                fullJdbcType = fullJdbcType + "(" + size + ")";
            }


            EntityField entityField = new EntityField(name, columnName, type, jdbcType, fullJdbcType.toUpperCase(), isEnum, size, defaultValue, nullable, description);

            if (isPrimaryKey) {
                primaryKeyList.add(entityField);
            } else {
                columnList.add(entityField);
            }
        }

        if (primaryKeyList.size() == 0) {
            List<EntityField> idField = columnList.stream().filter(item -> DEFAULT_KEY.equals(item.getName())).collect(Collectors.toList());
            primaryKeyList.addAll(idField);
            columnList.removeAll(idField);
        }
        entityModel.setColumnList(columnList);
        entityModel.setPrimaryKeyList(primaryKeyList);

        return entityModel;
    }

    private static void buildBaseFieldList(List<EntityField> baseEntityFieldList, List<EntityField> columnList, String keyType) {
        if (baseEntityFieldList != null) {

            for (EntityField entityField : baseEntityFieldList) {
                EntityField field = new EntityField(entityField);
                String name = field.getName();

                if (!StringUtils.isEmpty(keyType) && "id".equals(name)) {
                    field.setType(keyType);
                }
                String type = field.getType();
                String columnName = field.getColumnName();

                String length = field.getLength();

                if (StringUtils.isEmpty(columnName) && StringUtils.isEmpty(name)) {
                    continue;
                }
                if (StringUtils.isEmpty(type)) {
                    continue;
                }
                if (StringUtils.isEmpty(columnName)) {
                    field.setColumnName(StringUtils.transformUnderline(name));
                } else {
                    field.setName(StringUtils.transformHump(columnName));
                }
                JDBCType jdbcType = TYPE_MAP.get(type);
                if (jdbcType == null) {
                    continue;
                }
                field.setJdbcType(jdbcType);

                if (length == null) {
                    length = DEFAULT_LENGTH.get(jdbcType);
                }
                String fullJdbcType = jdbcType.getName();
                if (!StringUtils.isEmpty(length) && !"0".equals(length)) {
                    fullJdbcType = fullJdbcType + "(" + length + ")";
                }
                field.setFullJdbcType(fullJdbcType);
                field.setNullable(!Objects.equals(field.getNullable(), false));
                field.setIsEnum("enum".equalsIgnoreCase(type));
                columnList.add(field);
            }
        }
    }
}
