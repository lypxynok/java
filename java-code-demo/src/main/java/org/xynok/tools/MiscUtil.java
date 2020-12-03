package org.xynok.tools;

/**
 * 杂项方法
 */
public final class MiscUtil {
    /**
     * 获取调用方法名，调用该函数得到调用函数的名字
     * 调用者压栈
     * getMethodName压栈
     * getStackTrace压栈
     * 因此栈底[2]是调用者
     * @return
     */
    public static String getMethodName(){
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}