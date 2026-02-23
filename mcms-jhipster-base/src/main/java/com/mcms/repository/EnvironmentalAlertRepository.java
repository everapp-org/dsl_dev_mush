package com.mcms.repository;

import com.mcms.domain.EnvironmentalAlert;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EnvironmentalAlert entity.
 */
@Repository
public interface EnvironmentalAlertRepository extends JpaRepository<EnvironmentalAlert, Long> {
    default Optional<EnvironmentalAlert> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EnvironmentalAlert> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EnvironmentalAlert> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select environmentalAlert from EnvironmentalAlert environmentalAlert left join fetch environmentalAlert.room left join fetch environmentalAlert.sensor left join fetch environmentalAlert.batch",
        countQuery = "select count(environmentalAlert) from EnvironmentalAlert environmentalAlert"
    )
    Page<EnvironmentalAlert> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select environmentalAlert from EnvironmentalAlert environmentalAlert left join fetch environmentalAlert.room left join fetch environmentalAlert.sensor left join fetch environmentalAlert.batch"
    )
    List<EnvironmentalAlert> findAllWithToOneRelationships();

    @Query(
        "select environmentalAlert from EnvironmentalAlert environmentalAlert left join fetch environmentalAlert.room left join fetch environmentalAlert.sensor left join fetch environmentalAlert.batch where environmentalAlert.id =:id"
    )
    Optional<EnvironmentalAlert> findOneWithToOneRelationships(@Param("id") Long id);
}
