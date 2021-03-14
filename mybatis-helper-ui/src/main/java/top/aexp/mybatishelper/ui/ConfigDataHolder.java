package top.aexp.mybatishelper.ui;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import top.aexp.mybatishelper.core.model.BuildConfig;
import top.aexp.mybatishelper.core.model.Config;
import top.aexp.mybatishelper.core.model.EntityField;
import top.aexp.mybatishelper.core.utils.CollectionUtils;
import top.aexp.mybatishelper.core.utils.FileUtils;
import top.aexp.mybatishelper.core.utils.StringUtils;


import java.sql.JDBCType;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author LILONGTAO
 */
@Slf4j
public class ConfigDataHolder {

    private static Config config;
    private static final List<Consumer<Config>> DATA_CHANGE_CONSUMER_LIST = new ArrayList<>();

    public static void updateBuildConfig(Vector<Vector<?>> dataVector) {

        List<BuildConfig> buildConfigList = new ArrayList<>();
        for (Vector<?> vector : dataVector) {
            BuildConfig buildConfig =
                    new BuildConfig(
                            (String) vector.get(1),
                            (String) vector.get(2),
                            (String) vector.get(3),
                            null,
                            (String) vector.get(5),
                            null,
                            null,
                            !(Boolean) vector.get(4),
                            !(Boolean) vector.get(0)
                    );

            buildConfigList.add(buildConfig);
        }
        config.setBuildConfigList(buildConfigList);
        save();
    }

    public static void updateModelConfig(Vector<Vector<?>> dataVector) {

        List<EntityField> entityFieldList = new ArrayList<>();
        for (Vector<?> vector : dataVector) {
            EntityField entityField =
                    new EntityField(
                            (String) vector.get(0),
                            (String) vector.get(1),
                            (String) vector.get(2),
                            (String) vector.get(2),
                            null,
                            null,
                            false,
                            (String) vector.get(3),
                            (String) vector.get(4),
                            !(Boolean) vector.get(5),
                            (String) vector.get(6),
                            null
                    );

            entityFieldList.add(entityField);
        }
        if (CollectionUtils.isEmpty(entityFieldList)) {
            entityFieldList = defaultFieldList();
        }
        config.setBaseEntityFieldList(entityFieldList);
        save();
    }

    public static void updateBaseConfig(String dbType, String baseDbUrl, String baseDbUsername, String baseDbPassword, Boolean useDb, boolean dropTable) {
        config.setDbType(dbType);
        config.setBaseDbUrl(baseDbUrl);
        config.setBaseDbUsername(baseDbUsername);
        config.setBaseDbPassword(baseDbPassword);
        config.setUseDb(useDb);
        config.setDropTable(dropTable);
        save();
    }


    public static void save() {
        checkConfig();
        String s = JSON.toJSONString(config);
        log.info("saveConfig:" + s);
        FileUtils.serialization(config, getConfigName());
    }

    private static void checkConfig() {
        if (config == null) {
            config = defaultConfig();
        }
        if (CollectionUtils.isEmpty(config.getBuildConfigList())) {
            config.setBuildConfigList(defaultBuildConfigList());
        }
        if (CollectionUtils.isEmpty(config.getBaseEntityFieldList())) {
            config.setBaseEntityFieldList(defaultFieldList());
        }
    }

    private static String getConfigName() {
        return "config.json";
    }

    public static Config getData() {
        DATA_CHANGE_CONSUMER_LIST.forEach(consumer -> consumer.accept(config));
        return config;
    }

    public static void registerDataChangeConsumer(Consumer<Config> configConsumer) {
        DATA_CHANGE_CONSUMER_LIST.add(configConsumer);
    }


    public static void loadConfig(String name) {
        if (name != null) {
            try {
                String configStr = FileUtils.readFileToString(getConfigName(), "utf-8");
                if (!StringUtils.isEmpty(configStr)) {
                    ConfigDataHolder.config = JSON.parseObject(configStr, Config.class);
                }
            } catch (Exception e) {
                log.warn("找不到配置文件", e);
            }
        }


        if (config == null) {
            config = defaultConfig();
        }

        log.info("loadData:" + config);
    }

    public static Config defaultConfig() {
        Config config = new Config();
        config.setUseDb(true);
        config.setBaseDbUsername("root");
        config.setDbType("mysql");
        config.setBaseDbUrl("127.0.0.1:3306");
        config.setBaseEntityFieldList(defaultFieldList());
        config.setBuildConfigList(defaultBuildConfigList());
        return config;
    }

    public static List<EntityField> defaultFieldList() {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(new EntityField("id", "id", "Integer", "Integer", JDBCType.INTEGER, null, true, null, null, false, "主键", null));
    }

    public static List<BuildConfig> defaultBuildConfigList() {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(new BuildConfig(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, true, false));
    }

}
