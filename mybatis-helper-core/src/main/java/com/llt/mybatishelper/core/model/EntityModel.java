package com.llt.mybatishelper.core.model;

import lombok.Data;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.llt.mybatishelper.core.constants.Constants.DOT;

/**
 * @author LILONGTAO
 */
@Data
public class EntityModel {

    private String packageName;

    private String mapperClassName;

    private String mapperPackage;

    private String baseMapperClassName;

    private String baseMapperPackage;

    private String mapperName;

    private String baseMapperName;

    private String entityClassName;

    private String entityName;

    private String className;

    private String tableName;

    private String description;

    private List<EntityField> primaryKeyList;

    private List<EntityField> columnList;

    private boolean isNew = true;


}
