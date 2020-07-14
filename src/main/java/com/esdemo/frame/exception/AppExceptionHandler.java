package com.esdemo.frame.exception;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.bean.ResponseType;
import com.esdemo.frame.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.UnexpectedTypeException;
import java.sql.SQLException;

@Slf4j
@ResponseBody
@ControllerAdvice
public class AppExceptionHandler {


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseBean unexpectedType(UnexpectedTypeException exception) {
        log.error("校验方法太多，不确定合适的校验方法。{}", ExceptionUtils.collectExceptionStackMsg(exception));
        return ResponseType.SERVICE_ERROR.getResponseBean();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseBean messageNotReadable(HttpMessageNotReadableException exception) {
        log.error("请求参数不匹配。{}", ExceptionUtils.collectExceptionStackMsg(exception));
        return ResponseType.SERVICE_ERROR.getResponseBean();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseBean dataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("新增或更新sql异常: {}", ExceptionUtils.collectExceptionStackMsg(exception));
        return ResponseType.SERVICE_ERROR.getResponseBean();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = AppException.class)
    public ResponseBean handleCmsException(AppException exception) {
        log.error("自定义错误: {}", ExceptionUtils.collectExceptionStackMsg(exception));
        return ResponseBean.error(exception.getCode(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseBean handleNoHandlerFoundException(NoHandlerFoundException exception) {
        log.error("没找到请求:{}", ExceptionUtils.collectExceptionStackMsg(exception));
        return ResponseType.SERVICE_ERROR.getResponseBean();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public ResponseBean handleException(Exception exception) {
        log.error("其他异常: {}", ExceptionUtils.collectExceptionStackMsg(exception));
        return ResponseType.SERVICE_ERROR.getResponseBean();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SQLException.class)
    public ResponseBean handleSQLException(SQLException exception) {
        log.error("sql异常:{}", ExceptionUtils.collectExceptionStackMsg(exception));
        return ResponseType.SERVICE_ERROR.getResponseBean();
    }

}
