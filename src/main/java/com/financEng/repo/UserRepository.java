package com.financEng.repo;

import com.financEng.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmail(String email);

	User findByActivation(String code);

	User findByFNameAndSName(String fname,String sname);

	User findById(Long id);

	
}