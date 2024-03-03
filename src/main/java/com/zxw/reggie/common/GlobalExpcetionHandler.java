package com.zxw.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.CustomSQLExceptionTranslatorRegistrar;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//拦截这些类的controller，restcontroller注解
@ResponseBody//封装成json数据
@Slf4j
public class GlobalExpcetionHandler extends RuntimeException {
    /**
     * 异常处理方法
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error("数据重复异常：{}", exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")) {
            // 以空格分割异常信息
            String[] split = exception.getMessage().split(" ");
            // 拼接提示信息
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 异常处理方法
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.info(ex.getMessage());
        return R.error(ex.getMessage());
    }


}

