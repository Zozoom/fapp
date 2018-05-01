package com.financEng.repo;

import com.financEng.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	/*****************************
	 * Find User By Email
	 * ***************************/
	User findByEmail(String email);

	/*****************************
	 * Find User By Activation Code
	 * ***************************/
	User findByActivation(String code);

	/**************************************
	 * Find User By First and Last name
	 * ************************************/
	User findByFNameAndSName(String fname,String sname);

	/*****************************
	 * Find User By ID
	 * ***************************/
	User findById(Long id);

	
}