package com.jwt.repo;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jwt.entities.UserModel;
@Repository
public interface UserRepo extends JpaRepository<UserModel, Long> {

	UserModel findByUsername(String username);
	Optional<UserModel> findByEmail(String email);
	@Query("SELECT u FROM UserModel u WHERE u.name LIKE :#{#x}")
	public Page<UserModel> chercherUser(@Param("x") String mc,Pageable pageable);
	
	 @Transactional
	   @Modifying
	   @Query("UPDATE UserModel a " +
	           "SET a.enabled = TRUE WHERE a.email = ?1")
	   int enableUserModel(String email);
	 
	 @Query("SELECT u FROM UserModel u WHERE u.username = ?1 OR u.email = ?1")
	 UserModel connectUser(String identifiant);
}
