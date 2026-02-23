package com.mcms.repository;

import com.mcms.domain.PhaseExecution;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PhaseExecution entity.
 */
@Repository
public interface PhaseExecutionRepository extends JpaRepository<PhaseExecution, Long> {
    default Optional<PhaseExecution> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PhaseExecution> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PhaseExecution> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select phaseExecution from PhaseExecution phaseExecution left join fetch phaseExecution.batch left join fetch phaseExecution.room",
        countQuery = "select count(phaseExecution) from PhaseExecution phaseExecution"
    )
    Page<PhaseExecution> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select phaseExecution from PhaseExecution phaseExecution left join fetch phaseExecution.batch left join fetch phaseExecution.room"
    )
    List<PhaseExecution> findAllWithToOneRelationships();

    @Query(
        "select phaseExecution from PhaseExecution phaseExecution left join fetch phaseExecution.batch left join fetch phaseExecution.room where phaseExecution.id =:id"
    )
    Optional<PhaseExecution> findOneWithToOneRelationships(@Param("id") Long id);
}
