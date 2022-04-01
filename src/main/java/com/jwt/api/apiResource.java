package com.jwt.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.entities.Contact;
import com.jwt.entities.Role_Model;
import com.jwt.entities.UserModel;
import com.jwt.repo.ContactRepo;
import com.jwt.service.ContactService;
import com.jwt.service.UserService;

import net.bytebuddy.implementation.bytecode.constant.DefaultValue;


@RestController
@RequestMapping("api/")
public class apiResource {

	@Autowired
	private UserService  userService;
	@Autowired
	private ContactService contactService;
	@Autowired
	private ServletContext context;

	
	/*======================================================================
	 *------->>>>>>>| CRUD SUR LA TABLE USERMODEL |<<<<<<<<<<---------------
	 *======================================================================
	 */
//	@GetMapping("/users")
//	public ResponseEntity< Page<UserModel>> getAllUsers(
//			                      @RequestParam Integer page){
//		return ResponseEntity.ok().body(userService.getAllUsers(page));
//	}
	@GetMapping("/users")
	public ResponseEntity< Page<UserModel>> getAllUsers(
			                      @RequestParam(name="mc", defaultValue="") String mc,
			                      @RequestParam(name="page" ,defaultValue="0") int page,
			                      @RequestParam(name="size",defaultValue="3") int size){
		return ResponseEntity.ok().body(userService.getAllUsers(mc,page,size));
	}
	
	@PostMapping("/users/save")
	public UserModel  saveUser(@RequestParam("user") String user,
			                                   @RequestParam(name = "file", required = false) MultipartFile file)  throws JsonParseException,JsonMappingException,Exception {
	   
		UserModel users = new ObjectMapper().readValue(user,UserModel.class);
		if(!file.isEmpty()) {
			addUserImage(file);
			String fileName = file.getOriginalFilename();
			String newFileName = FilenameUtils.getBaseName(fileName)+"."+FilenameUtils.getExtension(fileName);
			users.setPhotos(newFileName);
			return userService.saveUser(users);
		}else {
			return userService.saveUser(users);
		}
	}
	@GetMapping("/images")
	public byte[] getImages(@RequestParam("name") String name) throws Exception{
		UserModel users = userService.getUser(name);
    	String filePath = "C:\\Users\\stague\\Documents\\springBoot\\api_contact-1\\src\\main\\userImages\\";

        return Files.readAllBytes(Paths.get(filePath+users.getPhotos()));
	}
	@GetMapping("/users/{id}")
	public ResponseEntity<Optional<UserModel>>  findUser(@PathVariable("id") Long id) {
		return ResponseEntity.ok().body(userService.findUser(id));
	}
	@GetMapping("/users/one")
	public ResponseEntity<UserModel> findUserByUsername(@RequestParam("name") String name) {
		return ResponseEntity.ok().body(userService.getUser(name));
	}
	@GetMapping("/users/contact")
	public ResponseEntity<List<Contact>>  getContactForSingleUser(@RequestParam(required = true) String name) {
		return ResponseEntity.ok().body(userService.contactForSingleUser(name));
	}
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Boolean>  deleteUser(@PathVariable("id") Long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok().build();
	}
	@DeleteMapping("/users/desactivate/{id}")
	public ResponseEntity<Boolean>  desactivateUser(@PathVariable("id") Long id) {
		userService.desactivateUser(id);
		return ResponseEntity.ok().build();
	}
	@DeleteMapping("/users/activate/{id}")
	public ResponseEntity<Boolean>  activateUser(@PathVariable("id") Long id) {
		userService.activateUser(id);
		return ResponseEntity.ok().build();
	}
	@PutMapping("/users/update/{id}")
	public ResponseEntity<UserModel>  UpdateUser(@RequestBody UserModel user,@PathVariable Long id) {
		  URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/update").toUriString());
		return ResponseEntity.created(uri).body(userService.updateUser(user, id)) ;
	}
	
	@PostMapping("/role/addroleuser")
	public ResponseEntity<?>  addRoleToUser(@RequestBody RoleToUserForm form) {
		userService.addRoleToUser(form.getUsername(),form.getRoleName());
		return ResponseEntity.ok().body("Role added succesfully !");
	}
	@PostMapping("/role/removeroleuser")
	public ResponseEntity<?>  removeRoleToUser(@RequestBody RoleToUserForm form) {
		userService.removeRoleToUser(form.getUsername(),form.getRoleName());
		return ResponseEntity.ok().body("Role removed succesfully !");
	}
	/*======================================================================
	 *-------->>>>>>>>>| Quelque fonctions importante |<<<<<<<<<<--------------
	 *======================================================================
	 */
    private void	addUserImage(MultipartFile file){
    	String path = "C:\\Users\\stague\\Documents\\springBoot\\api_contact-1\\src\\main\\userImages";
 	    String fileName  = file.getOriginalFilename();
		String newFileName = FilenameUtils.getBaseName(fileName)+"."+FilenameUtils.getExtension(fileName);
		File serverFile = new File(path+File.separator+newFileName);
		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			System.out.println("faile to upload");
		}
		
	}
    private void	addContactImage(MultipartFile file){
    	String path = "    C:\\Users\\stague\\Documents\\springBoot\\api_contact-1\\src\\main\\contactImage";
 	    String fileName  = file.getOriginalFilename();
		String newFileName = FilenameUtils.getBaseName(fileName)+"."+FilenameUtils.getExtension(fileName);
		File serverFile = new File(path+File.separator+newFileName);
		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			System.out.println("faile to upload");
		}
		
	}

	
	/*======================================================================
	 *-------->>>>>>>>>| CRUD SUR LA TABLE CONTACT |<<<<<<<<<<--------------
	 *======================================================================
	 */
	
	@GetMapping("/contact")
	public ResponseEntity<Page<Contact>> getAllContact(@RequestParam(name="mc", defaultValue="" ) String mc,
			                                           @RequestParam(name="page",defaultValue="0") int page,
			                                           @RequestParam(name="size",defaultValue="3") int size){
		return ResponseEntity.ok().body(contactService.getAllContact(mc,page,size));
	}
	@GetMapping("/contact/{id}")
	public ResponseEntity<Optional<Contact>> getContact(@PathVariable("id") Long id,
			                                              @RequestParam(required = true) String name){
		return ResponseEntity.ok().body(contactService.findContact(id));
	}
	
	@PostMapping("/contact/save")
	public Contact saveContact(@RequestParam("contact") String contact,
			                                     @RequestParam("file") MultipartFile file,
			                                     @RequestParam(required = true) String username) throws JsonParseException,JsonMappingException,Exception {

		Contact contacts = new ObjectMapper().readValue(contact,Contact.class);
		if(!file.isEmpty()) {
			addContactImage(file);
			String fileName = file.getOriginalFilename();
			String newFileName = FilenameUtils.getBaseName(fileName)+"."+FilenameUtils.getExtension(fileName);
			contacts.setPhoto(newFileName);
			return contactService.saveContact(username, contacts);
		}else {
			return contactService.saveContact(username, contacts);
		}
	}
	@DeleteMapping("/contact/{id}")
	public ResponseEntity<?>  deleteContact(@PathVariable("id") Long id) {
			                                   
		
		return ResponseEntity.ok().body(contactService.deleteContact(id));
	}
	@PutMapping("/contact/{id}")
	public ResponseEntity<?>  updateContact(@RequestBody Contact contact,
			                                    @PathVariable("id") Long id  ) {
			                                 
		
		return ResponseEntity.ok().body(contactService.updateContact(id, contact));
	}
	
	/*======================================================================
	 *-------->>>>>>>>>>>| CREATE REFRESH TOKEN  |<<<<<<<<<<<<<<<-----------
	 *======================================================================
	 */
	 @GetMapping("/token/refresh")
	  public void refreshToken(HttpServletRequest request,HttpServletResponse response) throws StreamWriteException, DatabindException, IOException{
			String autorizationHeader = request.getHeader("Authorization");

			
			if(autorizationHeader != null && autorizationHeader.startsWith("Bearer ")) {
				try {
					
					String refresh_token = autorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJwt = verifier.verify(refresh_token);
					String userName = decodedJwt.getSubject();
					UserModel user = userService.getUser(userName);
					String access_token = JWT.create()
			                 .withSubject(user.getUsername())
			                 .withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
			                 .withIssuer(request.getRequestURL().toString())
			                 .withClaim("role",user.getRole().stream().map(Role_Model::getName).collect(Collectors.toList()))
			                 .sign(algorithm);
						//response.setHeader("access Token ",access_token);
						//response.setHeader("refresh Token ",refrech_token);
						Map<String,String> tokens = new HashMap<>();
						tokens.put("access Token ",access_token);
						tokens.put("refresh Token ",refresh_token);
						response.setContentType("application/json");
						new ObjectMapper().writeValue(response.getOutputStream(), tokens);



				}catch (Exception e) {
					response.setHeader("erreur", e.getMessage());
	                response.setStatus(403,"forbidden"); 
					 Map<String,String> erreur = new HashMap<>();
					 erreur.put("eurreur_message ",e.getMessage());
				     response.setContentType("application/json");
				     new ObjectMapper().writeValue(response.getOutputStream(), erreur);		
	  
	  } 
			}else {
				
	            throw new  RuntimeException("Refresh token is missing");
	}
	  
	
	 }	
	
}

class RoleToUserForm {
	private String username;
	private String roleName;
	public RoleToUserForm() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RoleToUserForm(String username, String passWord) {
		super();
		this.username = username;
		this.roleName = passWord;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String passWord) {
		this.roleName = passWord;
	}
}
