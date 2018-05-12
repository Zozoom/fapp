package com.financEng.service;

import com.financEng.entity.Role;
import com.financEng.entity.User;
import com.financEng.repo.RoleRepository;
import com.financEng.repo.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	/***********************************
	 * DO the Logic and Calculations!
	 * Send it back to the Controller
	 * in String format usually.
	 * *********************************/
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	/*==================================================================================================================
     || Load User by Username (email)
     ==================================================================================================================*/

	/****************************************************************
	 * Logging in go through here before grant access to the pages.
	 * **************************************************************/
	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		User user = findByEmail(userEmail);

		if (user == null) {
            log.error(">> [loadUserByUsername] - Error cannot found USER: {}", userEmail);
			throw new UsernameNotFoundException(userEmail);
		}

		if(user.getActivation().isEmpty()){
			log.info(">> [loadUserByUsername] - Access Granted for: {}", user.getEmail());
		}
		else{
			log.info(">> [loadUserByUsername] - WARN! Login User: "+user.getEmail()+" | This user not activated !");
			log.debug(">> [loadUserByUsername] - WARN! User activation code: {}", user.getActivation());
			try {
				throw new Exception("User Access Denied! "+user.getEmail());
			} catch (Exception e) {
				log.error(">> [loadUserByUsername] - ["+getClass().getName()+"]");
				log.error(">> [loadUserByUsername] - Something went wrong with throwing exception: "+e.getMessage());
			}
		}

        loginTheUser(user);
		return new UserDetailsImpl(user);
	}

	/*==================================================================================================================
     || Find User by details.
     ==================================================================================================================*/

	@Override
    public User findByEmail(String email) {
		return userRepository.findByEmail(email);
    }

    @Override
    public User findByName(String fname, String sname) {
        return userRepository.findByFNameAndSName(fname,sname);
    }

	/*==================================================================================================================
     || Main User action methods Save, Delete, Modify etc.
     ==================================================================================================================*/

    /**************************************
     * This method register a new user.
     * ************************************/
    @Override
	public String registerUser(User userToRegister) {

    	/** Check the user is not exist yet. **/
		if (userRepository.findByEmail(userToRegister.getEmail()) != null) {
			log.error(">> [registerUser] - Error user already exist: {}", userToRegister.getEmail());
			return "user_exist_error";
		}

		/******************************************
		 * Get Role and Set it up.
		 * If no role like that it will be created.
		 * *****************************************/
		String USER_ROLE = "USER";
		Role userRole = roleRepository.findByRole(USER_ROLE);

		if (userRole != null) {
			userToRegister.getRoles().add(userRole);
		} else {
			userToRegister.addRoles(USER_ROLE);
		}
		log.debug(">> [registerUser] - User Role will be: "+USER_ROLE);

		/** User Enabled **/
		userToRegister.setEnabled(false);
		log.debug(">> [registerUser] - User Enabled: {}", userToRegister.getEnabled());

		/** User Locked **/
		userToRegister.setLocked(false);
		log.debug(">> [registerUser] - User Locked: {}", userToRegister.getLocked());

		/** User LoggedIn **/
		userToRegister.setLoggedIn(false);
		log.debug(">> [registerUser] - User LoggedIn: {}", userToRegister.getLoggedIn());

		/** User Password Encode **/
		userToRegister.setPassword(passwordEncoder.encode(userToRegister.getPassword()));
		log.debug(">> [registerUser] - User Password: {}", userToRegister.getPassword());

		/** User Creation Date **/
		Date createDate = new Date();
		userToRegister.setCreationDate(createDate);
		log.debug(">> [registerUser] - User Creation Date: {}", userToRegister.getCreationDate());

		/** User Expire Date Calculation **/
		Calendar c = Calendar.getInstance();
		c.setTime(createDate);
		c.add(Calendar.YEAR, 1);
		Date expireDate = c.getTime();

		/** User Expire Date Set**/
		userToRegister.setExpireDate(expireDate);
		log.debug(">> [registerUser] - User Expire Date: {}", userToRegister.getExpireDate());

		/** User Activation **/
		userToRegister.setActivation(generateKey());
		log.debug(">> [registerUser] - User Activation Code: {}", userToRegister.getActivation());

		/** User Save **/
		try{
			userRepository.save(userToRegister);
			log.info(">> [registerUser] - User ["+userToRegister.getEmail()+"] Successfully persisted to the database!");
		}
		catch (Exception e){
			log.error(">> [registerUser] - User ["+userToRegister.getEmail()+"] persisting error! User cannot persisted to database.");
			log.error(">> [registerUser] - Error: ["+e.getMessage()+"]");
		}

		/** Return with what... **/
		log.info(">> [registerUser] - Returning with a confirmation code.");
		return "user_persisted";
	}

	/********************************************************************
     * This method save the modified user.
	 * User Find by ID because the user can change his/her email address.
     * ******************************************************************/
    @Override
    public String saveUserModify(User userToModify) throws UsernameNotFoundException {
        User modifiedUser = userRepository.findById(userToModify.getId());

		/** Check the user is exist. Unee = 'User not exist error' **/
		if (modifiedUser == null) {
			log.error(">> [saveUserModify] - Error cannot found USER: {}", userToModify.getEmail());
			return "unee";
		}

        /** User FName **/
        if(userToModify.getfName().isEmpty()){
			log.error(">> [saveUserModify] - User First Name is empty.");
		}
        modifiedUser.setfName(userToModify.getfName());
		log.debug(">> [saveUserModify] - Change User Fist Name: {}", modifiedUser.getfName());


		/** User SName **/
		if(userToModify.getsName().isEmpty()){
			log.error(">> [saveUserModify] - User Second Name is empty.");
		}
		modifiedUser.setsName(userToModify.getsName());
		log.debug(">> [saveUserModify] - Change User Second Name: {}", modifiedUser.getsName());


		/** User Email **/
		if(userToModify.getEmail().isEmpty()){
			log.error(">> [saveUserModify] - User Email is empty.");
		}
		modifiedUser.setEmail(userToModify.getEmail());
		log.debug(">> [saveUserModify] - Change User Email: {}", modifiedUser.getEmail());


		/** User User Gender **/
		if(userToModify.getUserGender().isEmpty() || userToModify.getUserGender() == null){
			log.error(">> [saveUserModify] - User User Gender is empty.");
		}
		modifiedUser.setUserGender(userToModify.getUserGender());
		log.debug(">> [saveUserModify] - Change User Gender: {}", modifiedUser.getUserGender());

		/** User Save **/
		try{
			userRepository.save(modifiedUser);
			log.info(">> [saveUserModify] - User ["+modifiedUser.getEmail()+"] Successfully persisted to the database!");
		}
		catch (Exception e){
			log.error(">> [saveUserModify] - User ["+modifiedUser.getEmail()+"] persisting error! User cannot persisted to database.");
			log.error(">> [saveUserModify] - Error: ["+e.getMessage()+"]");
		}

		/** Return with what... **/
		log.info(">> [saveUserModify] - Returning with a confirmation code.");
		return "user_changed";
    }

	/********************************************************************
	 * This method save the user password change.
	 * User Find by ID because the user can change his/her email address.
	 * ******************************************************************/
	@Override
	public String saveUserPassword(User userToModify) {
		User modifiedUser = userRepository.findById(userToModify.getId());

		/** Check the user is exist. Unee = 'User not exist error' **/
		if (modifiedUser == null) {
			log.error(">> [saveUserPassword] - Error cannot found USER: {}", userToModify.getEmail());
			return "unee";
		}

		/** User Password **/
		if(userToModify.getPassword().isEmpty()){
			log.error(">> [saveUserPassword] - User Password is empty.");
		}
		modifiedUser.setPassword(passwordEncoder.encode(userToModify.getPassword()));
		log.debug(">> [saveUserPassword] - Change User Password: {}", modifiedUser.getPassword());

		/** User Save **/
		try{
			userRepository.save(modifiedUser);
			log.info(">> [saveUserPassword] - User ["+modifiedUser.getEmail()+"] Successfully persisted to the database!");
		}
		catch (Exception e){
			log.error(">> [saveUserPassword] - User ["+modifiedUser.getEmail()+"] persisting error! User cannot persisted to database.");
			log.error(">> [saveUserPassword] - Error: ["+e.getMessage()+"]");
		}

		/** Return with what... **/
		log.info(">> [saveUserPassword] - Password change was successfully saved.");
		return "user_pass_changed";
	}

	/********************************************************
     * This method set the generated code for the new user.
	 * And if the user no activation code in the DB then it
	 * drop back actSuccess answer.
     * *****************************************************/
	@Override
	public String userActivation(String code) {
		User user = userRepository.findByActivation(code);
		/** Check the user is exist. Unee = 'User not exist error' **/
		if (user == null) {
			log.warn(">> [userActivation] - User is already activated with this code: "+code);
			return "user_active";
		}
		
		user.setEnabled(true);
		log.debug(">> [userActivation] - Setting user enabled to login: "+user.getEmail()+" - "+user.getEnabled());
		user.setActivation("");
		log.debug(">> [userActivation] - Empty the activation code field.");

		/** User Save **/
		try{
			userRepository.save(user);
			log.info(">> [userActivation] - User ["+user.getEmail()+"] Successfully persisted to the database!");
		}
		catch (Exception e){
			log.error(">> [userActivation] - User ["+user.getEmail()+"] persisting error! User cannot persisted to database.");
			log.error(">> [userActivation] - Error: ["+e.getMessage()+"]");
		}

		/** Return with what... **/
		log.info(">> [userActivation] - Activation successfully saved.");
		return "act_success";
	}

	/*==================================================================================================================
     || Private methods
     ==================================================================================================================*/

	/**************************************
	 * This for testing the GeyGenFunction
	 * with a Simple RestController
	 * ************************************/
	@Override
	public String genActCodeImplicity() {
		return generateKey();
	}

	/**************************************
	 * This for testing the BCrypt
	 * with a Simple RestController
	 * ************************************/
	@Override
	public String genBCryptCode(String string) {
		return bycrypt(string);
	}

	/**************************************
	 * This for testing the LogOut the User
	 * with a Simple RestController
	 * ************************************/
	@Override
	public void logoutUser(User user) {
		logoutTheUser(user);
	}

	/***************************************************
     * Generate the activation code for user activation.
     * *************************************************/
    private String generateKey(){
		int length = 11;
		String generatedString = "fapp"+RandomStringUtils.random(length, true, true);

		log.debug(">> [generateKey] - Generated code for the new user: {}", generatedString);
		return generatedString;
    }

	/***************************************************
	 * Generate BCrypt code.
	 * *************************************************/
	private String bycrypt(String string){

		String generatedString = passwordEncoder.encode(string);

		log.debug(">> [bycrypt] - Generated bycrypt code: {}", generatedString);

		return generatedString;
	}

	/***********************************************************
	 * The Logout private method
	 * This method set the flag of user loggedIn, and save it
	 * to the database.
	 ***********************************************************/
	private void logoutTheUser(User user){
        User myuser = userRepository.findByEmail(user.getEmail());
		log.info(">> [logoutTheUser] - Logout the user...");

        myuser.setLoggedIn(false);

		/** User Save **/
		try{
			userRepository.save(myuser);
			log.info(">> [logoutTheUser] - User ["+myuser.getEmail()+"] Successfully persisted to the database!");
		}
		catch (Exception e){
			log.error(">> [logoutTheUser] - User ["+myuser.getEmail()+"] persisting error! User cannot persisted to database.");
			log.error(">> [logoutTheUser] - Error: ["+e.getMessage()+"]");
		}

	}

	/***********************************************************
	 * The Login private method
	 * This method set the flag of user loggedIn, and save it
	 * to the database.
	 ***********************************************************/
	private void loginTheUser(User user){
        User myuser = userRepository.findByEmail(user.getEmail());
		log.info(">> [loginTheUser] - Login the user...");

        myuser.setLoggedIn(true);
        myuser.setLastLogin(new Date());

		/** User Save **/
		try{
			userRepository.save(myuser);
			log.info(">> [loginTheUser] - User ["+myuser.getEmail()+"] Successfully persisted to the database!");
		}
		catch (Exception e){
			log.error(">> [loginTheUser] - User ["+myuser.getEmail()+"] persisting error! User cannot persisted to database.");
			log.error(">> [loginTheUser] - Error: ["+e.getMessage()+"]");
		}
	}
}