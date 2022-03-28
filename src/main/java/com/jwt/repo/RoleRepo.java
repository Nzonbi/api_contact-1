package com.jwt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwt.entities.Role_Model;
@Repository
public interface RoleRepo extends JpaRepository<Role_Model, Long> {

	Role_Model findByName(String roleName);

}
