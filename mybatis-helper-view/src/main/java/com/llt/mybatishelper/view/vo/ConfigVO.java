package com.llt.mybatishelper.view.vo;

import com.llt.mybatishelper.core.model.BuildConfig;
import javafx.beans.property.*;

import java.util.Objects;

public class ConfigVO {
    private final StringProperty entityFolder;

    private final StringProperty mapperFolder;

    private final StringProperty xmlFolder;

    private final StringProperty db;

    private final BooleanProperty useBaseField;

    private final BooleanProperty enable;

    public ConfigVO(BuildConfig buildConfig) {
        entityFolder = new SimpleStringProperty(buildConfig.getEntityFolder());
        mapperFolder = new SimpleStringProperty(buildConfig.getMapperFolder());
        xmlFolder = new SimpleStringProperty(buildConfig.getXmlFolder());
        db = new SimpleStringProperty(buildConfig.getDb());
        useBaseField = new SimpleBooleanProperty(!Objects.equals(buildConfig.getIgnoreBaseField(),true));
        enable = new SimpleBooleanProperty(!Objects.equals(buildConfig.getDisable(),true));
    }


    public ConfigVO() {
        entityFolder = new SimpleStringProperty();
        mapperFolder = new SimpleStringProperty();
        xmlFolder = new SimpleStringProperty();
        db = new SimpleStringProperty();
        useBaseField = new SimpleBooleanProperty(true);
        enable= new SimpleBooleanProperty(true);
    }


    public String getEntityFolder() {
        return entityFolder.get();
    }

    public StringProperty entityFolderProperty() {
        return entityFolder;
    }

    public void setEntityFolder(String entityFolder) {
        this.entityFolder.set(entityFolder);
    }

    public String getMapperFolder() {
        return mapperFolder.get();
    }

    public StringProperty mapperFolderProperty() {
        return mapperFolder;
    }

    public void setMapperFolder(String mapperFolder) {
        this.mapperFolder.set(mapperFolder);
    }

    public String getXmlFolder() {
        return xmlFolder.get();
    }

    public StringProperty xmlFolderProperty() {
        return xmlFolder;
    }

    public void setXmlFolder(String xmlFolder) {
        this.xmlFolder.set(xmlFolder);
    }

    public boolean isUseBaseField() {
        return useBaseField.get();
    }

    public BooleanProperty useBaseFieldProperty() {
        return useBaseField;
    }

    public void setUseBaseField(boolean useBaseField) {
        this.useBaseField.set(useBaseField);
    }

    public String getDb() {
        return db.get();
    }

    public StringProperty dbProperty() {
        return db;
    }

    public void setDb(String db) {
        this.db.set(db);
    }

    public boolean isEnable() {
        return enable.get();
    }

    public BooleanProperty enableProperty() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable.set(enable);
    }
}
