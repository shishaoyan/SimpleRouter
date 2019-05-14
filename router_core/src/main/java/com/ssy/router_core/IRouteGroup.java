package com.ssy.router_core;

import com.example.router_annotationt.RouteMeta;

import java.util.Map;



public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> atlas);
}
