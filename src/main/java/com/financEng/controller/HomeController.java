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
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class HomeController {

    /***********************************
     * Just Controls the threads.
     * Not do any Logic or Calculation!
     * *********************************/

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private UserService userService;

    private EmailService emailService;

    private User user;

    /*==================================================================================================================
      || SERVICES | Setting up services
      ==================================================================================================================*/

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

    /*==================================================================================================================
      || Page View Controllers with Models
      ==================================================================================================================*/

    /*********************************************************************
     * Main page View Controller.
     * Send information to the FrontEnd
     * getBackAuthUser(); method setup the authenticated user. IMPORTANT!
     * ********************************************************************/
	@RequestMapping("/")
	public String home(Model model){
        log.info(">> [/] - Home page");
        getBackAuthUser();
        model.addAttribute("profileDetails",user);
        model.addAttribute("todayDate",getActualDate("yyyy.MM.dd HH:mm:ss"));
	    return "index";
	}


    /*********************************************************************
     * Admin Page You can use the Pilot stuff.
     * ********************************************************************/
    @RequestMapping("/admin")
    public String admin(Model model){
        log.info(">> [/admin] - Admin page");
        model.addAttribute("profileDetails",user);
        model.addAttribute("todayDate",getActualDate("yyyy.MM.dd HH:mm:ss"));
        return "admin";
    }


    /*********************************************************************
     * This logout controller responsible for the user Logging out process.
     * ********************************************************************/
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        log.info(">> [/logout] - Call logout user.");

        dropAuthUser(request,response);

        return "redirect:/login?logout";
    }

	/*********************************************************************
     * User profile details page view controller.
     * Send information to the FrontEnd
     * ********************************************************************/
    @RequestMapping("/userprofile")
    public String userprofile(Model model){
        log.info(">> [userprofile] - User Profile | GetAutUser: {}", user.toString());
        model.addAttribute("profileDetails",user);
        return "userprofile";
    }

    /*********************************************************************
     * User profile details and save modifications.
     * Send information to the FrontEnd
     * ********************************************************************/
    @RequestMapping("/changeuserprof")
    public String saveModifiedUserDetails(Model model){
        log.info(">> [changeuserprof] - Change User Profile | GetAutUser: {}", user.toString());
        model.addAttribute("profileDetails",user);
        model.addAttribute("genders",User.Gender.values());
        return "changeuserprof";
    }

    /*********************************************************************
     * User profile details and save modifications.
     * Send information to the FrontEnd
     * ********************************************************************/
    @RequestMapping("/changeuserpass")
    public String saveModifiedUserPasswrod(Model model){
        log.info(">> [changeuserpass] - Change User Password | GetAutUser: {}", user.toString());
        model.addAttribute("profileDetails",user);
        model.addAttribute("genders",User.Gender.values());
        return "changeuserpass";
    }

    /*********************************************************************
     * Registration Page View Controller
     * Send information to the FrontEnd
     * ********************************************************************/
	@RequestMapping("/registration")
	public String registration(Model model){
		User user = new User();
        log.info(">> [registration] - User Registration | RegNewUser send an empty user to FE.");
		model.addAttribute("user",user);
		model.addAttribute("genders",User.Gender.values());
		return "registration";
	}

    /*==================================================================================================================
     || Action Page Controllers
     ==================================================================================================================*/

    /*********************************************************************
     * Registration POST when a new user was created.
     * Get information from the FrontEnd
     * ********************************************************************/
    @RequestMapping(value = "/registration/reg", method = RequestMethod.POST)
    public String reg(@ModelAttribute User user) {
        log.info(">> [reg] - User Registration - POST | NewUserEmail: {}", user.getEmail());
        log.debug(">> [reg] - User Registration - POST | NewUserDetails: {}", user.toString());

        String regStatus;
        regStatus = userService.registerUser(user);

        if(regStatus.contains("error")){
            log.warn(">> [reg:"+regStatus+"] - This user already exist in the system: ["+user.getEmail()+"] !");
            return "redirect:/login?"+regStatus;
        }
        else{
            emailService.sendActivationEmail(user);
            log.info(">> [reg:"+regStatus+"] - New User - Created | User: {}", user.getEmail());
            return "redirect:/login?"+regStatus;
        }

    }

    /*********************************************************************
     * Modifications on user POST.
     * Get information from the FrontEnd
     * ********************************************************************/
    @RequestMapping(value = "/saveUserChanges", method = RequestMethod.POST)
    public String saveUserChanges(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) {
        String changeStatus="";

        log.info(">> [saveUserChanges] - Save User Changes - POST | UserEmail: {}", user.getEmail());
        log.debug(">> [saveUserChanges] - Save User Changes - POST | UserDetails: {}", user.toString());

        if(user.getPassword() == null){
            log.info(">> [saveUserChanges] - Save User detail changes | UserEmail: {}", user.getEmail());
            changeStatus = userService.saveUserModify(user);
            //Here comes the Confirmation email type but first email service has to be upgrade.
            //emailService.sendMessage(user);
        }
        else{
            log.info(">> [saveUserChanges] - Save User password change | UserId: {}", user.getId());
            changeStatus = userService.saveUserPassword(user);
            //Here comes the Confirmation email type but first email service has to be upgrade.
            //emailService.sendMessage(user);
        }

        dropAuthUser(request,response);

        log.info(">> [saveUserChanges] - Redirecting and Logging out to the Login page.");

        return "redirect:/login?"+changeStatus;
    }

    /*********************************************************************
     * Send the activation code and verify it with a GET method.
     * Get information from the FrontEnd
     * ********************************************************************/
    @RequestMapping(path = "/easteregg", method = RequestMethod.GET)
    public String easteregg() {
        log.info(">> [easteregg] - Easter Egg shown.");
        return "redirect:/login?easteregg";
    }

    /*******************************************************************
     * Send the activation code and verify it with a GET method.
     * Get information from the FrontEnd
     * ******************************************************************/
    @RequestMapping(path = "/activation/{code}", method = RequestMethod.GET)
    public String activation(@PathVariable("code") String code) {
        log.info(">> [activation] - Activating with the following code: "+code);
        String isUserActive = userService.userActivation(code);

        if(isUserActive.equals("user_active")){
            log.info(">> [activation:"+isUserActive+"] - This code has been already used.");
            return "redirect:/login?"+isUserActive;
        }
        else{
            log.info(">> [activation:"+isUserActive+"] - Activation was successfully.");
            return "redirect:/login?"+isUserActive;
        }

    }

    /*==================================================================================================================
     || Private methods | Helpers
     ==================================================================================================================*/

    /***********************************************************
     * Simple get Actual Date with a simple format.
     * You have to give the Date format to the method.
     * "yyyy/MM/dd HH:mm:ss"
     ***********************************************************/
    private String getActualDate(String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    /***********************************************************
     * Get Authenticated User for more details.
     * This a private method.
     ***********************************************************/
    private User getBackAuthUser(){
        log.info(">> [getBackAuthUser] - Get authenticated user details.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)){
            user = userService.findByEmail(auth.getName());
            return user;
        }
        else {
            return new User();
        }
    }

    /***********************************************************
     * Get Authenticated User for more details.
     * This a private method.
     ***********************************************************/
    private void dropAuthUser(HttpServletRequest request, HttpServletResponse response){
        log.info(">> [dropAuthUser] - Clean up auth user...");

        userService.logoutUser(user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

    }

}
