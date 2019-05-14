package com.ssy.s_router_annotation.facade.enums;

public enum RouteType {

    ACTIVITY(0, "android.app.Activity"),
    SERVICE(1, "android.app.Service"),
    FRAGMENT(-1, "android.app.Fragment"),
    UNKNOWN(-1, "Unknown route type");

    int id;
    String className;


    public int getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    RouteType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
