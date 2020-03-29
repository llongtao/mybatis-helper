package com.llt.mybatishelper.model;

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

    private String baseDbDriverClassName;

    private Boolean useDb;

    private List<EntityField> baseEntityFieldList;

    private List<BuildConfig> buildConfigList;


}
