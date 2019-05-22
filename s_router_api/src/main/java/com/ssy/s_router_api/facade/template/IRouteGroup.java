package com.ssy.s_router_api.facade.template;

import com.ssy.s_router_annotation.facade.model.RouteMeta;

import java.util.Map;

public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> groups);
}
