package com.financEng.service;

import com.financEng.entity.User;

public interface UserService {
	
	String registerUser(User user);

	User findByEmail(String email);

	String userActivation(String code);
	
}
