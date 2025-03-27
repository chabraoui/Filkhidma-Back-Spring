package com.sid.services;

import javax.mail.MessagingException;

import com.sid.shared.dto.AnnonceDto;

public interface EmailService {
	void sendEmailToUser(AnnonceDto annonce) throws MessagingException;
}
