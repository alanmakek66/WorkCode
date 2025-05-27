package com.example.client;

public class Message {
    private String text;
    private String selfOrNot;
    private boolean isDeleted = false; // 添加一个标志位，表示是否已删除


    public Message(String text, String selfOrNot) {
        this.text = text;
        this.selfOrNot = selfOrNot;
    }

    public String getText() {
        return text;
    }

    public String getSelfOrNot() {
        return selfOrNot;
    }

    public boolean isDeleted() {
        return isDeleted;
    }



    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
