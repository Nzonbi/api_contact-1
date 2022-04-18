package com.jwt.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;



	@Entity
	public class UserModel {
		@Id @SequenceGenerator(allocationSize = 1, name = "id")
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;
		private String name;
		@Column(nullable = false,
				unique = true)
		private String username;
		@Column(nullable = false,
				unique = true)
		private String email;
		
		private Sexe sexe;
		private String passWord;
		private String photo;
		private boolean enabled=false;
		
		private Status_value status;
		 @OneToMany( cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
		 @JoinColumn(name = "users_id")
		private List<Contact> contact = new ArrayList<>() ;
		 
		 
		public List<Contact> getContact() {
			return contact;
		}

		public void setContact(List<Contact> contact) {
			this.contact = contact;
		}

		@ManyToMany
		private Collection<Role_Model> role = new ArrayList<>();

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public Sexe getSexe() {
			return sexe;
		}

		public void setSexe(Sexe sexe) {
			this.sexe = sexe;
		}

		public String getPassWord() {
			return passWord;
		}

		public void setPassWord(String passWord) {
			this.passWord = passWord;
		}

		public Status_value getStatus() {
			return status;
		}

		public void setStatus(Status_value status) {
			this.status = status;
		}

		public Collection<Role_Model> getRole() {
			return role;
		}

		public void setRole(Collection<Role_Model> role) {
			this.role = role;
		}

		public UserModel(String name, String username,String email ,Sexe sexe, String passWord,String photo ,Status_value status, List<Contact> contact,
				Collection<Role_Model> role) {
			super();
			this.name = name;
			this.username = username;
			this.sexe = sexe;
			this.email = email;
			this.passWord = passWord;
			this.photo = photo;
			this.status = status;
			this.contact = contact;
			this.role = role;
		}

		public UserModel() {
			super();
			// TODO Auto-generated constructor stub
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhotos() {
			return photo;
		}

		public void setPhotos(String photos) {
			this.photo = photos;
		}
		public boolean isEnabled() {
			return enabled;
		}

		
		
		
		
}
