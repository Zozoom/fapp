package com.financEng.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionGeneralController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /*==================================================================================================================
     || Exception Handlers
     ==================================================================================================================*/

    /************************************************
     * This handel all the exception.
     * *********************************************/
    @ExceptionHandler
    public String exceptionHandlerNacked(Exception ex, Model model){

        log.info(">> [exception] - Exception General page | Getting error details..");

        model.addAttribute("StackTrace",ex.getStackTrace());
        model.addAttribute("Cause",ex.getCause());
        model.addAttribute("Message",ex.getMessage());

        return "exception";
    }

}
