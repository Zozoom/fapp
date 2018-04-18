package com.financEng.service;

import com.financEng.entity.Role;
import com.financEng.entity.User;
import com.financEng.repo.RoleRepository;
import com.financEng.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private String activationCode;

	private final String USER_ROLE = "USER";

    /***********************************************************/
    /**  **/
    /***********************************************************/

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new UserDetailsImpl(user);
	}

	/*******************************************/
	/** Find users by something **/
    /*******************************************/

	@Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByName(String fname, String sname) {
        return userRepository.findByFNameAndSName(fname,sname);
    }

    /*******************************************/
    /** Function methods **/
    /*******************************************/

    /**
     * This method register a new user.
     * */
    @Override
	public String registerUser(User userToRegister) {
		User userCheck = userRepository.findByEmail(userToRegister.getEmail());

		/** Check user is exist or not * */
		if (userCheck != null)
			return "alreadyExists";

		/** Roles * */
		Role userRole = roleRepository.findByRole(USER_ROLE);
		if (userRole != null) {
			userToRegister.getRoles().add(userRole);
		} else {
			userToRegister.addRoles(USER_ROLE);
		}

		/** User Enabled **/
		userToRegister.setEnabled(false);

		/** User Locked **/
		userToRegister.setLocked(false);

		/** User Creation Date **/
		userToRegister.setCreationDate(new Date());

		/** User Expire Date Calculation **/
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.YEAR, 1);
		Date newDate = c.getTime();

		/** User Expire Date Set**/
		userToRegister.setExpireDate(newDate);

		/** User Activation **/
		activationCode = generateKey();
		userToRegister.setActivation(activationCode);

		/** User Save **/
		userRepository.save(userToRegister);

		/** Return with what... **/
		return "ok";
	}

	/**
     * This method save the modified user.
     * */
    @Override
    public String saveUserModify(User modifiedUser) {
        User userCheck = userRepository.findById(modifiedUser.getId());

        /** Check user is exist or not * */
        if (userCheck == null)
            return "User cannot Find!";

        /** User FName **/
        userCheck.setfName(modifiedUser.getfName());

        /** User SName **/
        userCheck.setsName(modifiedUser.getsName());

        /** User Email **/
        userCheck.setEmail(modifiedUser.getEmail());

        /** User Gender **/
        userCheck.setUserGender(modifiedUser.getUserGender());

        /** User Save **/
        userRepository.save(userCheck);

        log.debug("Saving was successfull.");

        /** Return with what... **/
        return "logout";
    }


    /**
     * This method generate the activation code.
     * */
	@Override
	public String userActivation(String code) {
		User user = userRepository.findByActivation(code);
		if (user == null)
		    return "noresult";
		
		user.setEnabled(true);
		user.setActivation("");
		userRepository.save(user);
		return "ok";
	}

    /*******************************************/
    /** Private methods **/
    /*******************************************/

    /**
     * Thos genereated the activation code itself.
     * */
    private String generateKey()
    {
        String key = "";
        Random random = new Random();
        char[] word = new char[16];
        for (int j = 0; j < word.length; j++) {
            word[j] = (char) ('a' + random.nextInt(26));
        }
        String toReturn = new String(word);
        log.debug("random code: " + toReturn);
        return new String(word);
    }
}
