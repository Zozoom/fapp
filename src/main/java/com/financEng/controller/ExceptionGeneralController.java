package com.financEng.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionGeneralController {

    @ExceptionHandler
    public String exceptionHandlerNacked(Exception ex, Model model){
        model.addAttribute("StackTrace",ex.getStackTrace());
        model.addAttribute("Cause",ex.getCause());
        model.addAttribute("Message",ex.getMessage());
        return "exceptionError";
    }

}
