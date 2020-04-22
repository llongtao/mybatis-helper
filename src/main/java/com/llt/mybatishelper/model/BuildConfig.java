package com.llt.mybatishelper.model;

import com.llt.mybatishelper.view.vo.ConfigVO;
import lombok.Data;

/**
 * @author LILONGTAO
 */
@Data
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

    public BuildConfig(ConfigVO configVO){
        this.entityFolder = configVO.getEntityFolder();
        this.mapperFolder = configVO.getMapperFolder();
        this.xmlFolder = configVO.getXmlFolder();
        this.db = configVO.getDb();
        this.ignoreBaseField = !configVO.isUseBaseField();
    }
}
