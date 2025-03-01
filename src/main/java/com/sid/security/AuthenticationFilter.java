package com.sid.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sid.requests.UserLoginRequest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.sid.SpringApplicationContext;
import com.sid.services.UserService;
import com.sid.shared.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

//filtre d'authentification
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
//attribut pour gerer l'authentification
	private final AuthenticationManager authenticationManager;
	 
//init de l'attribut via constructor
	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	
	
	
	//pour qu'on puisse autentifié (redifinition de la methode)
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			
			//RECEPTION DU DATA(email,password)
			UserLoginRequest creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequest.class);

			//passer data vers la methode authenticate
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
		
	
	//si authentifié avec succsée
	  @Override
	    protected void successfulAuthentication(HttpServletRequest req,
	                                            HttpServletResponse res,
	                                            FilterChain chain,
	                                            Authentication auth) throws IOException, ServletException {
	      
		  //recuperer username=email dans notre cas
	        String userName = ((User) auth.getPrincipal()).getUsername(); 
	        
	        //pour recuperer userId pour l'envoyer avec le token 
            UserService userService = (UserService)SpringApplicationContext.getBean("userServiceImpl");
	        
	        UserDto userDto = userService.getUser(userName);
	       
	        
	        // Extraire le rôle en tant que String (par exemple : "ADMIN", "USER", etc.)
	       List<String> roleName = userDto.getRole().stream()
	                                  .map(role -> role.getRoleName().name()) // Convertir RoleName en String
	                                  .collect(Collectors.toList()); 
	                            //    .findFirst() // Prendre le premier rôle (tu peux adapter selon tes besoins)
	                            //     .orElse("USER"); // Valeur par défaut si aucun rôle n'est trouvé
	        
	        //generer un token
	        String token = Jwts.builder()
	                .setSubject(userName)
	                .claim("id", userDto.getUserId())
	                .claim("name", userDto.getFirstName() + " " + userDto.getLastName())
	                .claim("scope", roleName)
	                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
	                .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET )
	                .compact();
	        
	       
	        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
	        res.addHeader("user_id", userDto.getUserId());
	     // Transformer la liste en String (exemple : "ADMIN,USER")
	        res.addHeader("scope", String.join(",", roleName));	        
	        res.getWriter().write("{\"token\": \"" + token + "\", \"id\": \""+ userDto.getUserId() + "\",\"scope\": \""+ roleName + "\"}");

	    }  
	

}
