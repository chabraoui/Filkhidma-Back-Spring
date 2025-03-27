package com.sid.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sid.shared.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "annonces")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AnnonceEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1592578953414423786L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String annonceId;
	
	private String name;
	
	private String description;
	
	private String prix;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private Date dateAnnonce;

	@Column(nullable = true)
	private String images;
	
	@JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "category_id")
	private CategoryEntity category;
	
	@JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "ville_id")
	private VilleEntity ville;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
    @PreRemove
    public void removeImageBeforeDeletion() {
        if (this.images != null && !this.images.isEmpty()) {
            Utils.deleteImage(this.images);
        }
    }

	
}
