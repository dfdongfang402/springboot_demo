package com.generator.type;

import java.util.HashMap;
import java.util.Map;

public class BeanNameSpace {
    private static BeanNameSpace root;

    public static BeanNameSpace getRoot() {
        return root;
    }

    public BeanNameSpace(final String name) {
        if (root == null)
            root = this;
        this.name = name;
    }

    public static BeanNameSpace findNameSpace(String name) {
        if (root == null)
            return null;
        return root.getChildNamespace(name);
    }

    private BeanNameSpace getChildNamespace(String name) {
        BeanNameSpace child = children.get(name);
        if (child == null) {
            for (Map.Entry<String, BeanNameSpace> e : children.entrySet()) {
                child = e.getValue().getChildNamespace(name);
                if (child != null) {
                    return child;
                }
            }
        }
        return child;
    }

    public Map<String, Bean> beans = new HashMap<String, Bean>();
    public BeanNameSpace parent;
    public Map<String, BeanNameSpace> children = new HashMap<String, BeanNameSpace>();
    private String name;

    public String getName() {
        return name;
    }

    public String getFullName() {
        BeanNameSpace curparent = parent;
        String curname = name;
        while (curparent != null) {
            curname = curparent.getName() + "." + curname;
            curparent = curparent.parent;
        }
        return curname;
    }

    public BeanNameSpace addChildNamespace(final BeanNameSpace ns) {
        return children.put(ns.getName(), ns);
    }

    public Bean addChildBean(final Bean bean) {
        return beans.put(bean.getName(), bean);
    }

    public Map<String, BeanNameSpace> getChildren() {
        return children;
    }

    public void writeJavafile() {
        for (Bean bean : beans.values()) {
            if (bean.getGenfile())
                bean.writeJavaFile();
        }
        for (BeanNameSpace ns : children.values()) {
            ns.writeJavafile();
        }
    }
}
