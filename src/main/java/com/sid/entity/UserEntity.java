package com.sid.entity;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -132096831648625772L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private String tel;
	@Column(nullable = true)
	private String userImage;
	private String encryptePassword;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "role_users", joinColumns = { @JoinColumn(name = "users_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<RoleEntity> role = new ArrayList<>();

	@JsonManagedReference
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<AnnonceEntity> annonces = new ArrayList<>();

}
