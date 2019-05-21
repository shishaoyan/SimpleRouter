package com.ssy.s_router_compiler.processor;

import com.ssy.s_router_compiler.utils.Logger;
import com.ssy.s_router_compiler.utils.TypeUtils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.ssy.s_router_compiler.utils.Consts.KEY_GENERATE_DOC_NAME;
import static com.ssy.s_router_compiler.utils.Consts.KEY_MODULE_NAME;
import static com.ssy.s_router_compiler.utils.Consts.NO_MODULE_NAME_TIPS;
import static com.ssy.s_router_compiler.utils.Consts.VALUE_ENABLE;

public abstract class BaseProcessor extends AbstractProcessor {

    Filer mFiler;
    Logger logger;
    Types types;
    Elements elementUtils;
    TypeUtils typeUtils;
    //Module name, app 或者 其他
    String moduleName;
    boolean generateDoc;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        logger = new Logger(processingEnvironment.getMessager());

        //尝试去获取 user configuration [modeleName]
        Map<String,String> options = processingEnvironment.getOptions();
        if (MapUtils.isNotEmpty(options)){
            moduleName = options.get(KEY_MODULE_NAME);
            generateDoc = VALUE_ENABLE.equals(options.get(KEY_GENERATE_DOC_NAME));
        }

        if (StringUtils.isNotEmpty(moduleName)){
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]","");
            logger.info("The user has configuration the module name ,it was ["+moduleName+"]");
        }else {
            logger.error(NO_MODULE_NAME_TIPS);
            throw new RuntimeException("SRouter::Compile >>> No module name,for more information,look at gradle log.");
        }

    }

    @Override
    public Set<String> getSupportedOptions() {

       return  new HashSet<String>(){
           {
               this.add(KEY_MODULE_NAME);
               this.add(KEY_GENERATE_DOC_NAME);
           }
       };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
