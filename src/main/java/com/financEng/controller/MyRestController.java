package com.financEng.controller;

import com.financEng.entity.User;
import com.financEng.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class MyRestController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    private User user;

    /*==================================================================================================================
     || Test Rest Controllers
     ==================================================================================================================*/

    /***************************************
     * Simple Hello Endpoint
     * **************************************/
    @RequestMapping(value = "/admin/hello", method = RequestMethod.GET)
    public String getHello() {
        log.info(">> [/admin/hello] - Admin Hello Page | Says hello...");
        return "Hello";
    }

    /***************************************
     * Simple Exception
     * **************************************/
    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public String getExceptionTemplate() throws Exception {
        String exceptMessage = "This is an exception message";
        if(true){
            log.info(">> [/exception] - This endpoint throw EXCEPTION. | "+exceptMessage);
            throw new Exception(exceptMessage);
        }
        return "exception";
    }

    /***************************************
     * Get back the Gender Enums in JSON
     * **************************************/
    @RequestMapping(value = "/admin/genders", method = RequestMethod.GET)
    public List<User.Gender> getGenderEnums() {
        log.info(">> [/admin/genders] - Get the Gender Enums | You can see the Gender types.");
        return Arrays.asList(User.Gender.values());
    }

    /***************************************
     * Get back the user detail in JSON
     * **************************************/
    @RequestMapping(value = "/admin/userprofile", method = RequestMethod.GET)
    public User getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)){
            log.info(">> [/admin/userprofile] - Get the Authenticated user Details. | You can see your details in JSON.");
            user = userService.findByEmail(auth.getName());
            return user;
        }
        else
            return null;
    }

}
