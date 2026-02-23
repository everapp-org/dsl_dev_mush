package com.mcms.repository;

import com.mcms.domain.Strain;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Strain entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StrainRepository extends JpaRepository<Strain, Long> {}
