package top.aexp.mybatishelper.core.builder.mapper;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import top.aexp.mybatishelper.core.model.EntityField;
import top.aexp.mybatishelper.core.model.EntityModel;
import top.aexp.mybatishelper.core.utils.StringUtils;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class DefaultMapperBuilder implements MapperBuilder{




    @Override
    public  CompilationUnit build(EntityModel entityModel) {

        String mapperPackage = entityModel.getBaseMapperPackage();
        String entityClassName = entityModel.getEntityClassName();
        String className = entityModel.getBaseMapperName();
        String entityName = entityModel.getEntityName();
        String classNm = entityModel.getClassName();

        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(mapperPackage);
        compilationUnit.addImport(IMPORT_JAVA_LANG);
        compilationUnit.addImport(IMPORT_LIST);
        compilationUnit.addImport(IMPORT_ANNOTATIONS_MAPPER);
        compilationUnit.addImport(entityClassName);
        MarkerAnnotationExpr mapperAnnotationExpr = new MarkerAnnotationExpr(MAPPER);
        ClassOrInterfaceDeclaration mapperClass = compilationUnit
                .addClass(className)
                .setPublic(true)
                .setInterface(true)
                .addAnnotation(mapperAnnotationExpr);
        mapperClass.setComment(new JavadocComment("@author MybatisHelper " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));


        NodeList<Parameter> nodeList = new NodeList<>();
        Parameter parameter = new Parameter();
        String parameterName = StringUtils.firstToLower(entityModel.getEntityName());
        parameter.setType(classNm);
        assert parameterName != null;
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
        listParameter.setType("List<" + classNm + ">");
        listParameter.setName(parameterName + LIST);
        listNodeList.add(listParameter);

        mapperClass.addMethod(INSERT + entityName).setParameters(nodeList).setType(Type.NODE).setBody(null)
                .setComment(new JavadocComment("插入\n"+"@param "+parameter.getName()+" 需要插入的实体\n"+"@return 修改行数"));

        mapperClass.addMethod(INSERT + entityName + LIST).setParameters(listNodeList).setType(Type.NODE).setBody(null)
                .setComment(new JavadocComment("批量插入\n"+"@param "+listParameter.getName()+" 需要插入的实体列表\n"+"@return 修改行数"));

        mapperClass.addMethod(UPDATE + entityName).setType(Type.NODE).setBody(null).setParameters(nodeList)
                .setComment(new JavadocComment("更新\n"+"@param "+parameter.getName()+" 需要更新的实体\n"+"@return 修改行数"));

        mapperClass.addMethod(UPDATE + entityName+LIST).setType(Type.NODE).setBody(null).setParameters(listNodeList)
                .setComment(new JavadocComment("批量更新\n"+"@param "+listParameter.getName()+" 需要更新的实体列表\n"+"@return 修改行数"));

        mapperClass.addMethod(UPDATE_SELECTIVE).setType(Type.NODE).setBody(null).setParameters(nodeList)
                .setComment(new JavadocComment("修改有值的列\n"+"@param "+parameter.getName()+" 需要修改的实体\n"+"@return 修改行数"));

        mapperClass.addMethod(QUERY + entityName).setBody(null).setType("List<" + classNm + ">").setParameters(nodeList)
                .setComment(new JavadocComment("查询\n"+"@param "+parameter.getName()+" 查询条件实体\n"+"@return 列表"));

        StringBuilder keyParams = new StringBuilder();
        keyParameterList.forEach(key->keyParams.append("@param ").append(key.getName()).append(" 主键\n"));

        mapperClass.addMethod(QUERY_BY_PRIMARY_KEY).setBody(null).setType(classNm).setParameters(keyParameterList)
                .setComment(new JavadocComment("根据id查询\n"+keyParams+"@return 实体"));

        mapperClass.addMethod(DELETE_BY_PRIMARY_KEY).setType(Type.NODE).setBody(null).setParameters(keyParameterList)
                .setComment(new JavadocComment("根据id删除\n"+keyParams+"@return 修改行数"));


        return compilationUnit;
    }

    @Override
    public  CompilationUnit addExtend(String mapperClassStr, String baseMapperName) {

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

    @Override
    public  CompilationUnit buildEmpty(EntityModel entityModel) {
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(entityModel.getMapperPackage());
        compilationUnit.addImport(IMPORT_ANNOTATIONS_MAPPER);
        compilationUnit.addImport(entityModel.getBaseMapperClassName());

        ClassOrInterfaceDeclaration mapperClass = compilationUnit
                .addClass(entityModel.getMapperName())
                .setPublic(true)
                .setInterface(true)
                .addAnnotation(new MarkerAnnotationExpr(MAPPER));
        mapperClass.setComment(new JavadocComment("@author MybatisHelper " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        mapperClass.addExtendedType(entityModel.getBaseMapperName());
        mapperClass.addOrphanComment(new LineComment(TIPS));
        return compilationUnit;
    }
}
