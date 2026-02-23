package com.mcms.repository;

import com.mcms.domain.MandatoryFieldCheck;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MandatoryFieldCheck entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MandatoryFieldCheckRepository extends JpaRepository<MandatoryFieldCheck, Long> {}
