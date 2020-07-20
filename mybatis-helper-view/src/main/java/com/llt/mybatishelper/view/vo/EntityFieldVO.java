package com.llt.mybatishelper.view.vo;

import com.llt.mybatishelper.core.model.EntityField;
import javafx.beans.property.*;
import lombok.Data;

import java.util.Objects;

@Data
public class EntityFieldVO {
    private StringProperty name;

    private StringProperty columnName;

    private StringProperty type;

    private StringProperty jdbcType;

    private StringProperty fullJdbcType;

    private StringProperty length;

    private StringProperty defaultValue;

    private BooleanProperty noNull;

    private StringProperty description;


    public EntityFieldVO(EntityField item) {
        name = new SimpleStringProperty(item.getName());
        columnName = new SimpleStringProperty(item.getColumnName());
        type = new SimpleStringProperty(item.getType());
        jdbcType = new SimpleStringProperty(String.valueOf(item.getJdbcType()));
        fullJdbcType = new SimpleStringProperty(item.getFullJdbcType());
        length = new SimpleStringProperty(item.getLength());
        defaultValue = new SimpleStringProperty(item.getDefaultValue());
        noNull = new SimpleBooleanProperty(!Objects.equals(item.getNullable(),true));
        description= new SimpleStringProperty(item.getDescription());
    }
    public EntityFieldVO() {
        name = new SimpleStringProperty();
        columnName = new SimpleStringProperty();
        type = new SimpleStringProperty();
        jdbcType = new SimpleStringProperty();
        fullJdbcType = new SimpleStringProperty();
        length = new SimpleStringProperty();
        defaultValue = new SimpleStringProperty();
        noNull = new SimpleBooleanProperty(false);
        description= new SimpleStringProperty();
    }


    public String getName(){
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getColumnName() {
        return columnName.get();
    }

    public StringProperty columnNameProperty() {
        return columnName;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getJdbcType() {
        return jdbcType.get();
    }

    public StringProperty jdbcTypeProperty() {
        return jdbcType;
    }

    public String getFullJdbcType() {
        return fullJdbcType.get();
    }

    public StringProperty fullJdbcTypeProperty() {
        return fullJdbcType;
    }

    public String getLength() {
        return length.get();
    }

    public StringProperty lengthProperty() {
        return length;
    }

    public String getDefaultValue() {
        return defaultValue.get();
    }

    public StringProperty defaultValueProperty() {
        return defaultValue;
    }



    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setColumnName(String columnName) {
        this.columnName.set(columnName);
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType.set(jdbcType);
    }

    public void setFullJdbcType(String fullJdbcType) {
        this.fullJdbcType.set(fullJdbcType);
    }

    public void setLength(String length) {
        this.length.set(length);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue.set(defaultValue);
    }

    public boolean isNoNull() {
        return noNull.get();
    }

    public BooleanProperty noNullProperty() {
        return noNull;
    }

    public void setNoNull(boolean noNull) {
        this.noNull.set(noNull);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
