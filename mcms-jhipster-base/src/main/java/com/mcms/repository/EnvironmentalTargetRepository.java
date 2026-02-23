package com.mcms.repository;

import com.mcms.domain.EnvironmentalTarget;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EnvironmentalTarget entity.
 */
@Repository
public interface EnvironmentalTargetRepository extends JpaRepository<EnvironmentalTarget, Long> {
    default Optional<EnvironmentalTarget> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EnvironmentalTarget> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EnvironmentalTarget> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select environmentalTarget from EnvironmentalTarget environmentalTarget left join fetch environmentalTarget.room",
        countQuery = "select count(environmentalTarget) from EnvironmentalTarget environmentalTarget"
    )
    Page<EnvironmentalTarget> findAllWithToOneRelationships(Pageable pageable);

    @Query("select environmentalTarget from EnvironmentalTarget environmentalTarget left join fetch environmentalTarget.room")
    List<EnvironmentalTarget> findAllWithToOneRelationships();

    @Query(
        "select environmentalTarget from EnvironmentalTarget environmentalTarget left join fetch environmentalTarget.room where environmentalTarget.id =:id"
    )
    Optional<EnvironmentalTarget> findOneWithToOneRelationships(@Param("id") Long id);
}
