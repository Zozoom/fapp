package com.financEng.controller;

import com.financEng.entity.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class MyRestController {

    @RequestMapping(value = "/dev/genders", method = RequestMethod.GET)
    public String getGenderEnums() {
        List<User.Gender> enums = Arrays.asList(User.Gender.values());
        return enums.toString();
    }

}
