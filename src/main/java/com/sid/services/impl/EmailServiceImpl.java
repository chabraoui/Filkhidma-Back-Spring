package com.sid.services.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sid.services.EmailService;
import com.sid.shared.dto.AnnonceDto;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendEmailToUser(AnnonceDto annonce) throws MessagingException {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			String subject = "Votre annonce a été approuvée 🎉";
			String body = "<h2>Félicitations !</h2>" + "<p>Votre annonce <strong>'" + annonce.getName()
					+ "'</strong> a été approuvée.</p>" + "<p><a href='http://localhost:4200/annonces/"
					+ annonce.getAnnonceId() + "'>Voir l'annonce</a></p>" + "<br><p>Merci d'utiliser Filkhidma !</p>";

			helper.setTo(annonce.getUser().getEmail());
			helper.setSubject(subject);
			helper.setText(body, true);
			helper.setFrom("no-reply@Filkhidma.ma");

			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
