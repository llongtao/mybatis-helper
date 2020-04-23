package com.llt.mybatishelper;

import com.alibaba.fastjson.JSON;
import com.llt.mybatishelper.controller.Controller;
import com.llt.mybatishelper.model.BuildConfig;
import com.llt.mybatishelper.model.Config;
import com.llt.mybatishelper.model.EntityField;
import com.llt.mybatishelper.utils.CollectionUtils;
import com.llt.mybatishelper.utils.FileUtils;
import com.llt.mybatishelper.view.vo.ConfigVO;
import com.llt.mybatishelper.view.vo.EntityFieldVO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author LILONGTAO
 * @date 2019-07-25
 */
@Slf4j
public class Main extends Application {


    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("application start");
        Controller.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL location = getClass().getClassLoader().getResource("helperGUI.fxml");
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        assert location != null;
        Parent root = fxmlLoader.load(location.openStream());
        Controller controller = fxmlLoader.getController();
        Controller.setInstance(controller);
        TableView<EntityFieldVO> table = controller.getBaseModel();
        TableView<ConfigVO> buildConfig = controller.getBuildConfig();
        table.setEditable(true);
        buildConfig.setEditable(true);
        controller.init();

        primaryStage.setTitle("mybatis生成器");
        primaryStage.setScene(new Scene(root, 1040, 524));

        controller.loadData();
        primaryStage.show();
    }



}

