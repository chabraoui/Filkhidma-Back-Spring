package com.sid.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.sid.shared.CategoryName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Enumerated(EnumType.STRING)
	private CategoryName categoryName;

	@OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<AnnonceEntity> annonces = new ArrayList<>();

}
