package com.financEng.repo;

import org.springframework.data.repository.CrudRepository;

import com.financEng.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

	/*****************************
	 * Find Role By Role (Name)
	 * ***************************/
	Role findByRole(String role);
	
}