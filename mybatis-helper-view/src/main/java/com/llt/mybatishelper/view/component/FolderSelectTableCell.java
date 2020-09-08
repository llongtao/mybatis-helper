package com.llt.mybatishelper.view.component;

import com.llt.mybatishelper.core.utils.StringUtils;
import com.llt.mybatishelper.view.controller.Controller;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.io.File;
import java.util.Objects;

/**
 * @author LILONGTAO
 * @date 2020-04-22
 */
@SuppressWarnings("rawtypes")
public class FolderSelectTableCell<S, T> extends TableCell<S, T> {

    private static final int SHOW_LEN = 25;


    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(null, null);
    }


    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty,
            final StringConverter<T> converter) {
        return list -> new FolderSelectTableCell<>(getSelectedProperty, converter);
    }


    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/
    private final Button btn = new Button();

    private boolean showLabel;


    /**
     * Creates a CheckBoxTableCell with a custom string converter.
     *
     * @param getSelectedProperty A {@link Callback} that will return a {@link
     *                            ObservableValue} given an index from the TableColumn.
     * @param converter           A StringConverter that, given an object of type T, will return a
     *                            String that can be used to represent the object visually.
     */
    public FolderSelectTableCell(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty,
            final StringConverter<T> converter) {

        setGraphic(null);
        setSelectedStateCallback(getSelectedProperty);
        setConverter(converter);

    }


    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/


    private final ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter") {
                @Override
                protected void invalidated() {
                    updateShowLabel();
                }
            };

    /**
     * The {@link StringConverter} property.
     */
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     */
    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     */
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }



    private final ObjectProperty<Callback<Integer, ObservableValue<String>>>
            selectedStateCallback =
            new SimpleObjectProperty<>(
                    this, "selectedStateCallback");

    /**
     * Property representing the {@link Callback} that is bound to by the
     * CheckBox shown on screen.
     */
    public final ObjectProperty<Callback<Integer, ObservableValue<String>>> selectedStateCallbackProperty() {
        return selectedStateCallback;
    }

    /**
     * Sets the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final void setSelectedStateCallback(Callback<Integer, ObservableValue<String>> value) {
        selectedStateCallbackProperty().set(value);
    }

    /**
     * Returns the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final Callback<Integer, ObservableValue<String>> getSelectedStateCallback() {
        return selectedStateCallbackProperty().get();
    }


    /* *************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateItem(T item, boolean empty) {

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            StringConverter<T> c = getConverter();

            if (showLabel) {
                setText(c.toString(item));
            }

            String path =null;
            String cellData =null;
            ObservableValue<?> selectedProperty = getSelectedProperty();
            if (selectedProperty != null && selectedProperty.getValue()!=null) {
                path = Objects.toString(getSelectedProperty().getValue());
                if (!StringUtils.isEmpty(path)) {
                    if (path.length() > SHOW_LEN) {
                        cellData = "..."+path.substring(path.length()-SHOW_LEN);
                    }else {
                        cellData = path;
                    }
                }
            }
            if (StringUtils.isEmpty(cellData)) {
                cellData = "选择文件夹";
            }
            btn.setText(cellData);
            String dir = path;
            btn.setOnAction(event -> {
                DirectoryChooser chooser = new DirectoryChooser();
                try{
                    File file = new File(dir);
                    if(file.exists()&&file.isDirectory()){
                        chooser.setInitialDirectory(file);
                    }
                }catch (Exception ignore){}

                File chosenDir = chooser.showDialog(Controller.primaryStage);
                if (chosenDir != null) {
                    String absolutePath = chosenDir.getAbsolutePath();
                    btn.setText(absolutePath);
                    TablePosition<S, T> stTablePosition = new TablePosition<>(getTableView(), getIndex(), getTableColumn());
                    TableColumn.CellEditEvent editEvent = new TableColumn.CellEditEvent(
                            getTableView(),
                            stTablePosition,
                            TableColumn.editCommitEvent(),
                            absolutePath
                    );
                    Event.fireEvent(getTableColumn(), editEvent);
                    Controller.getInstance().save();
                } else {
                    System.out.print("no directory chosen");
                }
            });
            setGraphic(btn);
        }
    }


    /***************************************************************************
     *                                                                         *
     * Private implementation                                                  *
     *                                                                         *
     **************************************************************************/

    private void updateShowLabel() {
        this.showLabel = converter != null;
        //this.checkBox.setAlignment(showLabel ? Pos.CENTER_LEFT : Pos.CENTER);
    }

    private ObservableValue<?> getSelectedProperty() {
        return getSelectedStateCallback() != null ?
                getSelectedStateCallback().call(getIndex()) :
                getTableColumn().getCellObservableValue(getIndex());

    }

}