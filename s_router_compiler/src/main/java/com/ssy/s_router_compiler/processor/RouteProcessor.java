package com.ssy.s_router_compiler.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.WildcardTypeName;
import com.ssy.s_router_annotation.facade.annotation.Route;
import com.ssy.s_router_annotation.facade.enums.RouteType;
import com.ssy.s_router_annotation.facade.model.RouteMeta;
import com.ssy.s_router_compiler.utils.Consts;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.StandardLocation;

import static com.ssy.s_router_compiler.utils.Consts.ACTIVITY;
import static com.ssy.s_router_compiler.utils.Consts.FRAGMENT;
import static com.ssy.s_router_compiler.utils.Consts.FRAGMENT_V4;
import static com.ssy.s_router_compiler.utils.Consts.IPROVIDER;
import static com.ssy.s_router_compiler.utils.Consts.IPROVIDER_GROUP;
import static com.ssy.s_router_compiler.utils.Consts.IROUTE_GROUP;
import static com.ssy.s_router_compiler.utils.Consts.METHOD_LOAD_INTO;
import static com.ssy.s_router_compiler.utils.Consts.PACKAGE_OF_GENERATE_DOCS;
import static com.ssy.s_router_compiler.utils.Consts.SERVICE;

public class RouteProcessor extends BaseProcessor {

    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>();//moduleName and routeMeta
    private Map<String, String> rootMap = new TreeMap<>();//map of root metas ,used for generate class file in order

    private TypeMirror iProvider = null;
    private Writer docWriter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        if (generateDoc) {
            try {
                docWriter = mFiler.createResource(StandardLocation.SOURCE_OUTPUT, PACKAGE_OF_GENERATE_DOCS, "srouter-map-of-" + moduleName + ".json").openWriter();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("create doc writer failed ,because " + e.getMessage());
            }
        }
        iProvider = elementUtils.getTypeElement(Consts.IPROVIDER).asType();
        logger.info(">>> RouteProcess init. <<<");


    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);

            try {
                logger.info(">>> Found routers ,start ... <<<");
                this.paserRoutes(routeElements);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    protected void paserRoutes(Set<? extends Element> routeElements) throws IOException {

        if (CollectionUtils.isNotEmpty(routeElements)) {
            // prepare the type an so on
            logger.info(">>> Found routes,size is " + routeElements.size() + " <<<");
            rootMap.clear();

            TypeMirror type_activity = elementUtils.getTypeElement(ACTIVITY).asType();
            TypeMirror type_service = elementUtils.getTypeElement(SERVICE).asType();
            TypeMirror type_fgrament = elementUtils.getTypeElement(FRAGMENT).asType();
            TypeMirror type_fgrament_v4 = elementUtils.getTypeElement(FRAGMENT_V4).asType();

            //interface of SRouter
            TypeElement type_IRouteGroup = elementUtils.getTypeElement(IROUTE_GROUP);
            TypeElement type_IProvicerGroup = elementUtils.getTypeElement(IPROVIDER_GROUP);

            ClassName routeMetaCn = ClassName.get(RouteMeta.class);
            ClassName routeTpyeCn = ClassName.get(RouteType.class);

            // build input tpye format as : Map<String,Class<? extends IRouteGroup>>
            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                    ClassName.get(Map.class), ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup)))

            );

            //Map<String,RouteMeta>
            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class));

            //build input param name
            ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "roots").build();
            ParameterSpec groutParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "group").build();
            ParameterSpec providerParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "provider").build();

            //build method 'loadInto' root
            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(rootParamSpec);

            MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(groutParamSpec);

            //follow a sequence ,find out metas of group first,gennerate jaca file,then sattistics them as root.
            for (Element element : routeElements) {
                TypeMirror typeMirror = element.asType();
                Route route = element.getAnnotation(Route.class);
                RouteMeta routeMeta = null;
                if (types.isSubtype(typeMirror, type_activity)) {
                    logger.info(">>> Found activity route: " + typeMirror.toString() + " <<<");
                    routeMeta = new RouteMeta(route, null, RouteType.ACTIVITY);
                    //get all filds annotation by @

                } else if (types.isSubtype(typeMirror, type_service)) {
                    routeMeta = new RouteMeta(route, null, RouteType.SERVICE);
                }

                categories(routeMeta);
            }

            for (Map.Entry<String,Set<RouteMeta>> entry:groupMap.entrySet()){
                String groupName = entry.getKey();
                MethodSpec.Builder
            }



        }


    }

    /**
     * sort metas in group
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            logger.info(">>> Start categories ,group = " + routeMeta.getGroup() + ",path = " + routeMeta.getPath());
            Set<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                Set<RouteMeta> routeMetasSet = new TreeSet<>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta r1, RouteMeta r2) {
                        try {
                            return r1.getPath().compareTo(r2.getPath());
                        } catch (NullPointerException e) {
                            logger.error(e.getMessage());
                            return 0;
                        }
                    }
                });
                routeMetasSet.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetasSet);
            } else {
                routeMetas.add(routeMeta);
            }

        } else {
            logger.warning(">>> Route meta verity error ,group is " + routeMeta.getGroup() + " <<<");
        }
    }

    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();

        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {   // The path must be start with '/' and not empty!
            return false;
        }

        if (StringUtils.isEmpty(meta.getGroup())) { // Use default group(the first word in path)
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }

                meta.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                logger.error("Failed to extract default group! " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
