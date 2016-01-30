package com.dianping.data.warehouse.web.controller;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.common.Const;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mt on 2014/5/13.
 */
public abstract class AbstractController {
    @ModelAttribute("user")
    private UserDO getUser(HttpServletRequest request) {
        return (UserDO) request.getAttribute(Const.REQUEST_ATTR_USER);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private
    @ResponseBody
    Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException error) {
        BindingResult bindingResult = error.getBindingResult();
        Result<String> result = new Result<String>();
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            result.setMessages(message);
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 隐藏错误细节，返回预定义exception
     */
    protected Result getExceptionResult(String exception) {
        Result<String> result = new Result<String>();
        result.setSuccess(false);
        result.setMessages(exception);
        return result;
    }

    /**
     * 显示错误细节
     */
    protected Result getExceptionResult(Exception e) {
        Result<String> result = new Result<String>();
        result.setSuccess(false);
        result.setMessages(e.getMessage());
        return result;
    }
}
