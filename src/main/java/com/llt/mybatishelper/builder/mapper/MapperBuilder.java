package com.llt.mybatishelper.builder.mapper;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.llt.mybatishelper.model.BuildConfig;
import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.utils.StringUtils;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import org.dom4j.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class MapperBuilder {


    private static final String MAPPER = "Mapper";

    private static final String LIST = "List";

    private static final String INSERT = "insert";

    private static final String UPDATE = "update";

    private static final String UPDATE_SELECTIVE = "updateSelective";

    private static final String QUERY_BY_PRIMARY_KEY = "queryByPrimaryKey";

    private static final String DELETE_BY_PRIMARY_KEY = "deleteByPrimaryKey";

    private static final String QUERY = "query";

    private static final String IMPORT_JAVA_LANG = "java.lang.*";

    private static final String IMPORT_LIST = "java.util.List";

    private static final String IMPORT_ANNOTATIONS_MAPPER = "org.apache.ibatis.annotations.Mapper";

    private static final String TIPS = "自己的查询请写在这里,更新时这个类不会被覆盖";

    public static CompilationUnit build(EntityModel entityModel) {

        String mapperPackage = entityModel.getBaseMapperPackage();
        String entityClassName = entityModel.getEntityClassName();
        String className = entityModel.getBaseMapperName();
        String entityName = entityModel.getEntityName();

        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(mapperPackage);
        compilationUnit.addImport(IMPORT_JAVA_LANG);
        compilationUnit.addImport(IMPORT_LIST);
        compilationUnit.addImport(IMPORT_ANNOTATIONS_MAPPER);
        compilationUnit.addImport(entityClassName);

        ClassOrInterfaceDeclaration mapperClass = compilationUnit
                .addClass(className)
                .setPublic(true)
                .setInterface(true)
                .addAnnotation(MAPPER);
        mapperClass.setComment(new JavadocComment("@author MybatisHelper\n@date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));


        NodeList<Parameter> nodeList = new NodeList<>();
        Parameter parameter = new Parameter();
        String parameterName = StringUtils.firstToLower(entityModel.getEntityName());
        parameter.setType(entityName);
        parameter.setName(parameterName);
        nodeList.add(parameter);


        List<EntityField> primaryKeyList = entityModel.getPrimaryKeyList();
        NodeList<Parameter> keyParameterList = new NodeList<>();
        primaryKeyList.forEach(primaryKey -> {
            Parameter keyParameter = new Parameter();
            keyParameter.setName(primaryKey.getName());
            keyParameter.setType(primaryKey.getType());
            keyParameterList.add(keyParameter);
        });

        NodeList<Parameter> listNodeList = new NodeList<>();
        Parameter listParameter = new Parameter();
        listParameter.setType("List<" + entityName + ">");
        listParameter.setName(parameterName + LIST);
        listNodeList.add(listParameter);

        mapperClass.addMethod(INSERT + entityName).setParameters(nodeList).setType(Type.NODE).setBody(null);
        mapperClass.addMethod(INSERT + entityName + LIST).setParameters(listNodeList).setType(Type.NODE).setBody(null);
        mapperClass.addMethod(UPDATE + entityName).setType(Type.NODE).setBody(null).setParameters(nodeList);
        mapperClass.addMethod(UPDATE_SELECTIVE).setType(Type.NODE).setBody(null).setParameters(nodeList);
        mapperClass.addMethod(QUERY + entityName).setBody(null).setType("List<" + entityName + ">").setParameters(nodeList);
        mapperClass.addMethod(QUERY_BY_PRIMARY_KEY).setBody(null).setType(entityName).setParameters(keyParameterList);
        mapperClass.addMethod(DELETE_BY_PRIMARY_KEY).setType(Type.NODE).setBody(null).setParameters(keyParameterList);

        return compilationUnit;
    }

    public static CompilationUnit addExtend(String mapperClassStr, String baseMapperName) {

        CompilationUnit compilationUnit = StaticJavaParser.parse(mapperClassStr);
        List<Node> childNodes = compilationUnit.getChildNodes();
        ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) childNodes.stream().filter(node -> node instanceof ClassOrInterfaceDeclaration).collect(Collectors.toList()).get(0);
        NodeList<ClassOrInterfaceType> extendedTypeList = classDeclaration.getExtendedTypes();
        if (extendedTypeList != null) {
            for (ClassOrInterfaceType classOrInterfaceType : extendedTypeList) {
                if (Objects.equals(classOrInterfaceType.getName().toString(), baseMapperName)) {
                    //已经继承的情况直接返回
                    return null;
                }
            }
        }
        classDeclaration.addExtendedType(baseMapperName);
        return compilationUnit;
    }

    public static CompilationUnit buildEmpty(EntityModel entityModel) {
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(entityModel.getMapperPackage());
        compilationUnit.addImport(IMPORT_ANNOTATIONS_MAPPER);
        compilationUnit.addImport(entityModel.getBaseMapperClassName());

        ClassOrInterfaceDeclaration mapperClass = compilationUnit
                .addClass(entityModel.getMapperName())
                .setPublic(true)
                .setInterface(true)
                .addAnnotation(MAPPER);
        mapperClass.setComment(new JavadocComment("@author MybatisHelper\n@date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        mapperClass.addExtendedType(entityModel.getBaseMapperName());
        mapperClass.addOrphanComment(new LineComment(TIPS));
        return compilationUnit;
    }
}
