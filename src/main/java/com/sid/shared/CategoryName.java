package com.sid.shared;

public enum CategoryName {
    PLOMBIER("PLOMBIER"),
    ELECTRICIEN("ELECTRICIEN"),
    TAPISSIER("TAPISSIER"),
    MACON("MACON"),
    CHARPENTIER("CHARPENTIER"),
    PEINTRE("PEINTRE"),
    JARDINIER("JARDINIER"),
    MENUISIER("MENUISIER");
	
    private final String categoryName;

    CategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return getName();
    }

}


