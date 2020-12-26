package com.deepanshu.whatsappdemo.databaseHelper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommanDataHolder implements Serializable {
    private int id;
    @SerializedName("sort_order")
    private int sortOrder;
    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private String value;
    @SerializedName("type")
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
