package com.sid.services;

import com.sid.shared.dto.AnnonceDto;

public interface NotificationService {
	void notifySuperAdmins(AnnonceDto annonce);
	// void notifyUser(String userId, AnnonceDto annonce);
}
