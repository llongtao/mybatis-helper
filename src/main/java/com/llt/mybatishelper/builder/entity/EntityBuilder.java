package com.llt.mybatishelper.builder.entity;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 * @Description
 */
public class EntityBuilder {

    private static final Map<String, String> TYPE_MAP;

    static {
        TYPE_MAP = new HashMap<>();
        TYPE_MAP.put("String", "VARCHAR");
        TYPE_MAP.put("Integer", "INT");
        TYPE_MAP.put("int", "INT");
        TYPE_MAP.put("Long", "BIGINT");
        TYPE_MAP.put("long", "BIGINT");
        TYPE_MAP.put("Boolean", "TINYINT");
        TYPE_MAP.put("boolean", "TINYINT");
        TYPE_MAP.put("Date", "TIMESTAMP");
        TYPE_MAP.put("BigDecimal", "DECIMAL");


    }

    private static final Map<String, Integer> DEFAULT_LENGTH;

    static {
        DEFAULT_LENGTH = new HashMap<>();
        DEFAULT_LENGTH.put("varchar", 255);

    }


    public static EntityModel build(String classStr){
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
        String auto = null;
        if (content != null) {
            tableDescription = StringUtils.getValue(".描述", content);
            tableName = StringUtils.getValue(".表名", content);
            auto = StringUtils.getValue(".auto", content);
        }
        if (auto == null) {
            return null;
        }
        entityModel.setDescription(tableDescription);
        String className = classDeclaration.getName().toString();
        entityModel.setTableName(tableName == null ? StringUtils.transformUnderline(className) : tableName);
        entityModel.setEntityName(className);
        List<Node> fieldList = classDeclaration.getChildNodes().stream().filter(node -> node instanceof FieldDeclaration).collect(Collectors.toList());

        List<EntityField> primaryKeyList = new ArrayList<>();

        List<EntityField> columnList = new ArrayList<>();

        for (Node field : fieldList) {
            String fieldComment = null;
            try {
                fieldComment = field.getComment().get().getContent();
            } catch (Exception ignore) {
            }
            String lengthStr = null;
            String primaryKey = null;
            String ignoreField = null;
            if (fieldComment != null) {
                primaryKey = StringUtils.getValue(".主键", fieldComment);
                lengthStr = StringUtils.getValue(".长度", fieldComment);
                ignoreField = StringUtils.getValue(".ignore", fieldComment);
            }
            if (ignoreField != null) {
                continue;
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


            String type = StringUtils.getAfterDot(((FieldDeclaration) field).getVariables().get(0).getType().toString());
            if (jdbcType == null) {
                jdbcType = TYPE_MAP.get(type);
            }
            if (size == null) {
                size = DEFAULT_LENGTH.get(jdbcType);
            }
            String fullJdbcType= jdbcType;
            if (size != null) {
                fullJdbcType = jdbcType + "(" + size + ")";
            }

            EntityField entityField = new EntityField(name, columnName, type,jdbcType.toUpperCase(), fullJdbcType.toUpperCase(), defaultValue, nullable, description);
            if (isPrimaryKey) {
                primaryKeyList.add(entityField);
            } else {
                columnList.add(entityField);
            }
        }

        entityModel.setColumnList(columnList);
        entityModel.setPrimaryKeyList(primaryKeyList);

        return entityModel;
    }
}
