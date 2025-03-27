package com.sid.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sid.shared.CategoryName;
import com.sid.shared.dto.AnnonceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

	private CategoryName categoryName;
	@JsonIgnoreProperties(value = { "user", "category", "ville" })
	private List<AnnonceDto> annonces;

}
