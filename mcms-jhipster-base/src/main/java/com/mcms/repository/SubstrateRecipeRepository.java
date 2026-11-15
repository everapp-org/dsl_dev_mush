package com.mcms.repository;

import com.mcms.domain.SubstrateRecipe;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SubstrateRecipe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubstrateRecipeRepository extends JpaRepository<SubstrateRecipe, Long> {}
