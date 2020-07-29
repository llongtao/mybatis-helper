package com.llt.mybatishelper.view.controller;

import com.alibaba.fastjson.JSON;
import com.llt.mybatishelper.core.data.DataSourceHolder;
import com.llt.mybatishelper.core.model.BuildConfig;
import com.llt.mybatishelper.core.model.BuildResult;
import com.llt.mybatishelper.core.model.Config;
import com.llt.mybatishelper.core.start.MyBatisHelperStarter;
import com.llt.mybatishelper.core.utils.CollectionUtils;
import com.llt.mybatishelper.core.utils.FileUtils;
import com.llt.mybatishelper.core.utils.StringUtils;
import com.llt.mybatishelper.view.component.AddCell;
import com.llt.mybatishelper.view.component.DeleteCell;
import com.llt.mybatishelper.view.component.FolderSelectTableCell;
import com.llt.mybatishelper.view.utils.BeanUtil;
import com.llt.mybatishelper.view.vo.ConfigVO;
import com.llt.mybatishelper.view.vo.EntityFieldVO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.llt.mybatishelper.core.constants.Constants.CONFIG_FILE_NAME;
import static com.llt.mybatishelper.core.constants.Constants.MYSQL;
import static com.llt.mybatishelper.core.constants.Constants.MYSQL_DRIVER;

/**
 * @author LILONGTAO
 * @date 2020-04-22
 */
@Slf4j
@Getter
public class Controller {
    public static Stage primaryStage;

    @FXML
    private CheckBox useDb;

    @FXML
    private ChoiceBox<String> dbType;

    @FXML
    private TextField baseDbUsername;

    @FXML
    private PasswordField baseDbPassword;

    @FXML
    private Button start;

    @FXML
    private TextField baseDbUrl;


    @FXML
    private TableView<ConfigVO> buildConfig;

    @FXML
    private TableColumn<ConfigVO, String> entityFolder;

    @FXML
    private TableColumn<ConfigVO, String> mapperFolder;

    @FXML
    private TableColumn<ConfigVO, String> xmlFolder;

    @FXML
    private TableColumn<ConfigVO, Boolean> useBaseField;

    @FXML
    private TableColumn<ConfigVO, String> db;

    @FXML
    private TableColumn<ConfigVO, Boolean> configAdd;

    @FXML
    private TableColumn<ConfigVO, Boolean> configDelete;


    @FXML
    private TableView<EntityFieldVO> baseModel;


    @FXML
    private TableColumn<EntityFieldVO, String> name;

    @FXML
    private TableColumn<EntityFieldVO, String> columnName;

    @FXML
    private TableColumn<EntityFieldVO, String> type;

    @FXML
    private TableColumn<EntityFieldVO, String> length;

    @FXML
    private TableColumn<EntityFieldVO, String> defaultValue;

    @FXML
    private TableColumn<EntityFieldVO, Boolean> noNull;

    @FXML
    private TableColumn<EntityFieldVO, String> description;

    @FXML
    private TableColumn<EntityFieldVO, Boolean> actionAdd;

    @FXML
    private TableColumn<EntityFieldVO, Boolean> actionDelete;

    public void init() {
        configBaseModelTableView();
        configTreeTableView();
        configUseDb();
        configStart();
        configDbType();
        log.info("controller init success");
    }

    private void configBaseModelTableView() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setEditable(true);
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(evt -> {
            evt.getRowValue().setName(evt.getNewValue());
            evt.getRowValue().setColumnName(StringUtils.transformUnderline(evt.getNewValue()));
            save();
        });

        columnName.setCellValueFactory(new PropertyValueFactory<>("columnName"));
        columnName.setEditable(true);
        columnName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnName.setOnEditCommit(evt -> {
            evt.getRowValue().setColumnName(evt.getNewValue());
            save();
        });

        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        type.setEditable(true);
        type.setCellFactory(TextFieldTableCell.forTableColumn());
        type.setOnEditCommit(evt -> {
            evt.getRowValue().setType(evt.getNewValue());
            save();
        });

        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        length.setEditable(true);
        length.setCellFactory(TextFieldTableCell.forTableColumn());
        length.setOnEditCommit(evt -> {
            evt.getRowValue().setLength(evt.getNewValue());
            save();
        });

        defaultValue.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
        defaultValue.setEditable(true);
        defaultValue.setCellFactory(TextFieldTableCell.forTableColumn());
        defaultValue.setOnEditCommit(evt -> {
            evt.getRowValue().setDefaultValue(evt.getNewValue());
            save();
        });


        noNull.setCellValueFactory(new PropertyValueFactory<>("noNull"));
        noNull.setEditable(true);
        noNull.setCellFactory(CheckBoxTableCell.forTableColumn(noNull));
        noNull.setOnEditCommit(evt -> {
            evt.getRowValue().setNoNull(evt.getNewValue());
            save();
        });

        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        description.setEditable(true);
        description.setCellFactory(TextFieldTableCell.forTableColumn());
        description.setOnEditCommit(evt -> {
            evt.getRowValue().setDescription(evt.getNewValue());
            save();
        });


        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        actionAdd.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        actionAdd.setCellFactory(personBooleanTableColumn -> new AddCell<>(primaryStage, baseModel, EntityFieldVO.class));

        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        actionDelete.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        actionDelete.setCellFactory(personBooleanTableColumn -> new DeleteCell<>(primaryStage, baseModel));

    }

    private void configTreeTableView() {
        entityFolder.setCellValueFactory(new PropertyValueFactory<>("entityFolder"));
        entityFolder.setEditable(true);
        entityFolder.setCellFactory(FolderSelectTableCell.forTableColumn());
        entityFolder.setOnEditCommit(evt -> {
            evt.getRowValue().setEntityFolder(evt.getNewValue());
            save();
        });

        mapperFolder.setCellValueFactory(new PropertyValueFactory<>("mapperFolder"));
        mapperFolder.setEditable(true);
        mapperFolder.setCellFactory(FolderSelectTableCell.forTableColumn());
        mapperFolder.setOnEditCommit(evt -> {
            evt.getRowValue().setMapperFolder(evt.getNewValue());
            save();
        });

        xmlFolder.setCellValueFactory(new PropertyValueFactory<>("xmlFolder"));
        xmlFolder.setEditable(true);
        xmlFolder.setCellFactory(FolderSelectTableCell.forTableColumn());
        xmlFolder.setOnEditCommit(evt -> {
            evt.getRowValue().setXmlFolder(evt.getNewValue());
            save();
        });
        useBaseField.setCellValueFactory(new PropertyValueFactory<>("useBaseField"));
        useBaseField.setEditable(true);
        useBaseField.setCellFactory(CheckBoxTableCell.forTableColumn(useBaseField));
        useBaseField.setOnEditCommit(evt -> {
            evt.getRowValue().setUseBaseField(evt.getNewValue());
            save();
        });
        db.setCellValueFactory(new PropertyValueFactory<>("db"));
        db.setEditable(true);
        db.setCellFactory(TextFieldTableCell.forTableColumn());
        db.setOnEditCommit(evt -> {
            evt.getRowValue().setDb(evt.getNewValue());
            save();
        });


        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        configAdd.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        configAdd.setCellFactory(personBooleanTableColumn -> new AddCell<>(primaryStage, buildConfig, ConfigVO.class));

        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        configDelete.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        configDelete.setCellFactory(personBooleanTableColumn -> new DeleteCell<>(primaryStage, buildConfig));


    }

    private void configUseDb() {
        useDb.setVisible(true);
        useDb.selectedProperty().addListener((l, o, n) -> {
            if (n) {
                baseDbUrl.setDisable(false);
                baseDbUsername.setDisable(false);
                baseDbPassword.setDisable(false);
                db.setEditable(true);
            } else {
                baseDbUrl.setDisable(true);
                baseDbUsername.setDisable(true);
                baseDbPassword.setDisable(true);
                db.setEditable(false);
            }
            save();
        });

    }

    private void configStart() {
        start.setOnMouseClicked(event -> {
            start.setDisable(true);
            try {
                Config config = save();
                checkStartConfigAndConfigDataSource(config);

                BuildResult run = MyBatisHelperStarter.db(config.getDbType()).run(config);
                if (run.isSucceed()) {
                    new Alert(Alert.AlertType.NONE, "已生成,请查看指定目录下base文件夹", new ButtonType[]{ButtonType.CLOSE}).show();
                }else {
                    log.warn("生成异常",run.getE());
                    new Alert(Alert.AlertType.ERROR, run.getE().getMessage(), new ButtonType[]{ButtonType.CLOSE}).show();
                }
                log.info(JSON.toJSONString(run.getLogs()));

            } catch (Exception e) {
                log.warn("生成异常",e);
                new Alert(Alert.AlertType.ERROR, e.getMessage(), new ButtonType[]{ButtonType.CLOSE}).show();
            }
            DataSourceHolder.clear();
            start.setDisable(false);
            log.info("SUCCESS");
        });
    }

    private void checkStartConfigAndConfigDataSource(Config config) {
        boolean useDb = Objects.equals(config.getUseDb(), true);
        List<BuildConfig> buildConfigList = config.getBuildConfigList();
        if (CollectionUtils.isEmpty(buildConfigList)) {
            throw new IllegalArgumentException("配置信息不能为空");
        }
        for (BuildConfig buildConfig1 : buildConfigList) {
            if (StringUtils.isEmpty(buildConfig1.getMapperFolder()) ||
                    StringUtils.isEmpty(buildConfig1.getEntityFolder()) ||
                    StringUtils.isEmpty(buildConfig1.getXmlFolder())
            ) {
                throw new IllegalArgumentException("配置文件夹信息不能为空");
            }
        }


        if (useDb) {
            if (StringUtils.isEmpty(config.getBaseDbUrl()) ||
                    StringUtils.isEmpty(config.getBaseDbPassword()) ||
                    StringUtils.isEmpty(config.getBaseDbUsername()) ||
                    StringUtils.isEmpty(config.getDbType())
            ) {
                throw new IllegalArgumentException("若生成表结构则数据库信息必填");
            }
            boolean matches = Pattern.matches("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]):([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$"
                    , config.getBaseDbUrl());
            if (!matches) {
                throw new IllegalArgumentException("数据库地址应以 ip:port 的形式填写");
            }



        }
    }

    private void configDbType() {
        dbType.setItems(FXCollections.observableArrayList(MYSQL));
    }

    public Config save() {
        Config config = new Config();
        config.setBaseDbUrl(baseDbUrl.getText());

        if (Objects.equals(config.getDbType(), MYSQL)) {
            config.setBaseDbDriverClassName(MYSQL_DRIVER);
        }

        //TODO else 其他数据库

        config.setDbType(dbType.getValue());
        config.setUseDb(useDb.isSelected());
        config.setBaseDbUsername(baseDbUsername.getText());
        config.setBaseDbPassword(baseDbPassword.getText());
        config.setBaseEntityFieldList(baseModel.getItems().stream().map(BeanUtil::entityfieldvo2Entityfield).collect(Collectors.toList()));
        config.setBuildConfigList(buildConfig.getItems().stream().map(BeanUtil::configvo2Buildconfig).collect(Collectors.toList()));

        log.info("saveConfig:{}",config);
        FileUtils.serialization(config, CONFIG_FILE_NAME);
        return config;
    }

    public void loadData() {

        Config config = null;
        try {
            String configStr = FileUtils.readFileToString(CONFIG_FILE_NAME);
            if (configStr == null) {
                configStr = FileUtils.readFileToString(Objects.requireNonNull(getClass().getClassLoader().getResource(CONFIG_FILE_NAME)).getPath());
            }
            config = JSON.parseObject(configStr, Config.class);
        } catch (Exception e) {
            log.warn("找不到配置文件",e);
        }
        log.info("loadData:{}",config);

        if (config == null || CollectionUtils.isEmpty(config.getBaseEntityFieldList())) {
            baseModel.setItems(FXCollections.observableArrayList(new EntityFieldVO()));
        } else {
            baseModel.setItems(FXCollections.observableArrayList(config.getBaseEntityFieldList().stream().map(EntityFieldVO::new).collect(Collectors.toList())));
        }
        if (config == null || CollectionUtils.isEmpty(config.getBuildConfigList())) {
            buildConfig.setItems(FXCollections.observableArrayList(new ConfigVO()));
        } else {
            buildConfig.setItems(FXCollections.observableArrayList(config.getBuildConfigList().stream().map(ConfigVO::new).collect(Collectors.toList())));
        }
        if (config != null) {
            this.dbType.setValue(config.getDbType());
            this.baseDbUrl.setText(config.getBaseDbUrl());
            this.baseDbUsername.setText(config.getBaseDbUsername());
            this.baseDbPassword.setText(config.getBaseDbPassword());
            this.useDb.setSelected(Objects.equals(config.getUseDb(), true));
        }
    }

    private static Controller instance;

    public static Controller getInstance() {
        return instance;
    }

    public static void setInstance(Controller controller) {
        Controller.instance = controller;
    }

}
