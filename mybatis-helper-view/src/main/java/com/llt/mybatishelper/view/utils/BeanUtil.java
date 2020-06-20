package com.llt.mybatishelper.view.utils;

import com.llt.mybatishelper.core.model.BuildConfig;
import com.llt.mybatishelper.core.model.EntityField;
import com.llt.mybatishelper.view.vo.ConfigVO;
import com.llt.mybatishelper.view.vo.EntityFieldVO;

public class BeanUtil {

    public static BuildConfig configvo2Buildconfig(ConfigVO configVO){
        BuildConfig buildConfig = new BuildConfig();
        buildConfig.setEntityFolder(configVO.getEntityFolder());
        buildConfig.setMapperFolder(configVO.getMapperFolder());
        buildConfig.setXmlFolder(configVO.getXmlFolder());
        buildConfig.setDb(configVO.getDb());
        buildConfig.setIgnoreBaseField(!configVO.isUseBaseField());
       return buildConfig;
    }
    public static EntityField entityfieldvo2Entityfield(EntityFieldVO item){
        EntityField entityField = new EntityField();
        entityField.setName(item.getName());
        entityField.setColumnName(item.getColumnName());
        entityField.setType(item.getType());
        entityField.setLength(String.valueOf(item.getLength()));
        entityField.setDefaultValue(item.getDefaultValue());
        entityField.setNullable(!item.isNoNull());
        entityField.setDescription(item.getDescription());
        return entityField;
    }



}
