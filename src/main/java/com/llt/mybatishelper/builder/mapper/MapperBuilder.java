package com.llt.mybatishelper.builder.mapper;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.llt.mybatishelper.model.BuildConfig;
import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.model.EntityModel;
import com.llt.mybatishelper.utils.StringUtils;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import org.dom4j.Document;

import java.util.List;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 * @Description
 */
public class MapperBuilder {
    
    private static final String BASE = "Base";

    private static final String MAPPER = "Mapper";
    
    private static final String DOT = ".";

    public static CompilationUnit build(EntityModel entityModel, BuildConfig buildConfig) {
        String entityName = entityModel.getEntityName();
        String packageName = entityModel.getPackageName();
        String entityClassName = packageName + DOT + entityName;
        entityModel.setEntityClassName(entityClassName);
        String prePackageName = StringUtils.getStringByDot(entityModel.getPackageName(), 2);
        String fullMapperPackage = buildConfig.getMapperFolder().replace("\\", DOT);
        String mapperPackage = StringUtils.getAfterString(fullMapperPackage, prePackageName)+".base";
        String className = BASE + entityName + MAPPER;
        entityModel.setMapperName(className);
        entityModel.setMapperClassName(mapperPackage + DOT + className);
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(mapperPackage);
        compilationUnit.addImport("java.lang.*");
        compilationUnit.addImport("java.util.List");
        compilationUnit.addImport("org.apache.ibatis.annotations.Mapper");
        compilationUnit.addImport(entityClassName);
        ClassOrInterfaceDeclaration mapperClass = compilationUnit
                .addClass(className)
                .setPublic(true)
                .setInterface(true)
                .addAnnotation("Mapper");


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
        listParameter.setName(parameterName+"List");
        listNodeList.add(listParameter);

        mapperClass.addMethod("insert" + entityName).setParameters(nodeList).setType(Type.NODE).setBody(null);
        mapperClass.addMethod("insert" + entityName+"List").setParameters(listNodeList).setType(Type.NODE).setBody(null);
        mapperClass.addMethod("update" + entityName).setType(Type.NODE).setBody(null).setParameters(nodeList);
        mapperClass.addMethod("updateSelective").setType(Type.NODE).setBody(null).setParameters(nodeList);
        mapperClass.addMethod("query" + entityName).setBody(null).setType("List<" + entityName + ">").setParameters(nodeList);
        mapperClass.addMethod("queryByPrimaryKey").setBody(null).setType(entityName).setParameters(keyParameterList);
        mapperClass.addMethod("deleteByPrimaryKey").setType(Type.NODE).setBody(null).setParameters(keyParameterList);

        return compilationUnit;
    }
}
