package com.zxw.reggie.common;

/**
 *  基于ThreadLocal封装工具类,用于保存获取当前用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();
    //设置值
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
    //获取值
    public static Long getCurrentId() {
        return threadLocal.get();
    }



}
