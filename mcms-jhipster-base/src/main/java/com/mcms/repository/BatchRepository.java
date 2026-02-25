package com.mcms.repository;

import com.mcms.domain.Batch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Batch entity.
 */
@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    default Optional<Batch> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Batch> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Batch> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select batch from Batch batch left join fetch batch.strain left join fetch batch.recipe",
        countQuery = "select count(batch) from Batch batch"
    )
    Page<Batch> findAllWithToOneRelationships(Pageable pageable);

    @Query("select batch from Batch batch left join fetch batch.strain left join fetch batch.recipe")
    List<Batch> findAllWithToOneRelationships();

    @Query("select batch from Batch batch left join fetch batch.strain left join fetch batch.recipe where batch.id =:id")
    Optional<Batch> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "select distinct batch from Batch batch " +
        "left join fetch batch.strain " +
        "left join fetch batch.recipe " +
        "where (:phase is null or batch.currentPhase = :phase) " +
        "and (:strainId is null or batch.strain.id = :strainId) " +
        "and (:isActive is null or batch.isActive = :isActive) " +
        "and (:startDateFrom is null or batch.startDate >= :startDateFrom) " +
        "and (:startDateTo is null or batch.startDate <= :startDateTo)",
        countQuery = "select count(distinct batch) from Batch batch " +
        "where (:phase is null or batch.currentPhase = :phase) " +
        "and (:strainId is null or batch.strain.id = :strainId) " +
        "and (:isActive is null or batch.isActive = :isActive) " +
        "and (:startDateFrom is null or batch.startDate >= :startDateFrom) " +
        "and (:startDateTo is null or batch.startDate <= :startDateTo)"
    )
    Page<Batch> findAllWithFilters(
        @Param("phase") String phase,
        @Param("strainId") Long strainId,
        @Param("isActive") Boolean isActive,
        @Param("startDateFrom") java.time.LocalDate startDateFrom,
        @Param("startDateTo") java.time.LocalDate startDateTo,
        Pageable pageable
    );
}
