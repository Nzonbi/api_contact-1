package com.jwt.repo;


import java.awt.print.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jwt.entities.Contact;
@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {
/**
 *selectionner la liste de des contact d'un utilisateur 
 **/
//	@Query("SELECT c FROM Contact c WHERE c.nom LIKE :#{#x}")
//	public Page<Contact> chercherContact(@Param("x") String mc,Pageable pageable);

}
