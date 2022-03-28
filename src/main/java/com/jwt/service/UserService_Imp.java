package com.jwt.service;

import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jwt.entities.Contact;
import com.jwt.entities.Role_Model;
import com.jwt.entities.Status_value;
import com.jwt.entities.UserModel;
import com.jwt.repo.RoleRepo;
import com.jwt.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice.OffsetMapping.Sort;

@Service @Transactional
@RequiredArgsConstructor
public class UserService_Imp implements UserService,UserDetailsService {
    
	@Autowired
	private UserRepo userRepo;
	@Autowired 
	private RoleRepo roleRepo;
	@Autowired
	private PasswordEncoder passWordEncoder;
//	@Override
//	public Page<UserModel> getAllUsers(Integer page) {
//		return userRepo.findAll(PageRequest.of(0,page));			
//	}
	@Override
	public Page<UserModel> getAllUsers(String mc,int page,int size) {
		return userRepo.chercherUser("%"+mc+"%", PageRequest.of(page,size));			
	}
	

	@Override
	public Optional<UserModel> findUser(Long id) {
		Optional<UserModel> user =  userRepo.findById(id);
		if(!user.isPresent()) {
			throw new IllegalStateException(
					"Usser with Id "+ id + "does not Exists !!!" );
		}
		return user;
	}

	@Override
	public UserModel updateUser(UserModel user, Long id) {
		 userRepo.findById(id)
				.orElseThrow(()->new IllegalStateException(
						" User with Id "+ id + "does not exists !!!"));
		user.setId(id);
		return userRepo.save(user);
	}

	@Override
	public boolean deleteUser(Long id) {
		boolean exist = userRepo.existsById(id);
		if(!exist) {
			 throw new IllegalStateException(
					 " User with Id "+ id + "does not exists !!!");
		}
		userRepo.deleteById(id);
		return true;
	}

	@Override
	public UserModel saveUser(UserModel user) {
		Optional<UserModel> userByEmail = userRepo.findByEmail(user.getEmail());
		  if(userByEmail.isPresent()) {
			  throw new IllegalStateException(" Email taken !!!");
		  }
			user.setPassWord(passWordEncoder.encode(user.getPassWord()));

		  userRepo.save(user);
		  user.setStatus(Status_value.ACTIVE);
		  this.addRoleToUser(user.getUsername(), "ROLE_USER");
		return user;
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		UserModel   user = userRepo.findByUsername(username);
	     Role_Model  role = roleRepo.findByName(roleName);
	      user.getRole().add(role);
		
	}

	@Override
	public void addContactToUser(String username, Contact contact) {
		UserModel user = userRepo.findByUsername(username);
		user.getContact().add(contact);
		
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserModel user = userRepo.findByUsername(username);
		if(user==null || user.getStatus().equals(Status_value.DESACTIVATE)) {
			throw new UsernameNotFoundException("user not found in data base");
		}
		Collection<SimpleGrantedAuthority> autorities = new ArrayList<>();
		user.getRole().forEach(role ->{autorities.add(new SimpleGrantedAuthority(role.getName()));});
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassWord(), autorities);
	}

	@Override
	public Role_Model saveRole(Role_Model role) {
		
		return  roleRepo.save(role);
	}

	@Override
	public UserModel getUser(String userName) {
		
			return userRepo.findByUsername(userName);
		
	}

	@Override
	public void removeRoleToUser(String userName, String roleName) throws IllegalStateException {
		UserModel user = userRepo.findByUsername(userName);
		Role_Model role = roleRepo.findByName(roleName);
		if(user.getRole().contains(role)) {
			user.getRole().remove(role);
		}else {
			throw new IllegalStateException("Role not Found !!!");
		}
		
	}

	@Override
	public List<Contact> contactForSingleUser(String username) {
	 UserModel user = userRepo.findByUsername(username);
		return user.getContact();
	}

	@Override
	public boolean desactivateUser(Long id) {
		UserModel user = userRepo.findById(id).orElseThrow(
			       ()->new IllegalStateException("user with id "+ id +" not found"));
	user.setStatus(Status_value.DESACTIVATE);
		return true;
	}

	@Override
	public boolean activateUser(Long id) {
		UserModel user = userRepo.findById(id).orElseThrow(
			       ()->new IllegalStateException("user with id "+ id +" not found"));
	user.setStatus(Status_value.ACTIVE);
		return true;
	}
	

}
