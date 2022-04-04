package com.jwt.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import com.jwt.entities.Contact;
import com.jwt.entities.UserModel;
import com.jwt.repo.ContactRepo;
import com.jwt.repo.UserRepo;
@Service @Transactional
public class Contactservice_imp implements ContactService {
  
	@Autowired
	private ContactRepo contactRepo;
	@Autowired 
	private UserRepo userRepo;
	@Autowired
	private UserService userService;
	@Override
	public Page<Contact> getAllContact(String mc,int page,int size) {
		return contactRepo.chercherContact("%"+mc+"%",PageRequest.of(page, size));
	}
	@Override
	public Optional<Contact> findContact(Long id) {
		return contactRepo.findById(id);
	}
	@Override
	public Contact updateContact(Long id, Contact contact) {
		Contact cont = contactRepo.findById(id).orElseThrow(
				       ()->new IllegalStateException("contact not found"));
		
		if(contact.getNom()!="" && 
				!Objects.equals(cont.getNom(), contact.getNom())) {
			cont.setNom(contact.getNom());
			
		}
		if(contact.getPrenom()!="" && 
				!Objects.equals(cont.getPrenom(), contact.getPrenom())) {
			cont.setPrenom(contact.getPrenom());
			
		}
		if(contact.getEmail()!="" && 
				!Objects.equals(cont.getEmail(), contact.getEmail())) {
			cont.setEmail(contact.getEmail());
			
		}
		if(contact.getTel()!="" && 
				!Objects.equals(cont.getTel(), contact.getTel())) {
			cont.setTel(contact.getTel());	
		}
		if(contact.getPhoto()!="" && 
				!Objects.equals(cont.getPhoto(), contact.getPhoto())) {
			cont.setPhoto(contact.getPhoto());
			
		}
		if(!contact.getSexe().equals("") && 
				!Objects.equals(cont.getSexe(), contact.getSexe())) {
			cont.setSexe(contact.getSexe());
			
		}
		return cont;
	}
	@Override
	public boolean deleteContact(Long id) {
		boolean exist = contactRepo.existsById(id);
		if(!exist) {
			throw new IllegalStateException(
					"contact with Id " + id + " does not exists");
		}
		contactRepo.deleteById(id);
		return true;
	}
	@Override
	public Contact saveContact(String username, Contact contact) {
		UserModel user = userRepo.findByUsername(username);
		if(user.getUsername() == null) {
			throw new IllegalStateException(
					"User with user name "+ username +"Does not exists!!");
		}
		userService.addContactToUser(user.getUsername(), contact);
		return contact;
	}
	
	

}
