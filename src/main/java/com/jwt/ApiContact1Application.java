package com.jwt;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jwt.entities.Contact;
import com.jwt.entities.Role_Model;
import com.jwt.entities.Sexe;
import com.jwt.entities.Status_value;
import com.jwt.entities.UserModel;
import com.jwt.service.UserService;



@SpringBootApplication
public class ApiContact1Application implements CommandLineRunner {
 
	@Autowired
	private UserService userService;
	public static void main(String[] args) {
		SpringApplication.run(ApiContact1Application.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Override
	public void run(String... args) throws Exception {
		
		 userService.saveUser (new UserModel("tsague","case","case@gmail.com",Sexe.FEMME,"1234","toto.png",Status_value.ACTIVE,new ArrayList<>(),new ArrayList<>()));
		 userService.saveUser(new UserModel("franck","thibaut","thibaut@gmail.com",Sexe.FEMME,"1234","toto.png",Status_value.ACTIVE,new ArrayList<>(),new ArrayList<>()));
		 userService.saveUser(new UserModel("tomy","chuck","chuck@gmail.com" ,Sexe.FEMME,"1234" ,"toto.png",Status_value.ACTIVE,new ArrayList<>(),new ArrayList<>()));
		 userService.saveUser(new UserModel("sarra","morgan","morgan@gmail.com",Sexe.FEMME,"1234" ,"toto.png",Status_value.ACTIVE,new ArrayList<>(),new ArrayList<>()));
 
		 userService.saveRole(new Role_Model("ROLE_USER"));
		 userService.saveRole(new Role_Model("ROLE_ADMIN"));
		 userService.saveRole(new Role_Model("ROLE_SUPER_ADMIN"));
		 
		 userService.addRoleToUser("case", "ROLE_USER");
		 userService.addRoleToUser("case", "ROLE_SUPER_ADMIN");
		 userService.addRoleToUser("chuck", "ROLE_ADMIN");
		 userService.addRoleToUser("chuck", "ROLE_USER");
		 userService.addRoleToUser("morgan", "ROLE_USER");
		 userService.addRoleToUser("thibaut", "ROLE_USER");
		 
		    userService.addContactToUser("case",new Contact("tsague", "yves thibaut", Sexe.FEMME, "tsague@gmail.com", "343343", "toto.png"));
			userService.addContactToUser("case",new Contact("tsague", "yves thibaut", Sexe.FEMME, "tsague@gmail.com", "343343", "toto.png"));
			userService.addContactToUser("chuck",new Contact("tsague", "yves thibaut", Sexe.FEMME, "tsague@gmail.com", "343343", "toto.png"));

		
	}

}
