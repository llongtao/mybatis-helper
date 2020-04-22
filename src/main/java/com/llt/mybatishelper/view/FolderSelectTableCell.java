package com.llt.mybatishelper.view;

import com.llt.mybatishelper.controller.Controller;
import com.llt.mybatishelper.utils.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
public class FolderSelectTableCell<S, T> extends TableCell<S, T> {
    private final static StringConverter<?> DEFAULT_STRING_CONVERTER = new StringConverter<Object>() {
        @Override public String toString(Object t) {
            return t == null ? null : t.toString();
        }

        @Override public Object fromString(String string) {
            return string;
        }
    };


    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(null, null);
    }


    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty) {
        return forTableColumn(getSelectedProperty, null);
    }


    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty,
            final boolean showLabel) {
        StringConverter<T> converter = !showLabel ?
                null : defaultStringConverter();
        return forTableColumn(getSelectedProperty, converter);
    }


    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty,
            final StringConverter<T> converter) {
        return list -> new FolderSelectTableCell<S, T>(getSelectedProperty, converter);
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

    // --- converter
    private ObjectProperty<StringConverter<T>> converter =
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


    // --- selected state callback property
    private ObjectProperty<Callback<Integer, ObservableValue<String>>>
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


    /***************************************************************************
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
        //super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            StringConverter<T> c = getConverter();

            if (showLabel) {
                setText(c.toString(item));
            }

            String cellData =null;
            ObservableValue<?> selectedProperty = getSelectedProperty();
            if (selectedProperty != null && selectedProperty.getValue()!=null) {
                cellData = Objects.toString( getSelectedProperty().getValue());
            }
            if (StringUtils.isEmpty(cellData)) {
                cellData = "选择文件夹";
            }
            btn.setText(cellData);
            String dir = cellData;
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
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
                }
            });
            setGraphic(btn);
        }
    }
    private void preUpdate(T item){
        super.updateItem(item, false);
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

    @SuppressWarnings("unchecked")
    static <T> StringConverter<T> defaultStringConverter() {
        return (StringConverter<T>) DEFAULT_STRING_CONVERTER;
    }

}