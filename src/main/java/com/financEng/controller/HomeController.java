package com.financEng.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.financEng.entity.User;
import com.financEng.service.EmailService;
import com.financEng.service.UserService;

@Controller
public class HomeController {
	
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private UserService userService;
    
    private EmailService emailService;

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/** Oldal elérhetőségek **/

	@RequestMapping("/")
	public String home(){
		return "index";
	}
	
	@RequestMapping("/bloggers")
	public String bloggers(){
		return "bloggers";
	}
	
	@RequestMapping("/stories")
	public String stories(){
		return "stories";
	}

    @RequestMapping("/userprofile")
    public String userprofile(@ModelAttribute User user, Model model){
	    user = userService.findByEmail(user.getEmail());
        model.addAttribute("profileDetails",user);
        return "userprofile";
    }
	
	@RequestMapping("/registration")
	public String registration(Model model){
		User user = new User();
		model.addAttribute("user",user);
		model.addAttribute("genders",User.Gender.values());
		return "registration";
	}

	/** Rest Controllerek **/

	@RequestMapping(value = "/reg", method = RequestMethod.POST)
    public String reg(@ModelAttribute User user) {
		log.info("Uj user!");
		log.debug(user.toString());

		userService.registerUser(user);
		emailService.sendMessage(user);
        return "auth/login";
    }
	
	 @RequestMapping(path = "/activation/{code}", method = RequestMethod.GET)
	    public String activation(@PathVariable("code") String code, HttpServletResponse response) {
		userService.userActivation(code);
		return "auth/login";
	 }

}
