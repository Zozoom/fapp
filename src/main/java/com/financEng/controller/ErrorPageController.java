package com.financEng.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class ErrorPageController implements ErrorController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String ERR_PATH = "/error";

    private ErrorAttributes errorAttributes;

    /*==================================================================================================================
     || Error page configurations and Autowire's
     ==================================================================================================================*/

    @Autowired
    public void setErrorAttributes(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return ERR_PATH;
    }

    /*==================================================================================================================
     || Error Page Request Mappers
     ==================================================================================================================*/

    /************************************************
     * This Catch the Error and give back a formal error page.
     * *********************************************/
    @RequestMapping(ERR_PATH)
    public String error (Model model, HttpServletRequest httpReq){
        RequestAttributes ra = new ServletRequestAttributes(httpReq);
        Map<String,Object> error = this.errorAttributes.getErrorAttributes(ra,true);

        log.info(">> [error] - Error page | Show error details in error page.");
        log.info(">> [error] - Error page | "+error.get("status")+" - "+error.get("error")+" | "+error.get("message"));

        model.addAttribute("timestamp",error.get("timestamp"));
        model.addAttribute("error",error.get("error"));
        model.addAttribute("message",error.get("message"));
        model.addAttribute("path",error.get("path"));
        model.addAttribute("status",error.get("status"));

        return "error";
    }

}
