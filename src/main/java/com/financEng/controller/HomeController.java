package com.financEng.controller;

import com.financEng.entity.User;
import com.financEng.service.EmailService;
import com.financEng.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController {
	
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private UserService userService;
    
    private EmailService emailService;

    private User user;

    /***********************************************************/
    /** SERVICES | Setting up services **/
    /***********************************************************/

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

    /***********************************************************/
	/** Page View Controllers with Models **/
    /***********************************************************/

    /**
     * Main page View Controller.
     * Send information to the FrontEnd
     * */
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

	/**
     * User profile details page view controller.
     * Send information to the FrontEnd
     * */
    @RequestMapping("/userprofile")
    public String userprofile(Model model){
        user = getBackAuthUser();
        log.debug(user.toString());
        model.addAttribute("profileDetails",user);
        return "userprofile";
    }

    /**
     * User profile details and save modifications.
     * Send information to the FrontEnd
     * */
    @RequestMapping("/changeuserprof")
    public String saveModifiedUserDetails(Model model){
        log.info("User Profile Change.");
        user = getBackAuthUser();
        log.debug(user.toString());
        model.addAttribute("profileDetails",user);
        model.addAttribute("genders",User.Gender.values());
        return "changeuserprof";
    }

    /**
     * User profile details and save modifications.
     * Send information to the FrontEnd
     * */
    @RequestMapping("/changeuserpass")
    public String saveModifiedUserPasswrod(Model model){
        log.info("User Password Change.");
        user = getBackAuthUser();
        log.debug(user.getPassword() +" - "+ user.toString());
        model.addAttribute("profileDetails",user);
        model.addAttribute("genders",User.Gender.values());
        return "changeuserpass";
    }

    /**
     * Registration Page View Controller
     * Send information to the FrontEnd
     * */
	@RequestMapping("/registration")
	public String registration(Model model){
        log.info("User Registration.");
		User user = new User();
		model.addAttribute("user",user);
		model.addAttribute("genders",User.Gender.values());
		return "registration";
	}

    /**
     * This method handles login GET requests.
     * If users is already logged-in and tries to goto login page again, will be redirected to list page.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login() {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(">>>>>>>>>>>>>>>>>>>>> "+userDetails);
        return "login";
    }

	/***********************************************************/
	/** Rest Controllerek **/
    /***********************************************************/

    /**
     * Registration POST when a new user was created.
     * Get information from the FrontEnd
     * */
	@RequestMapping(value = "/reg", method = RequestMethod.POST)
    public String reg(@ModelAttribute User user, Model model) {
		log.info("The following new user email: "+user.getEmail());
		log.debug("The following new user details: "+user.toString());

		if(userService.findByEmail(user.getEmail()) != null){
            log.warn("This user already exist in the system: ["+user.getEmail()+"] !");
            model.addAttribute("userExist",true);
		    return "auth/login";
        }
        else {
            userService.registerUser(user);
            emailService.sendMessage(user);
            log.info("User ["+user.getEmail()+"] was created !");
            model.addAttribute("userCreated",true);
            return "auth/login";
        }
    }

    /**
     * Modifications on user POST.
     * Get information from the FrontEnd
     * */
    @RequestMapping(value = "/saveUserChanges", method = RequestMethod.POST)
    public String saveUserChanges(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) {
        log.info("The following user was modificated: " +user.getEmail());
        log.debug("The following user was modificated: "+user.getPassword()+" "+user.toString());

        if(user.getPassword() == null){
            userService.saveUserModify(user);
        }
        else{
            userService.saveUserPassword(user);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }

    /**
     * Send the activation code and verify it with a GET method.
     * Get information from the FrontEnd
     * */
	 @RequestMapping(path = "/activation/{code}", method = RequestMethod.GET)
	    public String activation(@PathVariable("code") String code, HttpServletResponse response) {
		userService.userActivation(code);
		return "auth/login";
	 }

    /***********************************************************/
    /** Private methods | Helpers **/
    /***********************************************************/

    private User getBackAuthUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)){
            String fname = auth.getName().split(" ")[0];
            String sname = auth.getName().split(" ")[1];
            user = userService.findByName(fname,sname);
        }
        else {
            user = new User();
        }
        return user;
    }

}
