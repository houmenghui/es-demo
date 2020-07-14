package com.esdemo;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.bean.ResponseType;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@ControllerAdvice
public class AppErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH)
    public ResponseBean handleError(HttpServletRequest request, HttpServletResponse response) {
        return ResponseType.NOT_FOUND.getResponseBean();
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

}
