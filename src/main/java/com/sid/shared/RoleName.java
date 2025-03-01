package com.sid.shared;

public enum RoleName {
    USER("USER"),
    SUPER_ADMIN("SUPER_ADMIN"),
    ADMIN("ADMIN");

    private final String roleName;

    RoleName(String roleName) {
        this.roleName = roleName;
    }

    // Méthode pour obtenir le nom du rôle
    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return this.roleName;
    }
}
