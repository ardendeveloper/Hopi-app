package com.dev.hopi_app;

/**
 * Created by Arden on 1/10/2016.
 */
public class Chat {
    String name;
    String text;
    String pushID;

    public Chat() {
    }

    public Chat(String name, String pushID, String message) {
        this.name = name;
        this.text = message;
        this.pushID = pushID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPushID() {
        return pushID;
    }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }
}
