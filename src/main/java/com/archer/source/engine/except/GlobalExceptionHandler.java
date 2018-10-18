package com.archer.source.engine.except;

import com.archer.source.domain.entity.ExceptionInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    /** 
    * @description: 全局的异常处理
    * @author:      Zhao.Peng
    * @date:        2018/8/28 
    * @time:        15:48 
    * @param:
    * @return:
    */
    @ExceptionHandler(value = ArcherException.class)
    public @ResponseBody ExceptionInfo jsonExceptHandler(HttpServletRequest request, ArcherException e) throws Exception {
        ExceptionInfo except = new ExceptionInfo();
        except.setErrorMsg(e.getMessage());
        return except;
    }
}
