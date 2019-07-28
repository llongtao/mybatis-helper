package com.llt.mybatishelper.model;

import lombok.Data;

import java.util.List;

@Data
public class Config {
    private String baseDbUrl;

    private String baseDbUsername;

    private String baseDbPassword;

    private Boolean useLombok;

    private String baseDbDriverClassName;

    List<BuildConfig> buildConfigList;
}
