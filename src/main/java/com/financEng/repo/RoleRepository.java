package com.financEng.repo;
import org.springframework.data.repository.CrudRepository;

import com.financEng.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

	Role findByRole(String role);
	
}