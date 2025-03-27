package com.sid.shared.dto;


import java.util.List;
import com.sid.shared.CategoryName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {

	
	private long id;
	private CategoryName categoryName;
    @ToString.Exclude
	private List<AnnonceDto> annonces;

}
