package com.sid.shared;

import org.springframework.data.jpa.domain.Specification;

import com.sid.entity.AnnonceEntity;

import lombok.AllArgsConstructor;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class Search implements Specification<AnnonceEntity> {

    private final Long categoryId;  
    private final Long villeId;
    private final String keyword;



    @Override
    public Predicate toPredicate(Root<AnnonceEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        // Condition de filtre sur "category_id"
        if (categoryId != null) {
            predicates.add(builder.equal(root.get("category").get("id"), categoryId)); // Comparaison avec category.id
        }

        // Condition de filtre sur "ville_id"
        if (villeId != null) {
            predicates.add(builder.equal(root.get("ville").get("id"), villeId)); // Comparaison avec ville.id
        }

        // Condition de filtre sur "keyword"
        if (keyword != null && !keyword.isEmpty()) {
            // On cherche "keyword" dans "name" et "description" avec des conditions "OR"
            Predicate namePredicate = builder.like(root.get("name"), "%" + keyword + "%");
            Predicate descriptionPredicate = builder.like(root.get("description"), "%" + keyword + "%");
            predicates.add(builder.or(namePredicate, descriptionPredicate)); // "OR" entre les deux
        }

        // Retourner une combinaison de toutes les conditions (AND logique entre toutes)
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
