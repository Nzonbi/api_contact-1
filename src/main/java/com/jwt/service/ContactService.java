package com.jwt.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.jwt.entities.Contact;

public interface ContactService {

	Page<Contact> getAllContact(String mc,int page,int size);
	Optional<Contact> findContact(Long id);
	Contact updateContact(Long id,Contact contact);
	boolean deleteContact(Long id);
	Contact saveContact(String userName , Contact contact);
	
}
