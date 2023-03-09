package top.aexp.mybatishelper.core.builder.mapper;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import top.aexp.mybatishelper.core.model.EntityField;
import top.aexp.mybatishelper.core.model.EntityModel;
import top.aexp.mybatishelper.core.utils.CollectionUtils;
import top.aexp.mybatishelper.core.utils.TemplateUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class DefaultMapperBuilder implements MapperBuilder{




    @Override
    public  String build(EntityModel entityModel) {

        String basePackage = entityModel.getMapperPackage()+".base";
        Map<String,Object> param = new HashMap<>();
        param.put("basePackage",basePackage);
        param.put("entityClassPath",basePackage);
        String pkType = "Integer";
        List<EntityField> primaryKeyList = entityModel.getPrimaryKeyList();
        if (!CollectionUtils.isEmpty(primaryKeyList)) {
            pkType = primaryKeyList.get(0).getType();
        }
        param.put("pkType",pkType);
        param.put("now",LocalDateTime.now());
        param.put("entity",entityModel);

        return TemplateUtils.out(param, "BaseMapper.ftl");
    }

    @Override
    public CompilationUnit buildMultiPk(EntityModel entityModel) {
        return null;
    }

    @Override
    public  CompilationUnit addExtend(String mapperClassStr, String baseMapperName,String entityClassName,String pkType) {

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
        String type = baseMapperName+"<"+entityClassName+","+pkType+">";
        classDeclaration.addExtendedType(type);
        return compilationUnit;
    }

    @Override
    public String buildEmpty(EntityModel entityModel, String pkType) {


        Map<String,Object> param = new HashMap<>();
        param.put("now",LocalDateTime.now());
        param.put("entity",entityModel);

        return TemplateUtils.out(param, "EmptyMapper.ftl");
    }
}
