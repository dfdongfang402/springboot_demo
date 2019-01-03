package com.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;

public class CustomClassTransform implements ClassFileTransformer {
    private static final Logger logger = LoggerFactory.getLogger(com.agent.CustomClassTransform.class);
    private Map<String, ReloadClassInfo> reloadClassInfoMap = null;

    public CustomClassTransform(Map<String, ReloadClassInfo> classNamePathMap) {
        this.reloadClassInfoMap = classNamePathMap;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        logger.info("[hot swap] transform class name: {}, class loader: {}", className, loader);
        String fullClassName = className.replace("/", ".");
        ReloadClassInfo info = reloadClassInfoMap.get(fullClassName);
        if (info == null || info.getBs() == null) {
            return classfileBuffer;
        }

        byte[] classContents = info.getBs();
        logger.info("[hot swap] successfully redefine class {}", fullClassName);
        return classContents;

    }
}
