package com.agent;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class HotSwapAgent {
    private static final Logger logger = LoggerFactory.getLogger(com.agent.HotSwapAgent.class);
    private static final String PATCH_JAR = "patch.jar";

    public static void premain(String args, Instrumentation inst) {
        
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        logger.info("[hot swap] begin, agentmain method invoked with args: {} and inst: {}, RedefineClasses: {} and RetransformClasses: {}", args, inst, inst.isRedefineClassesSupported(), inst.isRetransformClassesSupported());
        Map<String, ReloadClassInfo> reloadClassInfoMap = getReloadClassInfoMap();
        if (reloadClassInfoMap.isEmpty()) {
            logger.info("[hot swap] no classes files, finish.");
            return;
        }
        logger.info("[hot swap] reloadClassInfoMap: {}", reloadClassInfoMap);
        CustomClassTransform classTransformer = new CustomClassTransform(reloadClassInfoMap);
        inst.addTransformer(classTransformer, true);

        List<Class> transformClasses = new ArrayList<>();
        for (Entry<String, ReloadClassInfo> entry : reloadClassInfoMap.entrySet()) {
            ReloadClassInfo info = entry.getValue();
            if(info.isNew()) {
                loadNewClass(info);
            } else {
                transformClasses.add(info.getSwapClass());
            }
        }
        try {
            inst.retransformClasses(transformClasses.toArray(new Class[]{}));
        } catch (Throwable t) {
            logger.error("[hot swap] re transform classes error, classes: " + reloadClassInfoMap.keySet().toString(), t);
        }
        inst.removeTransformer(classTransformer);
        logger.info("[hot swap] finish");
    }

    private static Map<String, ReloadClassInfo> getReloadClassInfoMap() {
        Map<String, ReloadClassInfo> reloadClassMap = new HashMap<>();
        try {
            File file = new File(PATCH_JAR);
            logger.info("[hot swap] jar file: {}", file.getCanonicalPath());
            if(!file.exists()){
                logger.error("[hot swap] no jar file");
                return reloadClassMap;
            }

            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while(enumeration.hasMoreElements()){
                JarEntry entry = enumeration.nextElement();
                String name = entry.getName();
                if(!name.endsWith(".class")){
                    continue;
                }
                String classname = name.replace('/', '.');
                classname = classname.substring(0, classname.lastIndexOf('.'));
                InputStream inputStream = jarFile.getInputStream(entry);
                byte[] bs = IOUtils.toByteArray(inputStream);
                Class<?> cls = null;
                try {
                    cls = Class.forName(classname);
                } catch (ClassNotFoundException e) {
                    logger.info("[hot swap] current no this class {}", classname);
                }

                if(cls != null){
                    reloadClassMap.put(classname, new ReloadClassInfo(classname, bs, cls, false));
                }else{
                    reloadClassMap.put(classname, new ReloadClassInfo(classname, bs, null, true));
                }
            }

            jarFile.close();
        } catch (Throwable e) {
            logger.error("hot swap error", e);
        }

        return reloadClassMap;
    }

    private static void loadNewClass(ReloadClassInfo info) {
        String classname = info.getSwapClassName();
        try {
            Class.forName(classname);
        } catch (ClassNotFoundException e) {
            logger.error("[hot swap] ClassNotFoundException", e);
        }
    }
}
