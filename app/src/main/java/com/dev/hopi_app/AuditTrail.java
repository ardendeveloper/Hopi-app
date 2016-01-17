package com.dev.hopi_app;

/**
 * Created by Arden on 1/12/2016.
 */
public class AuditTrail {
    String action;
    String user;
    String timestamp;

    public AuditTrail() {
    }

    public AuditTrail(String action, String user) {
        this.action = action;
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
