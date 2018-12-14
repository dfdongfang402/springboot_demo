package com.generator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPathExpressionException;

public class GeneratorCpp {
    private static class Dependence {
        private java.util.Set<String> roots;
        private java.util.Map<String, String[]> subs;

        public Dependence() {
            roots = new java.util.TreeSet<String>();
            subs = new java.util.TreeMap<String, String[]>();
        }

        final public void pushName(String name) {
            roots.add(name);
        }

        final public void pushName(String name, String[] parent) {
            subs.put(name, parent);
        }

        final public String[] getClassnames() {
            java.util.List<String> values = new java.util.LinkedList<String>(roots);
            java.util.Set<String> pushed = new java.util.TreeSet<String>(roots);

            int lastsize = subs.size();
            while (lastsize > 0) {
                java.util.List<String> temps = new java.util.LinkedList<String>();
                java.util.Set<java.util.Map.Entry<String, String[]>> entry = subs.entrySet();
                java.util.Iterator<java.util.Map.Entry<String, String[]>> it = entry.iterator();
                while (it.hasNext()) {
                    java.util.Map.Entry<String, String[]> vp = it.next();
                    String[] ps = vp.getValue();
                    boolean ready = true;
                    int count = ps.length;
                    for (int i = 0; i < count; i++) {
                        if (!pushed.contains(ps[i])) {
                            ready = false;
                            break;
                        }
                    }
                    if (ready) {
                        String name = vp.getKey();
                        values.add(name);
                        pushed.add(name);
                        temps.add(name);
                    }
                }
                for (String name : temps)
                    subs.remove(name);

                int currentsize = subs.size();
                if (lastsize == currentsize)
                    break;
                else
                    lastsize = currentsize;
            }

            return values.toArray(new String[0]);
        }
    }

    static private java.util.Set<String> singleTypes = makeSingleTypes();
    static private java.util.Set<String> complexTypes = makeComplexTypes();

    private static java.util.Set<String> makeSingleTypes() {
        java.util.Set<String> ss = new java.util.TreeSet<String>();
        ss.add("string");
        ss.add("int");
        ss.add("long");
        ss.add("bool");
        ss.add("double");
        return ss;
    }

    private static java.util.Set<String> makeComplexTypes() {
        java.util.Set<String> ss = new java.util.TreeSet<String>();
        ss.add("vector");
        ss.add("set");
        return ss;
    }

    final private static void outTabs(java.io.PrintStream ps, int n) {
        for (int i = 0; i < n; i++)
            ps.print('\t');
    }

    static private class VarInfo {
        /**
         * 变量名
         */
        public String name;
        /**
         * 变量的类型
         */
        public String type;
        /**
         * 初始值
         */
        public String comment;
        /**
         * 如果是Collection，容器内的值类型
         */
        public String valueType;

        final private static String getSingleTypeCPPType(final String type) {
            if (type.equalsIgnoreCase("string"))
                return "std::wstring";
            else if (type.equalsIgnoreCase("long"))
                return "long long";
            else
                return type;
        }

        final public String getCPPType() {
            if (singleTypes.contains(type)) {
                return getSingleTypeCPPType(type);
            } else {
                String subtype;
                if (singleTypes.contains(valueType))
                    subtype = getSingleTypeCPPType(valueType);
                else
                    subtype = valueType.replaceAll("\\.", "::");
                return "std::" + type + "<" + subtype + ">";
            }
        }

        final public boolean isNeedRef() {
            return !singleTypes.contains(type) || type.equalsIgnoreCase("string");
        }
    }

    private static class BeanMaker {
        private ClassNameUtil cname;
        private java.util.List<VarInfo> varlist;
        private java.io.PrintStream outHFile;
        private java.io.PrintStream outCFile;

        private boolean hasString = false;
        private boolean hasVector = false;
        private boolean hasSet = false;

        private java.util.List<VarInfo> indexSingle = new java.util.LinkedList<VarInfo>();
        private java.util.List<VarInfo> indexMulti = new java.util.LinkedList<VarInfo>();

        private BeanMaker(final String dstdir, final String targetClassname, org.w3c.dom.Element e) {
            cname = new ClassNameUtil(targetClassname);
            outHFile = makeOutPrintStream(dstdir + "/" + cname.getDir() + ".h");
            outCFile = makeOutPrintStream(dstdir + "/" + targetClassname + ".inl");

            varlist = makeVarlist(e, targetClassname);
            hasVector = !indexMulti.isEmpty();
        }

        final private java.io.PrintStream makeOutPrintStream(final String filename) {
            try {
                new java.io.File(filename).getParentFile().mkdirs();
                return new java.io.PrintStream(new java.io.FileOutputStream(filename), true, "utf-8");
            } catch (final Exception ex) {
                logger.error("err", ex);
                return null;
            }
        }

        final private String getNodeComment(org.w3c.dom.Element e) {
            org.w3c.dom.Node nextnode = e.getNextSibling();
            if (nextnode != null && org.w3c.dom.Node.TEXT_NODE == nextnode.getNodeType())
                return nextnode.getTextContent().trim().replaceAll("[\r\n]", "");
            else
                return "";
        }

        final private java.util.List<VarInfo> makeVarlist(org.w3c.dom.Element e, final String targetClassname) {
            final org.w3c.dom.NodeList list = e.getElementsByTagName("variable");
            java.util.List<VarInfo> varlist = new java.util.ArrayList<VarInfo>(list.getLength());
            for (int i = 0; i != list.getLength(); ++i) {
                final org.w3c.dom.Element vare = (org.w3c.dom.Element) list.item(i);
                VarInfo bi = new VarInfo();
                bi.type = vare.getAttribute("type").toLowerCase();
                if (bi.type.isEmpty()) {
                    logger.error(targetClassname + "'s variable need type");
                    continue;
                }

                if (complexTypes.contains(bi.type))
                    bi.valueType = vare.getAttribute("value").toLowerCase();
                else if (singleTypes.contains(bi.type))
                    bi.valueType = "";
                else
                    continue;

                checkCPlusPlusTypes(bi.type);

                bi.name = vare.getAttribute("name");
                bi.comment = getNodeComment(vare);
                varlist.add(bi);

                String index = vare.getAttribute("index");
                if (index.equalsIgnoreCase("unique"))
                    indexSingle.add(bi);
                else if (index.equalsIgnoreCase("multi"))
                    indexMulti.add(bi);
            }
            return varlist;
        }

        final private void checkIsString(final String type) {
            if (type.equalsIgnoreCase("string"))
                hasString = true;
        }

        final private void checkIsVector(final String type) {
            if (type.equalsIgnoreCase("vector"))
                hasVector = true;
        }

        final private void checkIsSet(final String type) {
            if (type.equalsIgnoreCase("set"))
                hasSet = true;
        }

        final private void checkCPlusPlusTypes(final String type) {
            checkIsString(type);
            checkIsVector(type);
            checkIsSet(type);
        }

        final public void checkDependence(Dependence dep) {
            java.util.List<String> deps = new java.util.LinkedList<String>();
            for (VarInfo info : varlist) {
                if (singleTypes.contains(info.type))
                    continue;
                if (complexTypes.contains(info.type)) {
                    if (!singleTypes.contains(info.valueType))
                        deps.add(info.valueType);
                } else {
                    deps.add(info.type);
                }
            }
            if (deps.isEmpty())
                dep.pushName(cname.getClassnameString());
            else
                dep.pushName(cname.getClassnameString(), deps.toArray(new String[0]));
        }

        final public boolean outHFile() {
            if (null == outHFile)
                return false;
            outHFile.println("#pragma once");
            outHFile.println();

            if (hasString)
                outHFile.println("#include <string>");
            if (hasVector)
                outHFile.println("#include <vector>");
            if (hasSet)
                outHFile.println("#include <set>");
            if (hasString || hasVector || hasSet)
                outHFile.println();

            final String[] namespaces = cname.getNamespaceArray();
            for (int i = 0; i < namespaces.length; i++) {
                outTabs(outHFile, i);
                outHFile.println("namespace " + namespaces[i] + " {");
            }

            outHFile.println();

            outTabs(outHFile, namespaces.length);
            outHFile.println("struct " + cname.getShortName());
            outTabs(outHFile, namespaces.length);
            outHFile.println('{');

            for (final VarInfo varinfo : varlist) {
                outTabs(outHFile, namespaces.length + 1);
                outHFile.print(varinfo.getCPPType() + " " + varinfo.name + ";");
                if (varinfo.comment.isEmpty())
                    outHFile.println();
                else
                    outHFile.println(" // " + varinfo.comment);
            }

            outTabs(outHFile, namespaces.length);
            outHFile.println("};");

            outHFile.println();

            outTabs(outHFile, namespaces.length);
            outHFile.println("class " + cname.getShortName() + "Table");
            outTabs(outHFile, namespaces.length);
            outHFile.println("{");

            outTabs(outHFile, namespaces.length);
            outHFile.println("public:");
            outTabs(outHFile, namespaces.length + 1);
            outHFile.println(cname.getShortName() + "Table() {}");
            outTabs(outHFile, namespaces.length + 1);
            outHFile.println("virtual ~" + cname.getShortName() + "Table() {}");

            outTabs(outHFile, namespaces.length);
            outHFile.println("public:");

            outTabs(outHFile, namespaces.length + 1);
            outHFile.println("virtual void getAllID( std::vector<int>& ids) = 0;");

            outTabs(outHFile, namespaces.length + 1);
            outHFile.println("virtual const " + cname.getShortName() + "& getRecorder( int id) = 0;");

            for (final VarInfo index : indexSingle) {
                outTabs(outHFile, namespaces.length + 1);
                if (index.isNeedRef())
                    outHFile.println("virtual const " + cname.getShortName() + "& getRecorderBy" + ClassNameUtil.capFirst(index.name) + "( const " + index.getCPPType() + "& key) = 0;");
                else
                    outHFile.println("virtual const " + cname.getShortName() + "& getRecorderBy" + ClassNameUtil.capFirst(index.name) + "( " + index.getCPPType() + " key) = 0;");
            }
            for (final VarInfo index : indexMulti) {
                outTabs(outHFile, namespaces.length + 1);
                if (index.isNeedRef())
                    outHFile.println("virtual const std::vector<int>& getRecorderBy" + ClassNameUtil.capFirst(index.name) + "( const " + index.getCPPType() + "& key) = 0;");
                else
                    outHFile.println("virtual const std::vector<int>& getRecorderBy" + ClassNameUtil.capFirst(index.name) + "( " + index.getCPPType() + " key) = 0;");
            }

            outTabs(outHFile, namespaces.length);
            outHFile.println("};");

            outHFile.println();
            outTabs(outHFile, namespaces.length);
            outHFile.println(cname.getShortName() + "Table& Get" + cname.getShortName() + "TableInstance();");

            outHFile.println();
            for (int i = namespaces.length - 1; i >= 0; i--) {
                outTabs(outHFile, i);
                outHFile.println("} // namespace " + namespaces[i] + " {");
            }
            outHFile.println();
            return true;
        }

        final public boolean outCFile() {
            outCFile.println();
            outCFile.println("namespace XMLCONFIG {");
            outCFile.println();

            String beantype = cname.getClassnameString().replaceAll("\\.", "::");

            outCFile.println("// 从xml的一个node读取出数据");
            outTabs(outCFile, 1);
            outCFile.println("inline void BeanFromXML( " + beantype + "& bean, const XMLIO::CINode& node)");
            outTabs(outCFile, 1);
            outCFile.println("{");

            java.util.List<VarInfo> complexvars = new java.util.LinkedList<VarInfo>();
            for (VarInfo info : varlist) {
                if (singleTypes.contains(info.type)) {
                    outTabs(outCFile, 2);
                    outCFile.println("LoadAttributeValue( node, L\"" + info.name + "\", bean." + info.name + ");");
                } else {
                    complexvars.add(info);
                }
            }
            if (complexvars.size() != varlist.size())
                outCFile.println();

            if (complexvars.size() > 0) {
                outTabs(outCFile, 2);
                outCFile.println("std::wstring name;");
                outTabs(outCFile, 2);
                outCFile.println("size_t count = node.GetChildrenCount();");
                outTabs(outCFile, 2);
                outCFile.println("for( size_t i = 0; i < count; i ++)");
                outTabs(outCFile, 2);
                outCFile.println("{");

                outTabs(outCFile, 3);
                outCFile.println("XMLIO::CINode subnode;");
                outTabs(outCFile, 3);
                outCFile.println("if( !node.GetChildAt( i, subnode))");
                outTabs(outCFile, 4);
                outCFile.println("continue;");

                outTabs(outCFile, 3);
                outCFile.println("name = subnode.GetName();");

                java.util.Iterator<VarInfo> it = complexvars.iterator();

                {
                    VarInfo info = it.next();

                    outTabs(outCFile, 3);
                    outCFile.println("if( name == L\"" + info.name + "\")");
                    outTabs(outCFile, 4);
                    outCFile.println("BeanFromXML( bean." + info.name + ", subnode);");
                }
                while (it.hasNext()) {
                    VarInfo info = it.next();

                    outTabs(outCFile, 3);
                    outCFile.println("else if( name == L\"" + info.name + "\")");
                    outTabs(outCFile, 4);
                    outCFile.println("BeanFromXML( bean." + info.name + ", subnode);");
                }

                outTabs(outCFile, 2);
                outCFile.println("}");
            }

            outTabs(outCFile, 1);
            outCFile.println("}");

            outCFile.println();

            outCFile.println("// 将数据放入CFileStream");
            outTabs(outCFile, 1);
            outCFile.println("inline PFS::CFileStream& operator<<( PFS::CFileStream& fs, const " + beantype + "& src)");
            outTabs(outCFile, 1);
            outCFile.println("{");
            for (VarInfo info : varlist) {
                outTabs(outCFile, 2);
                outCFile.println("fs << src." + info.name + ";");
            }
            outTabs(outCFile, 2);
            outCFile.println("return fs;");
            outTabs(outCFile, 1);
            outCFile.println("}");

            outCFile.println();

            outCFile.println("// 从CFileStream读取出数据");
            outTabs(outCFile, 1);
            outCFile.println("inline const PFS::CFileStream& operator>>( const PFS::CFileStream& fs, " + beantype + "& dst)");
            outTabs(outCFile, 1);
            outCFile.println("{");
            for (VarInfo info : varlist) {
                outTabs(outCFile, 2);
                outCFile.println("fs >> dst." + info.name + ";");
            }
            outTabs(outCFile, 2);
            outCFile.println("return fs;");
            outTabs(outCFile, 1);
            outCFile.println("}");

            outCFile.println();

            outCFile.println("// Table的实现类");
            String tablename = cname.getClassnameString().replaceAll("\\.", "_") + "_table_implement";
            outTabs(outCFile, 1);
            outCFile.println("class " + tablename + " : public " + beantype + "Table");
            outTabs(outCFile, 1);
            outCFile.println("{");

            outTabs(outCFile, 1);
            outCFile.println("public:");
            outTabs(outCFile, 2);
            outCFile.println("typedef " + beantype + " BeanType;");

            outTabs(outCFile, 1);
            outCFile.println("private:");
            outTabs(outCFile, 2);
            outCFile.println("CBeanCache<BeanType>* m_cache;");
            for (VarInfo info : indexSingle) {
                outTabs(outCFile, 2);
                outCFile.println("CUniqueIndex<" + info.getCPPType() + "> m_index_" + info.name + ";");
            }
            for (VarInfo info : indexMulti) {
                outTabs(outCFile, 2);
                outCFile.println("CMultiIndex<" + info.getCPPType() + "> m_index_" + info.name + ";");
            }
            outTabs(outCFile, 1);
            outCFile.println("public:");
            outTabs(outCFile, 2);
            outCFile.println(tablename + "( CBeanCache<BeanType>* cache)");
            outTabs(outCFile, 3);
            outCFile.println(": m_cache( cache)");
            outTabs(outCFile, 2);
            outCFile.println("{}");
            outTabs(outCFile, 2);
            outCFile.println("virtual ~" + tablename + "()");
            outTabs(outCFile, 2);
            outCFile.println("{");
            outTabs(outCFile, 3);
            outCFile.println("delete m_cache;");
            outTabs(outCFile, 2);
            outCFile.println("}");

            outTabs(outCFile, 1);
            outCFile.println("public:");
            outTabs(outCFile, 2);
            outCFile.println("void InsertBean( const BeanType& bean)");
            outTabs(outCFile, 2);
            outCFile.println("{");
            outTabs(outCFile, 3);
            outCFile.println("m_cache->InsertBean( bean);");
            for (VarInfo info : indexSingle) {
                outTabs(outCFile, 3);
                outCFile.println("m_index_" + info.name + ".Insert( bean.id, bean." + info.name + ");");
            }
            for (VarInfo info : indexMulti) {
                outTabs(outCFile, 3);
                outCFile.println("m_index_" + info.name + ".Insert( bean.id, bean." + info.name + ");");
            }
            outTabs(outCFile, 2);
            outCFile.println("}");

            outCFile.println();


            outTabs(outCFile, 2);
            outCFile.println("virtual void getAllID( std::vector<int>& ids)");
            outTabs(outCFile, 2);
            outCFile.println("{");
            outTabs(outCFile, 3);
            outCFile.println("return m_cache->GetAllID( ids);");
            outTabs(outCFile, 2);
            outCFile.println("}");

            outTabs(outCFile, 2);
            outCFile.println("virtual const BeanType& getRecorder( int id)");
            outTabs(outCFile, 2);
            outCFile.println("{");
            outTabs(outCFile, 3);
            outCFile.println("return m_cache->GetBean( id);");
            outTabs(outCFile, 2);
            outCFile.println("}");

            for (VarInfo info : indexSingle) {
                outTabs(outCFile, 2);
                if (info.isNeedRef())
                    outCFile.println("virtual const BeanType& getRecorderBy" + ClassNameUtil.capFirst(info.name) + "( const " + info.getCPPType() + "& key)");
                else
                    outCFile.println("virtual const BeanType& getRecorderBy" + ClassNameUtil.capFirst(info.name) + "( " + info.getCPPType() + " key)");

                outTabs(outCFile, 2);
                outCFile.println("{");
                outTabs(outCFile, 3);
                outCFile.println("return getRecorder( m_index_" + info.name + ".GetID( key));");
                outTabs(outCFile, 2);
                outCFile.println("}");
            }
            for (VarInfo info : indexMulti) {
                outTabs(outCFile, 2);
                if (info.isNeedRef())
                    outCFile.println("virtual const std::vector<int>& getRecorderBy" + ClassNameUtil.capFirst(info.name) + "( const " + info.getCPPType() + "& key)");
                else
                    outCFile.println("virtual const std::vector<int>& getRecorderBy" + ClassNameUtil.capFirst(info.name) + "( " + info.getCPPType() + " key)");

                outTabs(outCFile, 2);
                outCFile.println("{");
                outTabs(outCFile, 3);
                outCFile.println("return m_index_" + info.name + ".GetID( key);");
                outTabs(outCFile, 2);
                outCFile.println("}");
            }


            outTabs(outCFile, 1);
            outCFile.println("};");

            outCFile.println();
            outCFile.println("// 根据CPP类型得到类型描述字符串");

            outTabs(outCFile, 1);
            outCFile.println("template<> inline const wchar_t* GetValueTypeName<" + beantype + ">()");
            outTabs(outCFile, 1);
            outCFile.println("{");
            outTabs(outCFile, 2);
            outCFile.println("return L\"" + cname.getClassnameString() + "\";");
            outTabs(outCFile, 1);
            outCFile.println("}");
            outCFile.println();

            outCFile.println();
            outCFile.println("} // namespace XMLCONFIG {");
            outCFile.println();
            return true;
        }
    }

    private Dependence dependence = new Dependence();

    private Args args ;
    public GeneratorCpp(Args args) {
        this.args = args;
    }

    private void writeCppFile(final String dstdir, final String targetClassname, org.w3c.dom.Element e) {
        BeanMaker bm = new BeanMaker(dstdir, targetClassname, e);
        if (!bm.outHFile())
            logger.error("outHFile fail " + targetClassname);
        if (!bm.outCFile())
            logger.error("outCFile fail " + targetClassname);
        bm.checkDependence(dependence);
    }

    private void writeManageCppFile(final String dstdir) {
        String[] classnames = dependence.getClassnames();
        java.io.PrintStream ps;
        try {
            String filename = dstdir + "/tablemanager.cpp";
            new java.io.File(filename).getParentFile().mkdirs();
            ps = new java.io.PrintStream(new java.io.FileOutputStream(filename), true, "utf-8");
        } catch (final Exception ex) {
            logger.error("err", ex);
            return;
        }

        ps.println("#include \"xmlconfig.h\"");

        ps.println();

        ps.println("// 需要包含的头文件");
        final int classcount = classnames.length;
        for (int i = 0; i < classcount; i++) {
            String hfilename = classnames[i].replaceAll("\\.", "/");
            ps.println("#include \"" + hfilename + ".h\"");
        }

        ps.println();

        ps.println("// 需要包含的inl文件");
        for (int i = 0; i < classcount; i++) {
            String inlfilename = classnames[i];
            ps.println("#include \"" + inlfilename + ".inl\"");
        }

        ps.println();

        ps.println("// 所有表对象的集合");
        ps.println("namespace XMLCONFIG { ");

        ps.println();
        outTabs(ps, 1);
        ps.println("struct TableInstances");
        outTabs(ps, 1);
        ps.println("{");

        for (int i = 0; i < classcount; i++) {
            outTabs(ps, 2);
            String tablename = classnames[i].replaceAll("\\.", "_");
            ps.println(tablename + "_table_implement table_" + tablename + ";");
        }

        ps.println();

        outTabs(ps, 2);
        ps.println("TableInstances( CConfigManager& manager)");
        if (classcount > 0) {
            outTabs(ps, 3);
            String tablename = classnames[0].replaceAll("\\.", "_");
            String beantype = classnames[0].replaceAll("\\.", "::");
            ps.println(": table_" + tablename + "(  manager.CreateBeanCacheInstance<" + beantype + ">())");
        }
        for (int i = 1; i < classcount; i++) {
            outTabs(ps, 3);
            String tablename = classnames[i].replaceAll("\\.", "_");
            String beantype = classnames[i].replaceAll("\\.", "::");
            ps.println(", table_" + tablename + "(  manager.CreateBeanCacheInstance<" + beantype + ">())");
        }
        outTabs(ps, 2);
        ps.println("{");
        for (int i = 0; i < classcount; i++) {
            outTabs(ps, 3);
            String tablename = classnames[i].replaceAll("\\.", "_");
            ps.println("manager.MakeTableValues( table_" + tablename + ");");
        }
        outTabs(ps, 2);
        ps.println("}");

        outTabs(ps, 1);
        ps.println("};");

        ps.println();

        outTabs(ps, 1);
        ps.println("TableInstances* g_tableinstancesinstance = NULL;");
        ps.println();
        outTabs(ps, 1);
        ps.println("void InitializeTableInstances(CConfigManager& manager)");
        outTabs(ps, 1);
        ps.println("{");
        outTabs(ps, 2);
        ps.println("assert( NULL == g_tableinstancesinstance);");
        outTabs(ps, 2);
        ps.println("g_tableinstancesinstance = new TableInstances( manager);");
        outTabs(ps, 1);
        ps.println("}");
        ps.println();
        outTabs(ps, 1);
        ps.println("void UninitializeTableInstances()");
        outTabs(ps, 1);
        ps.println("{");
        outTabs(ps, 2);
        ps.println("delete g_tableinstancesinstance;");
        outTabs(ps, 2);
        ps.println("g_tableinstancesinstance = NULL;");
        outTabs(ps, 1);
        ps.println("}");

        ps.println();
        ps.println("} // namespace XMLCONFIG { ");

        ps.println();

        ps.println("// 开始定义每个GetXXXTableInstance");
        ps.println();

        for (int i = 0; i < classcount; i++) {
            ClassNameUtil cnames = new ClassNameUtil(classnames[i]);
            String[] namespaces = cnames.getNamespaceArray();

            final int nscount = namespaces.length;
            for (int j = 0; j < nscount; j++)
                ps.print("namespace " + namespaces[j] + " { ");
            ps.println();
            ps.println();
            outTabs(ps, 1);
            ps.println(cnames.getShortName() + "Table& Get" + cnames.getShortName() + "TableInstance()");
            outTabs(ps, 1);
            ps.println("{");

            outTabs(ps, 2);
            String tablename = cnames.getClassnameString().replaceAll("\\.", "_");
            ps.println("return XMLCONFIG::g_tableinstancesinstance->table_" + tablename + ";");

            outTabs(ps, 1);
            ps.println("}");

            ps.println();

            for (int j = 0; j < nscount; j++)
                ps.print("} ");

            ps.println();
            ps.println();
        }

    }

    private javax.xml.xpath.XPathFactory xpathFactory = javax.xml.xpath.XPathFactory.newInstance();
    private static Logger logger = LoggerFactory.getLogger(GeneratorCpp.class);

    public void generate() {
        // bean定义文件
        final String xmlpath = args.genCodeXmlPath;
        // 生好的C++文件放哪里
        final String dstdir = args.dstDir;

        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setXIncludeAware(true);
        factory.setNamespaceAware(true);
        org.w3c.dom.Document doc;
        try {
            javax.xml.parsers.DocumentBuilder db = factory.newDocumentBuilder();
            doc = db.parse(xmlpath);
        } catch (final Exception ex) {
            logger.error("parse doc fail", ex);
            return;
        }
        if (doc == null)
            return;

        Object xpathresult = null;
        try {
            xpathresult = xpathFactory.newXPath().evaluate("//bean", doc, javax.xml.xpath.XPathConstants.NODESET);
            if (xpathresult == null) {
                logger.error("格式不正确");
                return;
            }

            org.w3c.dom.NodeList nl = (org.w3c.dom.NodeList) xpathresult;
            for (int i = 0; i != nl.getLength(); ++i) {
                org.w3c.dom.Element e = (org.w3c.dom.Element) nl.item(i);
                if (!e.getAttribute("genxml").equals("client"))
                    continue;
                final String targetClassname = ClassNameUtil.xmlpathToNS(e);
                logger.info("writing " + targetClassname);
                writeCppFile(dstdir, targetClassname, e);
            }
            writeManageCppFile(dstdir);
            logger.error("C++文件生成成功");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}
