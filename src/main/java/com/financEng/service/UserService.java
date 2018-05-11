package com.financEng.service;

import com.financEng.entity.User;

public interface UserService {
	
	String registerUser(User user);

    String saveUserModify(User user);

	String saveUserPassword(User user);

	User findByEmail(String email);

	User findByName(String fname,String sname);

    String userActivation(String code);

    String genActCodeImplicity();

    String genBCryptCode(String string);

    void logoutUser (User user);

	
}
