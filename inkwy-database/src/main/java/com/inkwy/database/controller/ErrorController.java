package com.inkwy.database.controller;

import com.inkwy.database.domain.Response;
import com.inkwy.database.enums.CodeEnum;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@RestController
public class ErrorController {

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class })
    public Response<Object> handleException(HttpServletRequest req, HttpServletResponse resp, HttpRequestMethodNotSupportedException e) {
        return Response.resp(CodeEnum.FAILED.getCode(),e.getMessage(),null);
    }

    @ExceptionHandler({IllegalArgumentException.class })
    public Response<Object> handleException(HttpServletRequest req,HttpServletResponse resp,IllegalArgumentException e) {
        return Response.resp(CodeEnum.FAILED.getCode(),e.getMessage(),null);
    }


}
