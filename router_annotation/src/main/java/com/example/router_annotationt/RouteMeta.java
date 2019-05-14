package com.example.router_annotationt;

import java.lang.reflect.Type;

import javax.lang.model.element.Element;

public class RouteMeta {
    public enum Type {
        ACTIVITY, ISERVICE
    }

    private Type type;
    private Element element;
    /**
     * 注解使用的类对象
     */
    private Class<?> aClazz;
    private String path;
    private String group;

    public RouteMeta(Type type, Element element, Class<?> aClazz, String path, String group) {
        this.type = type;
        this.element = element;
        this.aClazz = aClazz;
        this.path = path;
        this.group = group;
    }
    public static RouteMeta build(Type type, Class<?> destination, String path, String
            group) {
        return new RouteMeta(type, null, destination, path, group);
    }
    public RouteMeta(Type type, Route route, Element element) {
        this(type, element, null, route.path(), route.group());
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getaClazz() {
        return aClazz;
    }

    public void setaClazz(Class<?> aClazz) {
        this.aClazz = aClazz;
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
}
