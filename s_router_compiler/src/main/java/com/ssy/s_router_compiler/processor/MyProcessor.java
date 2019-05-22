package com.ssy.s_router_compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.ssy.s_router_annotation.facade.annotation.Route;
import com.ssy.s_router_compiler.utils.Logger;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


public class MyProcessor extends AbstractProcessor {
    Logger logger;
    private Filer filer;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //获得 filer 对象 用来创建文件
        filer = processingEnvironment.getFiler();
        logger.info(">>> init <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {


        //扫描到的注解集合 ，筛选我们的 MyAnnotation
        for (TypeElement element : annotations) {
            //如果注解使我们自己的注解
            if (element.getQualifiedName().toString().equals(Route.class.getCanonicalName())) {
                //不熟悉 JavaPoet 的可以先学习下 JavaPoet
                //这里的作用就是 生成我们的 Student 类
                TypeSpec student = TypeSpec.classBuilder("Student")
                        .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                        .addMethod(getMethodSpec("getName", "小明"))
                        .addMethod(getMethodSpec("getAddr", "北京"))
                        .build();

                try {
                    //最后把 file 写到 com.ssy.testdemo 目录下
                    JavaFile javaFile = JavaFile.builder("com.ssy.testdemo", student)
                            .addFileComment("This codes are generated automatically. Do not modify!")
                            .build();
                    //写入文件

                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }


        return true;
    }
    // 创建方法 Spec
    private static MethodSpec getMethodSpec(String methodStr, String returnStr) {
        return MethodSpec.methodBuilder(methodStr)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(String.class)
                .addStatement("return $S", returnStr)
                .build();
    }
    //用来表示这个注解处理器是注册给哪个注解的
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Route.class.getCanonicalName());
        return types;
    }
    //制定 Java 版本 我们这个用最新的
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
