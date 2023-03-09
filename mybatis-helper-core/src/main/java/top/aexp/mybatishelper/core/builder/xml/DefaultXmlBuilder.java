package top.aexp.mybatishelper.core.builder.xml;

import top.aexp.mybatishelper.core.model.EntityModel;
import top.aexp.mybatishelper.core.utils.TemplateUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LILONGTAO
 * @date 2019-07-30
 */
public class DefaultXmlBuilder implements XmlBuilder {


    @Override
    public String build(EntityModel entityModel, String split) {
        Map<String,Object> param = new HashMap<>();
        param.put("now", LocalDateTime.now());
        param.put("entity",entityModel);
        param.put("split",split);
        return TemplateUtils.out(param, "BaseXml.ftl");

    }


    @Override
    public String buildEmpty(EntityModel entityModel) {
        Map<String,Object> param = new HashMap<>();
        param.put("now", LocalDateTime.now());
        param.put("entity",entityModel);
        return TemplateUtils.out(param, "EmptyXml.ftl");
    }

}
