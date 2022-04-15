package com.jwt.service;

import java.nio.channels.IllegalSelectorException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jwt.email.EmailSender;
import com.jwt.entities.Contact;
import com.jwt.entities.Role_Model;
import com.jwt.entities.Sexe;
import com.jwt.entities.Status_value;
import com.jwt.entities.UserModel;
import com.jwt.entities.*;
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
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private confirmationTokenService confirmationTokenService;
	
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
		UserModel users = userRepo.findById(id).orElseThrow(
			       ()->new IllegalStateException("contact not found"));
	
	if(user.getName()!="" && 
			!Objects.equals(users.getName(), user.getName())) {
		users.setName(user.getName());
		
	}
	if(user.getUsername()!="" && 
			!Objects.equals(users.getUsername(), user.getUsername())) {
		users.setUsername(user.getUsername());
		
	}
	if(user.getEmail()!="" && 
			!Objects.equals(users.getEmail(), user.getEmail())) {
		users.setEmail(user.getEmail());
		
	}
	
	if(user.getPhotos()!="" && 
			!Objects.equals(users.getPhotos(), user.getPhotos())) {
		users.setPhotos(user.getPhotos());
		
	}
	if(user.getSexe()!=null && 
			!Objects.equals(users.getSexe(), user.getSexe())) {
		users.setSexe(user.getSexe());
		
	}
	if(user.getPassWord()!="" && 
			!Objects.equals(users.getPassWord(), user.getPassWord())) {
		users.setPassWord(user.getPassWord());
		
	}
	return users;
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
	@Transactional
	public UserModel saveUser(UserModel user) {
		Optional<UserModel> userByEmail = userRepo.findByEmail(user.getEmail());
		  if(userByEmail.isPresent()) {
			  throw new IllegalStateException(" Email taken !!!");
		  }
			user.setPassWord(passWordEncoder.encode(user.getPassWord()));
     
		  userRepo.save(user);
		  user.setStatus(Status_value.ACTIVE);
		  addRoleToUser(user.getUsername(), "ROLE_USER");
	String token = UUID.randomUUID().toString();
			
			ConfirmationToken  confirmationToken = new ConfirmationToken(
					token,
					LocalDateTime.now(),
					LocalDateTime.now().plusMinutes(15),
					user
					);
		
			
			
			confirmationTokenService.saveConfirmationToken(
					confirmationToken);
			  String link = "http://localhost:9090/api/confirm?token=" + token;
		        emailSender.send(
		                user.getEmail(),
		                buildEmail(user.getName(), link));

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
		
		UserModel user = userRepo.connectUser(username);
		if(user==null || user.getStatus().equals(Status_value.DESACTIVATE)) {
			throw new UsernameNotFoundException("user not found in data base");
		}
		boolean enabled = !user.isEnabled();
		Collection<SimpleGrantedAuthority> autorities = new ArrayList<>();
		user.getRole().forEach(role ->{autorities.add(new SimpleGrantedAuthority(role.getName()));});
//		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassWord(), autorities);
		UserDetails user1 = User.withUsername(user.getUsername())
				.password(user.getPassWord()).authorities(autorities).disabled(enabled).build();
		return user1;
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


	@Override
	public UserModel findUserById(Long id) {
		Optional<UserModel> user =  userRepo.findById(id);
		if(!user.isPresent()) {
			throw new IllegalStateException(
					"Usser with Id "+ id + "does not Exists !!!" );
		}
		return userRepo.findById(id).get();
	}
	@Override
	@Transactional
	public String confirm(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())){
//        	System.out.println(confirmationToken.getUserModel().getId());
//        	deleteUser(confirmationToken.getUserModel().getId());
        	return "sorry! the the token expired"; 
//            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        enableUserModel(
                confirmationToken.getUserModel().getEmail());
        return "<div>"
        		+ " confirmed <button>ok</button></div>";
    }
	 public int enableUserModel(String email) {
	        return userRepo.enableUserModel(email);
	    }

		@Override
		public UserModel seachByEmail(String email) {
		UserModel user =	userRepo.findByEmail(email).orElseThrow(()-> new IllegalStateException("users not found"));
			return user;
		}

	  private String buildEmail(String name, String link) {
	        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
	                "\n" +
	                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
	                "\n" +
	                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
	                "    <tbody><tr>\n" +
	                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
	                "        \n" +
	                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
	                "          <tbody><tr>\n" +
	                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
	                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
	                "                  <tbody><tr>\n" +
	                "                    <td style=\"padding-left:10px\">\n" +
	                "                  \n" +
	                "                    </td>\n" +
	                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
	                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
	                "                    </td>\n" +
	                "                  </tr>\n" +
	                "                </tbody></table>\n" +
	                "              </a>\n" +
	                "            </td>\n" +
	                "          </tr>\n" +
	                "        </tbody></table>\n" +
	                "        \n" +
	                "      </td>\n" +
	                "    </tr>\n" +
	                "  </tbody></table>\n" +
	                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
	                "    <tbody><tr>\n" +
	                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
	                "      <td>\n" +
	                "        \n" +
	                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
	                "                  <tbody><tr>\n" +
	                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
	                "                  </tr>\n" +
	                "                </tbody></table>\n" +
	                "        \n" +
	                "      </td>\n" +
	                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
	                "    </tr>\n" +
	                "  </tbody></table>\n" +
	                "\n" +
	                "\n" +
	                "\n" +
	                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
	                "    <tbody><tr>\n" +
	                "      <td height=\"30\"><br></td>\n" +
	                "    </tr>\n" +
	                "    <tr>\n" +
	                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
	                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
	                "        \n" +
	                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
	                "        \n" +
	                "      </td>\n" +
	                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
	                "    </tr>\n" +
	                "    <tr>\n" +
	                "      <td height=\"30\"><br></td>\n" +
	                "    </tr>\n" +
	                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
	                "\n" +
	                "</div></div>";
	    }


	@Override
	public boolean deleteDesabledUser(String username) {
		
		UserModel user = userRepo.findByUsername(username);
		if(user.isEnabled()){
			return false;
		}
		userRepo.deleteById(user.getId());
		return true;
	}



}
