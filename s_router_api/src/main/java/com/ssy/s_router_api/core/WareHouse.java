package com.ssy.s_router_api.core;

import com.ssy.s_router_annotation.facade.model.RouteMeta;
import com.ssy.s_router_api.facade.template.IProvider;
import com.ssy.s_router_api.facade.template.IRouteGroup;

import java.util.HashMap;
import java.util.Map;

public class WareHouse {

    //Cache route and metas
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String, RouteMeta> routes = new HashMap<>();

    //Cache provider
    static Map<String, IProvider> providers = new HashMap<>();
    static Map<String, RouteMeta> providerIndex = new HashMap<>();

    static void clear() {
        routes.clear();
        groupsIndex.clear();
        providers.clear();
        providerIndex.clear();
    }


}
