package top.aexp.mybatishelper.view.component;

import top.aexp.mybatishelper.view.controller.Controller;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author LILONGTAO
 * @date 2020-04-22
 */
public class  AddCell<T> extends TableCell<T, Boolean> {

    /**
     *  a button for adding a new person.
     */
    final Button addButton = new Button("+");

    /**
     *  pads and centers the add button in the cell.
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
    public AddCell(final Stage stage, final TableView table, Object model) {
        paddedButton.getChildren().add(addButton);
        addButton.setOnMousePressed(mouseEvent -> buttonY.set(mouseEvent.getScreenY()));
        addButton.setOnAction(actionEvent -> {
            table.getSelectionModel().select(getTableRow().getIndex());
            int nextIndex = table.getSelectionModel().getSelectedIndex() + 1;
            try {
                table.getItems().add(nextIndex, model);
                Controller.getInstance().save();
            } catch (Exception ignore) {
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
