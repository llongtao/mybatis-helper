package com.llt.mybatishelper.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author LILONGTAO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
