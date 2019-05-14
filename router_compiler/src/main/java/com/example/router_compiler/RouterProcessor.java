package com.example.router_compiler;

import com.example.router_annotationt.Route;
import com.example.router_annotationt.RouteMeta;
import com.example.router_compiler.util.Constant;
import com.example.router_compiler.util.Log;
import com.example.router_compiler.util.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 处理器接收的参数 替代 {@link AbstractProcessor#getSupportedOptions()} 函数
 */
@SupportedOptions(Constant.ARGUMENTS_NAME)
/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()} 函数
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)

/**
 * 注册给哪些注解的  替代 {@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 */
@SupportedAnnotationTypes(Constant.ANNOTATION_TYPE_ROUTE)

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
    private Filer filer;
    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;
    /**
     * type(类信息)工具类
     */
    private Types typeUtils;
    private Log log;
    private String moduleName;
    /**
     * 分组 key:组名 value:对应组的路由信息
     */
    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
        //获得apt的日志输出
        log = Log.newLog(processingEnvironment.getMessager());
        log.i("--------------");
        typeUtils = processingEnvironment.getTypeUtils();

        Map<String,String> options = processingEnvironment.getOptions();
        if (!Utils.isEmpty(options)){
            moduleName = options.get(Constant.ARGUMENTS_NAME);
        }
        if (Utils.isEmpty(moduleName)){
            throw new RuntimeException("Not set process moudleName option !");
        }
        log.i("init RouterProcessor " + moduleName + " success !");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (!Utils.isEmpty(annotations)) {
            //扫描到的注解集合 ，筛选我们的注解
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
            if (!Utils.isEmpty(routeElements)) {
                processRouter(routeElements);
            }
            return true;
        }

        return false;
    }

    private void processRouter(Set<? extends Element> routeElements) {
        TypeElement activity = elementUtils.getTypeElement(Constant.ACTIVITY);
        TypeElement service = elementUtils.getTypeElement(Constant.ISERVICE);
        for (Element element : routeElements) {
            RouteMeta routeMeta;
            //获得这个元素的信息
            TypeMirror typeMirror = element.asType();
            log.i("Route class:" + typeMirror.toString());
            Route route = element.getAnnotation(Route.class);
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, route, element);
            } else if (typeUtils.isSubtype(typeMirror, service.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.ISERVICE, route, element);
            } else {
                throw new RuntimeException("only support Activity and IService Route: " + element);
            }

            categories(routeMeta);
        }
        TypeElement iRouteGroup = elementUtils.getTypeElement(Constant.IROUTE_GROUP);
         TypeElement iRouteRoot = elementUtils.getTypeElement(Constant.IROUTE_ROOT);
        //生成Group记录分组表
        generatedGroup(iRouteGroup);
        //生成Root类 作用 记录<分组，对应的Group类>
          generatedRoot(iRouteRoot);

    }

    private void generatedRoot(TypeElement iRouteRoot) {

//创建参数类型
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),WildcardTypeName.subtypeOf(ClassName.get(iRouteRoot))
                ));

        ParameterSpec routes = ParameterSpec.builder(parameterizedTypeName,"routes").build();


    }


    private void generatedGroup(TypeElement iRouteGroup) {

        //创建参数类型 Map<String,RouteMeta>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        //生成参数
        ParameterSpec routerMetaMap = ParameterSpec.builder(parameterizedTypeName, "routerMetaMap").build();
        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INTO)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(routerMetaMap);

            String groupName = entry.getKey();
            log.i("-----------groupName-------"+groupName);
            List<RouteMeta> groupData = entry.getValue();

            for (RouteMeta routeMeta : groupData) {
                methodBuilder.addStatement("routerMetaMap.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        routeMeta.getType(),
                        ClassName.get(((TypeElement) routeMeta.getElement())),
                        routeMeta.getPath(),
                        routeMeta.getGroup());

            }

            String groupClassName = Constant.NAME_OF_GROUP + groupName;

            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
            try {
                javaFile.writeTo(filer);
                log.i("Generated RouteRoot：" + Constant.PACKAGE_OF_GENERATE_FILE + "." + groupClassName);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    /**
     * 检查配置是否配置了 group 如果没有配置那么就从 path 抽取组名
     *
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        log.i("--------categories------");
        if (routeVerify(routeMeta)) {
            log.i("Group : " + routeMeta.getGroup() + " path=" + routeMeta.getPath());
            List<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (Utils.isEmpty(routeMetas)) {

                routeMetas = new ArrayList<>();
                routeMetas.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetas);
                log.i("--------isEmpty------");
            } else {
                log.i("--------no isEmpty------");
                routeMetas.add(routeMeta);
            }

        } else {
            log.i("Group info error:" + routeMeta.getPath());
        }
    }

    /**
     * 验证 路由地址的合法性
     *
     * @param routeMeta
     */
    private boolean routeVerify(RouteMeta routeMeta) {

        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();

        if (!path.startsWith("/")) {
            return false;
        }

        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (Utils.isEmpty(defaultGroup)) {
                return false;
            }
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }
}
