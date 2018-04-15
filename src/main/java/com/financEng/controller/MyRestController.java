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

    @RequestMapping(value = "/dev/genders", method = RequestMethod.GET)
    public String getGenderEnums() {
        List<User.Gender> enums = Arrays.asList(User.Gender.values());
        return enums.toString();
    }

    @RequestMapping(value = "/dev/userprofile", method = RequestMethod.GET)
    public String getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)){
            user = userService.findByEmail(auth.getName());
            System.out.println(user.toString());
            return user.getfName();
        }
        else
            return "You are not logged in... ";


    }

}
