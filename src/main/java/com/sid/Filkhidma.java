package com.sid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sid.entity.CategoryEntity;
import com.sid.entity.RoleEntity;
import com.sid.entity.VilleEntity;
import com.sid.repository.CategoryRepository;
import com.sid.repository.RoleRepository;
import com.sid.repository.VilleRepository;
import com.sid.shared.CategoryName;
import com.sid.shared.RoleName;
import com.sid.shared.VilleName;

@SpringBootApplication
public class Filkhidma extends SpringBootServletInitializer {
	
	@Autowired
	private  RoleRepository roleRepository;
	
	@Autowired
	private  VilleRepository villeRepository;
	
	@Autowired
	private  CategoryRepository categoryRepository;
	
	@Override
	protected  SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Filkhidma.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Filkhidma.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
	
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
	
	
	//@Bean
    public CommandLineRunner insertRoles() {
        return args -> {
            // Vérifier si les rôles existent déjà dans la base de données
            if (roleRepository.count() == 0) {
                for (RoleName roleName : RoleName.values()) {
                    RoleEntity roleEntity = new RoleEntity();
                    roleEntity.setRoleName(roleName);  // Associer le rôle avec l'énumération
                    roleRepository.save(roleEntity);   // Sauvegarder dans la base de données
                }
                System.out.println("Les rôles ont été initialisés dans la base de données.");
            } else {
                System.out.println("Les rôles existent déjà dans la base de données.");
            }
        };
    }
    
    //@Bean
    public CommandLineRunner insertCategories() {
        return args -> {
            // Vérifier si les category existent déjà dans la base de données
            if (categoryRepository.count() == 0) {
                for (CategoryName categoryName : CategoryName.values()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setCategoryName(categoryName);  // Associer le rôle avec l'énumération
                    categoryRepository.save(categoryEntity);   // Sauvegarder dans la base de données
                }
                System.out.println("Les category ont été initialisés dans la base de données.");
            } else {
                System.out.println("Les category existent déjà dans la base de données.");
            }
        };
    }
    
   //@Bean
    public CommandLineRunner insertVilles() {
        return args -> {
            // Vérifier si les villes existent déjà dans la base de données
            if (villeRepository.count() == 0) {
                for (VilleName villeName : VilleName.values()) {
                	VilleEntity villeEntity = new VilleEntity();
                	villeEntity.setVilleName(villeName);  // Associer le rôle avec l'énumération
                	villeRepository.save(villeEntity);   // Sauvegarder dans la base de données
                }
                System.out.println("Les villes ont été initialisés dans la base de données.");
            } else {
                System.out.println("Les villes existent déjà dans la base de données.");
            }
        };
    }
	
	

}
