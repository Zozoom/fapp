package com.financEng.controller;

import com.financEng.entity.User;
import com.financEng.service.UserService;
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

    @Autowired
    private UserService userService;

    private User user;

    /**
     *
     * */
    @RequestMapping(value = "/admin/hello", method = RequestMethod.GET)
    public String getHello() {
        return "Hello";
    }

    /**
     *
     * */
    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public String getExceptionTemplate() throws Exception {
        if(true)
            throw new Exception("Proba exception");
        return "Exception";
    }

    /**
     *
     * */
    @RequestMapping(value = "/admin/genders", method = RequestMethod.GET)
    public String getGenderEnums() {
        List<User.Gender> enums = Arrays.asList(User.Gender.values());
        return enums.toString();
    }

    /**
    *
    * */
    @RequestMapping(value = "/admin/userprofile", method = RequestMethod.GET)
    public String getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)){
            String fname = auth.getName().split(" ")[0];
            String sname = auth.getName().split(" ")[1];
            user = userService.findByName(fname,sname);
            System.out.println("Here is the user: "+user.toString());
            return user.getfName()+" "+user.getsName();
        }
        else
            return "You are not logged in... ";
    }

}
