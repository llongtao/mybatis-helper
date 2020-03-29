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

public class FolderSelectTreeTableCell<S> extends TreeTableCell<S,String> {
    private final static StringConverter<?> defaultStringConverter = new StringConverter<Object>() {
        @Override public String toString(Object t) {
            return t == null ? null : t.toString();
        }

        @Override public Object fromString(String string) {
            return (Object) string;
        }
    };

    public static <S> Callback<TreeTableColumn<S,String>, TreeTableCell<S,String>> forTreeTableColumn() {
        return forTreeTableColumn(null, null);
    }
    
    public static <S> Callback<TreeTableColumn<S,String>, TreeTableCell<S,String>> forTreeTableColumn(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty) {
        return forTreeTableColumn(getSelectedProperty, null);
    }

  
    public static <S> Callback<TreeTableColumn<S,String>, TreeTableCell<S,String>> forTreeTableColumn(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty,
            final boolean showLabel) {
        StringConverter<String> converter = ! showLabel ?
                null : defaultStringConverter();
        return forTreeTableColumn(getSelectedProperty, converter);
    }

    public static <S> Callback<TreeTableColumn<S,String>, TreeTableCell<S, String>> forTreeTableColumn(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty,
            final StringConverter<String> converter) {
        return list -> new FolderSelectTreeTableCell<S>(getSelectedProperty, converter);
    }



    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/
    private final Button btn = new Button();

    private boolean showLabel;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a default CheckBoxTreeTableCell.
     */
    public FolderSelectTreeTableCell() {
        this(null, null);
    }

    /**
     * Creates a default CheckBoxTreeTableCell with a custom {@link Callback} to
     * retrieve an ObservableValue for a given cell index.
     *
     * @param getSelectedProperty A {@link Callback} that will return an {@link
     *      ObservableValue} given an index from the TreeTableColumn.
     */
    public FolderSelectTreeTableCell(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty) {
        this(getSelectedProperty, null);
    }

    /**
     * Creates a CheckBoxTreeTableCell with a custom string converter.
     *
     * @param getSelectedProperty A {@link Callback} that will return a {@link
     *      ObservableValue} given an index from the TreeTableColumn.
     * @param converter A StringConverter that, given an object of type T, will return a
     *      String that can be used to represent the object visually.
     */
    public FolderSelectTreeTableCell(
            final Callback<Integer, ObservableValue<String>> getSelectedProperty,
            final StringConverter<String> converter) {

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
    private ObjectProperty<StringConverter<String>> converter =
            new SimpleObjectProperty<StringConverter<String>>(this, "converter") {
                @Override
                protected void invalidated() {
                    updateShowLabel();
                }
            };

    /**
     * The {@link StringConverter} property.
     */
    public final ObjectProperty<StringConverter<String>> converterProperty() {
        return converter;
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     */
    public final void setConverter(StringConverter<String> value) {
        converterProperty().set(value);
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     */
    public final StringConverter<String> getConverter() {
        return converterProperty().get();
    }



    // --- selected state callback property
    private ObjectProperty<Callback<Integer, ObservableValue<String>>>
            selectedStateCallback =
            new SimpleObjectProperty<Callback<Integer, ObservableValue<String>>>(
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

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override 
    public void updateItem(String item, boolean empty) {

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            StringConverter<String> c = getConverter();

            if (showLabel) {
                setText(c.toString(item));
            }

            ObservableValue<?> selectedProperty = getSelectedProperty();

            String cellData =null;
            if (selectedProperty != null && getSelectedProperty().getValue()!=null) {
                cellData = Objects.toString( getSelectedProperty().getValue());
            }
            if (StringUtils.isEmpty(cellData)) {
                cellData = "选择文件夹";
            }
            btn.setText(cellData);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    DirectoryChooser chooser = directoryChooser;
                    File chosenDir = chooser.showDialog(Controller.primaryStage);
                    if (chosenDir != null) {
                        String absolutePath = chosenDir.getAbsolutePath();
                        btn.setText(absolutePath);
                        TreeTablePosition<S, String> stTablePosition = new TreeTablePosition<>(getTreeTableView(), getIndex(), getTableColumn());
                        TreeTableColumn.CellEditEvent editEvent = new TreeTableColumn.CellEditEvent(
                                getTreeTableView(),
                                stTablePosition,
                                TableColumn.editCommitEvent(),
                                absolutePath
                        );
                        Event.fireEvent(getTableColumn(), editEvent);
                    } else {
                        System.out.print("no directory chosen");
                    }
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

    @SuppressWarnings("unchecked")
    static  StringConverter<String> defaultStringConverter() {
        return (StringConverter<String>) defaultStringConverter;
    }
}
