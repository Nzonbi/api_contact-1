package com.jwt.service;

import java.util.List;
import java.util.Optional;

import com.jwt.entities.Contact;

public interface ContactService {

	List<Contact> getAllContact();
	Optional<Contact> findContact(Long id);
	Contact updateContact(Long id,Contact contact);
	boolean deleteContact(Long id);
	Contact saveContact(String userName , Contact contact);
	
}
