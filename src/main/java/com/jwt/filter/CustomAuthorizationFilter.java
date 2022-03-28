package com.jwt.filter;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.entities.UserModel;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-type,Accept,X-Requested-With,x-www-form-urlencoded,Authorization,Bearer");
		if(request.getServletPath().equals("/login") || request.getServletPath().equals("/api/token/refresh")) {
			filterChain.doFilter(request, response);
		}else {
			String autorizationHeader = request.getHeader("Authorization");
			if(autorizationHeader != null && autorizationHeader.startsWith("Bearer ")) {
				try {
					
					String token = autorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJwt = verifier.verify(token);
					String userName = decodedJwt.getSubject();
					String[] roles = decodedJwt.getClaim("role").asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					stream(roles).forEach(role ->{
						authorities.add(new SimpleGrantedAuthority(role));
					});

					UsernamePasswordAuthenticationToken authentificationToken =
							new UsernamePasswordAuthenticationToken(userName,null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authentificationToken);
					filterChain.doFilter(request, response);
				}catch (Exception e) {
					 response.setHeader("erreur", e.getMessage());
					 response.setStatus(403,"forbidden");
					 Map<String,String> erreur = new HashMap<>();
					 erreur.put("error_message ",e.getMessage());
				     response.setContentType("application/json");
				     new ObjectMapper().writeValue(response.getOutputStream(), erreur);
		
				     System.out.println("tototootootot");
				}
					
				}else {
					filterChain.doFilter(request, response);


			}
		}
		
	}

}
