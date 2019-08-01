package com.llt.mybatishelper.model;

import lombok.Data;

import java.util.List;

/**
 * @author LILONGTAO
 */
@Data
public class BuildConfig {

    private String entityFolder;

    private String mapperFolder;

    private String xmlFolder;

    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

}
