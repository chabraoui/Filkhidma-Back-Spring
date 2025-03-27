package com.sid.exceptions;

import org.springframework.http.HttpStatus;

public class AnnonceException extends RuntimeException {
    private final HttpStatus status;
	
	private static final long serialVersionUID = 847500838613349753L;
	
    public AnnonceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() {
        return status;
    }
}
