package com.example.a0321;

public class FqaModel {
    private String key;
    private String value;

    public FqaModel() {}
    // Constructor
    public FqaModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getter for key
    public String getKey() {
        return key;
    }

    // Setter for key
    public void setKey(String key) {
        this.key = key;
    }

    // Getter for value
    public String getValue() {
        return value;
    }

    // Setter for value
    public void setValue(String value) {
        this.value = value;
    }
}
