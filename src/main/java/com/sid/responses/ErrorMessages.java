package com.sid.responses;

public enum ErrorMessages {
	
	MISSING_REQUIRED_FIELD("Missing required field."),
    RECORD_ALREADY_EXISTS("Record already exists."),
    BAD_LOGIN("email or password not valid."),
    NO_RECORD_FOUND("Record with provided id is not found."),
    NO_AUTH_FOUND("user auth not found"),
    NO_CITY_FOUND("city  not found"),
    NO_CAT_FOUND("cat not found"),
    NO_SCOPE_FOUND("Role invalide ! Choisir parmi : ADMIN, SUPER_ADMIN, USER\""),
    NO_AUTORIZ_REQUEST("vous n'etes pas autorisé a executer cette requete"),
    NO_USER_WITH_SCOPE("Aucun utilisateur trouvé avec le rôle :"),
	CAT_EMPTY("pas d'annonce dans cette category"),
	CITY_EMPTY("pas d'annonce dans cette ville"),
	CAT_Not_FOUND("Category not exist "),
	SAVE_FAILED("Failed to save image"),
	SEARCH_EMPTY("Annonce recherchée introuvable"),
	CITY_Not_FOUND("ville not exist");

	
	 private String errorMessage;

	private ErrorMessages(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	 
	 
}
