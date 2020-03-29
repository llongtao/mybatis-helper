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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 * @date 2019-07-25
 */
public class Main extends Application {


    public static void main(String[] args) throws IOException {
        launch(args);



    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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
        primaryStage.setScene(new Scene(root, 990, 524));

        controller.loadData();
        primaryStage.show();
    }


    /**
     * shows a dialog which displays a UI for adding a person to a table.
     *
     * @param parent a parent stage to which this dialog will be modal and placed next to.
     * @param table  the table to which a person is to be added.
     * @param y      the y position of the top left corner of the dialog.
     */
    private void showAddPersonDialog(Stage parent, final TableView<EntityFieldVO> table, double y) {
        // initialize the dialog.
        final Stage dialog = new Stage();
        dialog.setTitle("New Person");
        dialog.initOwner(parent);  //对话框永远在前面
        dialog.initModality(Modality.WINDOW_MODAL);  //必须关闭对话框后才能操作其他的
        dialog.initStyle(StageStyle.UTILITY); //对话框-只保留关闭按钮
        dialog.setX(parent.getX() + parent.getWidth());
        dialog.setY(y);

        // create a grid for the data entry.
        GridPane grid = new GridPane();
        final TextField firstNameField = new TextField();
        final TextField lastNameField = new TextField();
        grid.addRow(0, new Label("First Name"), firstNameField);
        grid.addRow(1, new Label("Last Name"), lastNameField);
        grid.setHgap(10);
        grid.setVgap(10);
        GridPane.setHgrow(firstNameField, Priority.ALWAYS);
        GridPane.setHgrow(lastNameField, Priority.ALWAYS);

        // create action buttons for the dialog.
        Button ok = new Button("OK");
        ok.setDefaultButton(true);
        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);

        // only enable the ok button when there has been some text entered.
        ok.disableProperty().bind(firstNameField.textProperty().isEqualTo("").or(lastNameField.textProperty().isEqualTo("")));

        // add action handlers for the dialog buttons.
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int nextIndex = table.getSelectionModel().getSelectedIndex() + 1;
                table.getItems().add(nextIndex, new EntityFieldVO());
                table.getSelectionModel().select(nextIndex);
                dialog.close();
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                dialog.close();
            }
        });

        // layout the dialog.
        HBox buttons = HBoxBuilder.create().spacing(10).children(ok, cancel).alignment(Pos.CENTER_RIGHT).build();
        VBox layout = new VBox(10);
        layout.getChildren().addAll(grid, buttons);
        layout.setPadding(new Insets(5));
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

}

