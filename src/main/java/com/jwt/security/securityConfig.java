package com.jwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jwt.filter.CustomAuthenticationFilter;
import com.jwt.filter.CustomAuthorizationFilter;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class securityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bcryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers("/login/**","/api/token/refresh/**","/api/users/save/**","/api/confirm/**").permitAll();
		/*
		 * user Authorization request per Role
		 */ 
		http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/users/**").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_USER");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/users/**").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users/contact/**").hasAnyAuthority("ROLE_USER","ROLE_SUPER_ADMIN","ROLE_ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/images/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/imagesCont/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users/one/**").hasAnyAuthority("ROLE_USER","ROLE_SUPER_ADMIN","ROLE_ADMIN");

		/*
		 * Contact Authorization request per Role
		 */
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/contact/**").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_USER");
		http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/contact/**").hasAnyAuthority("ROLE_USER");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/contact/**").hasAnyAuthority("ROLE_USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/contact/save/**").hasAnyAuthority("ROLE_USER");
		/*
		 * grade grants based on your role in the application
		 */
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/role/removeroleuser/**").hasAnyAuthority("ROLE_SUPER_ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/role/addroleuser/**").hasAnyAuthority("ROLE_SUPER_ADMIN");
		http.authorizeRequests().anyRequest().authenticated();
		http.addFilter(new CustomAuthenticationFilter(AuthenticationanagerBean()));
	    http.addFilterBefore(new CustomAuthorizationFilter(),UsernamePasswordAuthenticationFilter.class);
	}
	@Bean
	public AuthenticationManager AuthenticationanagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	

}
