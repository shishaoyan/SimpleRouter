package com.ssy.s_router_annotation.facade.model;

import com.ssy.s_router_annotation.facade.annotation.Route;
import com.ssy.s_router_annotation.facade.enums.RouteType;

import java.util.Map;

public class RouteMeta {
    private RouteType type;
    private Class<?> destination;
    private String path;
    private String group;
    private String name;
    private int priority = -1;
    private int extra = -1;

    public RouteMeta() {
    }

    public static RouteMeta build(RouteType type, Class<?> dextination, String path, String group, int priority, int extra) {
        return new RouteMeta(type, dextination, path, group, null, priority, extra);
    }

    public RouteMeta(RouteType type, Class<?> destination, String path, String group, String name, int priority, int extra) {
        this.type = type;
        this.destination = destination;
        this.path = path;
        this.group = group;
        this.name = name;
        this.priority = priority;
        this.extra = extra;
    }

    public RouteMeta(Route route, Class<?> destination, RouteType type) {
        this(type, destination, route.path(), route.group(), route.name(), route.priority(), route.extras());
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "RouteMeta{" +
                "type=" + type +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", extra=" + extra +
                '}';
    }
}
