package com.generator.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author wang dongfang
 * @ClassName FreemarkerUtil.java
 * @Description TODO
 * @createTime 2018年12月05日 15:14:00
 */
public class FreemarkerUtil {
    public static Logger logger = LoggerFactory.getLogger(FreemarkerUtil.class);

    private static freemarker.template.Configuration cfg;

    public static void initCfg(String templatedir) {
        cfg = initFreeMarker(templatedir);
    }

    public static freemarker.template.Configuration initFreeMarker(String templatedir) {
        final freemarker.template.Configuration cfg;
        try {
            cfg = new freemarker.template.Configuration(Configuration.VERSION_2_3_28);
            cfg.setDirectoryForTemplateLoading(new File(templatedir));
            cfg.setDefaultEncoding("UTF-8");
            // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
            cfg.setLogTemplateExceptions(false);
        } catch (final Exception e) {
            logger.error("err", e);
            return null;
        }
        cfg.setObjectWrapper(new freemarker.template.DefaultObjectWrapper(Configuration.VERSION_2_3_28));
        return cfg;
    }

    public static Template getTemplate(String name) {
        try {
            return cfg.getTemplate(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
