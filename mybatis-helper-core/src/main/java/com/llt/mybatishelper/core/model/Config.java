package com.llt.mybatishelper.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author LILONGTAO
 */
@Data
public class Config {

    private String dbType;

    private String database;

    private String baseDbUrl;

    private String baseDbUsername;

    private String baseDbPassword;

    private Boolean useDb;

    private List<EntityField> baseEntityFieldList;

    private List<BuildConfig> buildConfigList;

    private String charset;
}
