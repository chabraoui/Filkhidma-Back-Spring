package com.sid.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sid.repository.UserRepository;
import com.sid.services.UserService;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;

	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,
			UserRepository userRepository) {
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests(autorisation -> autorisation
				// .antMatchers("/ws/**").permitAll()
				.antMatchers("/ws/**", "/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**")
				.permitAll().antMatchers(HttpMethod.GET, "/api/**").permitAll()
				.antMatchers(HttpMethod.GET, "/annonces/**", SecurityConstants.SIGN_UP_URL + "/user/**").permitAll()
				.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL,
						SecurityConstants.SIGN_UP_URL + "/forgetpassword")
				.permitAll()
				// .antMatchers(HttpMethod.POST, "/users/**").permitAll()
				.anyRequest().authenticated()).addFilter(getAuthenticationFilter())
				.addFilter(new AuthorizationFilter(authenticationManager()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	}

	protected AuthenticationFilter getAuthenticationFilter() throws Exception {
		final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager(), userRepository);
		filter.setFilterProcessesUrl("/users/login");
		return filter;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

}
