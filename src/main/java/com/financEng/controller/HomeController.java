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
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        log.info(">> [/] - Home page");
	    return "index";
	}

	/**
     * User profile details page view controller.
     * Send information to the FrontEnd
     * */
    @RequestMapping("/userprofile")
    public String userprofile(Model model){

        user = getBackAuthUser();
        log.info(">> [userprofile] - User Profile | GetAutUser: "+user.toString());

        model.addAttribute("profileDetails",user);
        return "userprofile";
    }

    /**
     * User profile details and save modifications.
     * Send information to the FrontEnd
     * */
    @RequestMapping("/changeuserprof")
    public String saveModifiedUserDetails(Model model){

        user = getBackAuthUser();
        log.info(">> [changeuserprof] - Change User Profile | GetAutUser: "+user.toString());

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

        user = getBackAuthUser();
        log.info(">> [changeuserpass] - Change User Password | GetAutUser: "+user.toString());

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

		User user = new User();
        log.info(">> [registration] - User Registration | RegNewUser: "+user.toString());

		model.addAttribute("user",user);
		model.addAttribute("genders",User.Gender.values());
		return "registration";
	}

    /***********************************************************/
    /** Rest Controllers **/
    /***********************************************************/

    /**
     * Registration POST when a new user was created.
     * Get information from the FrontEnd
     * */
    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    public String reg(@ModelAttribute User user) {
        log.info(">> [reg] - User Registration - POST | NewUserEmail: "+user.getEmail());
        log.debug(">> [reg] - User Registration - POST | NewUserDetails: "+user.toString());

        if(userService.findByEmail(user.getEmail()) != null){
            log.warn(">> [reg] - This user already exist in the system: ["+user.getEmail()+"] !");
            return "redirect:/login?userexist";
        }
        else {
            userService.registerUser(user);
            emailService.sendMessage(user);

            log.info(">> [reg] - New User - Created | User: "+user.getEmail());
            return "redirect:/login?usercreated";
        }
    }

    /**
     * Modifications on user POST.
     * Get information from the FrontEnd
     * */
    @RequestMapping(value = "/saveUserChanges", method = RequestMethod.POST)
    public String saveUserChanges(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) {
        log.info(">> [saveUserChanges] - Save User Changes - POST | UserEmail: "+user.getEmail());
        log.debug(">> [saveUserChanges] - Save User Changes - POST | UserDetails: "+user.toString());

        if(user.getPassword() == null){
            log.info(">> [saveUserChanges] - Save User detail changes | UserEmail: "+user.getEmail());
            userService.saveUserModify(user);
        }
        else{
            log.info(">> [saveUserChanges] - Save User password change | UserEmail: "+user.getEmail());
            userService.saveUserPassword(user);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        log.info(">> [saveUserChanges] - Redirecting... to Login page.");
        return "redirect:/login?logout";
    }

    /**
     * Send the activation code and verify it with a GET method.
     * Get information from the FrontEnd
     * */
    @RequestMapping(path = "/just", method = RequestMethod.GET)
    public String just() {
        return "redirect:/login?just";
    }


    /**
     * Send the activation code and verify it with a GET method.
     * Get information from the FrontEnd
     * */
    @RequestMapping(path = "/activation/{code}", method = RequestMethod.GET)
    public String activation(@PathVariable("code") String code) {
        String regAfform = userService.userActivation(code);
        return "redirect:/login?actsuccess";
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
