package com.mcms.repository;

import com.mcms.domain.ContaminationEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ContaminationEvent entity.
 */
@Repository
public interface ContaminationEventRepository extends JpaRepository<ContaminationEvent, Long> {
    default Optional<ContaminationEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ContaminationEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ContaminationEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select contaminationEvent from ContaminationEvent contaminationEvent left join fetch contaminationEvent.batch left join fetch contaminationEvent.room",
        countQuery = "select count(contaminationEvent) from ContaminationEvent contaminationEvent"
    )
    Page<ContaminationEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select contaminationEvent from ContaminationEvent contaminationEvent left join fetch contaminationEvent.batch left join fetch contaminationEvent.room"
    )
    List<ContaminationEvent> findAllWithToOneRelationships();

    @Query(
        "select contaminationEvent from ContaminationEvent contaminationEvent left join fetch contaminationEvent.batch left join fetch contaminationEvent.room where contaminationEvent.id =:id"
    )
    Optional<ContaminationEvent> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "select contaminationEvent from ContaminationEvent contaminationEvent left join fetch contaminationEvent.room where contaminationEvent.batch.id = :batchId order by contaminationEvent.detectedDate desc"
    )
    List<ContaminationEvent> findByBatchIdOrderByDetectedDateDesc(@Param("batchId") Long batchId);
}
