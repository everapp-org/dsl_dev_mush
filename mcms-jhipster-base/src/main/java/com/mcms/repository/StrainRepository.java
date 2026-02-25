package com.mcms.repository;

import com.mcms.domain.Strain;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Strain entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StrainRepository extends JpaRepository<Strain, Long> {
    /**
     * Search strains by name, species, or variety (case-insensitive).
     */
    @Query(
        "SELECT s FROM Strain s WHERE " +
        "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(s.species) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(s.variety) LIKE LOWER(CONCAT('%', :searchTerm, '%'))"
    )
    List<Strain> searchStrains(@Param("searchTerm") String searchTerm);
}
