package com.jwt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.jwt.entities.Contact;
import com.jwt.entities.Role_Model;
import com.jwt.entities.UserModel;

public interface UserService {
  
	Page<UserModel> getAllUsers(String mc,int page, int size);
	Optional<UserModel> findUser(Long id);
	UserModel updateUser(UserModel user,Long id);
	UserModel getUser(String userName);
	boolean  deleteUser(Long id);
	UserModel saveUser(UserModel user);
	Role_Model saveRole(Role_Model role);
	void addRoleToUser(String userName,String roleName);
	void removeRoleToUser(String userName,String roleName);
	void addContactToUser(String username, Contact contact);
	List<Contact> contactForSingleUser(String username);
	boolean desactivateUser(Long id);
	boolean activateUser(Long id);

	
}
