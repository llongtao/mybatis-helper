package top.aexp.mybatishelper.core.utils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

/**
 * @author LILONGTAO
 */
public class TemplateUtils {
    //创建一个freemarker.template.Configuration实例，它是存储 FreeMarker 应用级设置的核心部分
    //指定版本号
    private static final Configuration cfg;

    static {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(TemplateUtils.class, "/templates");
        //设置模板目录
        cfg.setTemplateLoader(classTemplateLoader);
        //设置默认编码格式
        cfg.setDefaultEncoding("UTF-8");
    }

    public static String out(Map<String, Object> model, String template) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer out = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));
        //从设置的目录中获得模板
        Template temp;
        try {
            temp = cfg.getTemplate(template);
            temp.process(model, out);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toString();
    }

    public static void main(String[] args) {

    }

}
