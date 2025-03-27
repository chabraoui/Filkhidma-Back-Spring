package com.sid.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sid.services.NotificationService;
import com.sid.shared.dto.AnnonceDto;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Override
	public void notifySuperAdmins(AnnonceDto annonce) {
		try {
			messagingTemplate.convertAndSend("/topic/pendingAnnonces", annonce);
		} catch (Exception e) {
			System.err.println("Erreur lors de l'envoi de la notification : " + e.getMessage());
		}
	}

//	@Override
//	public void notifyUser(String userId, AnnonceDto annonce) {
//		String notificationMessage = "Hi " + annonce.getUser().getFirstName() + "Votre annonce : " + annonce.getName()
//				+ " est approuvé ";
//		try {
//			messagingTemplate.convertAndSendToUser(userId, "/queue/annonceApproved", notificationMessage);
//		} catch (Exception e) {
//			System.err.println("Erreur lors de l'envoi de la notification : " + e.getMessage());
//		}
//	}

}
