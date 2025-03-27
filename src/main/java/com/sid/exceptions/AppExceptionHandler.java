package com.sid.exceptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.sid.responses.ErrorMessage;

@ControllerAdvice
public class AppExceptionHandler {

	@ExceptionHandler(value = UserException.class)
	public ResponseEntity<Object> HandleUserException(UserException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), ex.getStatus());
	}

	@ExceptionHandler(value = { AnnonceException.class })
	public ResponseEntity<Object> HandleUserException(AnnonceException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), ex.getStatus());
	}

	@ExceptionHandler(value = { CategoryException.class })
	public ResponseEntity<Object> HandleUserException(CategoryException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), ex.getStatus());
	}

	@ExceptionHandler(value = { VilleException.class })
	public ResponseEntity<Object> HandleUserException(VilleException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), ex.getStatus());
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> HandleOtherExceptions(Exception ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@ExceptionHandler(value=MethodArgumentNotValidException.class)
	public ResponseEntity<Object> HandleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) 
	{
		Map<String, String> errors = new HashMap<>();
		 
	    ex.getBindingResult().getFieldErrors().forEach(error -> 
	        errors.put(error.getField(), error.getDefaultMessage()));
		
		return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

}
