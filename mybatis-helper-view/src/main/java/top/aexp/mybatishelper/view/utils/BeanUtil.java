package top.aexp.mybatishelper.view.utils;

import top.aexp.mybatishelper.core.model.BuildConfig;
import top.aexp.mybatishelper.core.model.EntityField;
import top.aexp.mybatishelper.view.vo.ConfigVO;
import top.aexp.mybatishelper.view.vo.EntityFieldVO;

public class BeanUtil {

    public static BuildConfig configvo2Buildconfig(ConfigVO configVO){
        BuildConfig buildConfig = new BuildConfig();
        buildConfig.setEntityFolder(configVO.getEntityFolder());
        buildConfig.setMapperFolder(configVO.getMapperFolder());
        buildConfig.setXmlFolder(configVO.getXmlFolder());
        buildConfig.setDb(configVO.getDb());
        buildConfig.setIgnoreBaseField(!configVO.isUseBaseField());
        buildConfig.setDisable(!configVO.isEnable());
       return buildConfig;
    }
    public static EntityField entityfieldvo2Entityfield(EntityFieldVO item){
        EntityField entityField = new EntityField();
        entityField.setName(item.getName());
        entityField.setColumnName(item.getColumnName());
        entityField.setType(item.getType());
        entityField.setLength(item.getLength());
        entityField.setDefaultValue(item.getDefaultValue());
        entityField.setNullable(!item.isNoNull());
        entityField.setDescription(item.getDescription());
        return entityField;
    }



}
