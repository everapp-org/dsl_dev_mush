package com.mcms.repository;

import com.mcms.domain.Material;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Material entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    /**
     * Find a material by its unique code.
     * Used for duplicate detection during CSV import.
     *
     * @param code the material code to search for
     * @return Optional containing the material if found, empty otherwise
     */
    Optional<Material> findOneByCode(String code);
}
