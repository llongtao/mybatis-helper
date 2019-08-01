package com.llt.mybatishelper;

import com.alibaba.fastjson.JSON;
import com.llt.mybatishelper.model.Config;
import com.llt.mybatishelper.service.MyBatisHelperFactory;
import com.llt.mybatishelper.utils.FileUtils;
import com.llt.mybatishelper.utils.StringUtils;

import java.net.URL;

/**
 * @author LILONGTAO
 * @date 2019-07-25
 */
public class Main {

    public static void main(String[] args)  {

        URL resource = Main.class.getClassLoader().getResource("config.json");
        assert resource != null;
        String configStr = FileUtils.readFileToString(resource.getPath());
        Config config = JSON.parseObject(configStr, Config.class);
        System.out.println(config);

        String dbUrl = config.getBaseDbUrl();
        if (StringUtils.isEmpty(dbUrl) ) {
            dbUrl = config.getBuildConfigList().get(0).getDbUrl();
        }

        MyBatisHelperFactory.getMybatisHelper(dbUrl).run(config);
        System.out.println("SUCCESS");
    }
}
