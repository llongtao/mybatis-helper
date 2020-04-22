package com.llt.mybatishelper.view;

import com.llt.mybatishelper.controller.Controller;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author LILONGTAO
 * @date 2020-04-22
 */
public class DeleteCell<T> extends TableCell<T, Boolean> {
    /**
     * 删除按钮
     */
    final Button deleteButton = new Button("-");
    /**
     * pads and centers the add button in the cell.
     */
    final StackPane paddedButton = new StackPane();
    /**
     * records the y pos of the last button press so that the add person dialog can be shown next to the cell.
     */
    final DoubleProperty buttonY = new SimpleDoubleProperty();

    /**
     * AddPersonCell constructor
     *
     * @param stage the stage in which the table is placed.
     * @param table the table to which a new person can be added.
     */
    public DeleteCell(final Stage stage, final TableView table) {

        paddedButton.getChildren().add(deleteButton);
        deleteButton.setOnMousePressed(mouseEvent -> buttonY.set(mouseEvent.getScreenY()));
        deleteButton.setOnAction(actionEvent -> {
            int size = table.getItems().size();
            if (size>1){
                table.getSelectionModel().select(getTableRow().getIndex());
                int index = table.getSelectionModel().getSelectedIndex();
                table.getItems().remove(index);
                Controller.getInstance().save();
            }
        });
    }

    /**
     * places an add button in the row only if the row is not empty.
     */
    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(paddedButton);
        } else {
            setGraphic(null);
        }
    }
}
