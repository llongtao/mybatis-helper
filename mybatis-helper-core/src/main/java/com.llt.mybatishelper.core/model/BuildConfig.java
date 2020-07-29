package com.llt.mybatishelper.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LILONGTAO
 */
@Data
@AllArgsConstructor
public class BuildConfig {

    private String entityFolder;

    private String mapperFolder;

    private String xmlFolder;

    private String dbUrl;

    private String db;

    private String dbUsername;

    private String dbPassword;

    private Boolean ignoreBaseField;

    public BuildConfig(){
    }


}
