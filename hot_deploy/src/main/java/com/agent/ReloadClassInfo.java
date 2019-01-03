package com.agent;

/**
 * @author wang dongfang
 * @ClassName ReloadClassInfo.java
 * @Description TODO
 * @createTime 2019年01月02日 16:54:00
 */
public class ReloadClassInfo {
    private String swapClassName;
    private byte[] bs;
    private boolean isNew;
    private Class swapClass;

    public ReloadClassInfo(String swapClassName, byte[] bs, Class swapClass, boolean isNew) {
        this.swapClassName = swapClassName;
        this.bs = bs;
        this.swapClass = swapClass;
        this.isNew = isNew;
    }

    public String getSwapClassName() {
        return swapClassName;
    }

    public void setSwapClassName(String swapClassName) {
        this.swapClassName = swapClassName;
    }

    public byte[] getBs() {
        return bs;
    }

    public void setBs(byte[] bs) {
        this.bs = bs;
    }

    public Class getSwapClass() {
        return swapClass;
    }

    public void setSwapClass(Class swapClass) {
        this.swapClass = swapClass;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
