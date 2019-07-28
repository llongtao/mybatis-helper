package com.llt.mybatishelper;



import com.alibaba.fastjson.JSON;
import com.llt.mybatishelper.model.Config;
import com.llt.mybatishelper.service.MybatisHelper;
import com.llt.mybatishelper.utils.FileUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {

        URL resource = Main.class.getClassLoader().getResource("config.json");
        assert resource != null;
        String configStr = FileUtils.readFileToString(resource.getPath());
        Config config = JSON.parseObject(configStr, Config.class);
        System.out.println(config);

        MybatisHelper.run(config);
    }
}
