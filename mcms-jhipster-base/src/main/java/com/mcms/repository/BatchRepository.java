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
        "where (:search is null or lower(batch.batchCode) like lower(concat('%', :search, '%'))) " +
        "and (:phase is null or batch.currentPhase = :phase) " +
        "and (:strainId is null or batch.strain.id = :strainId) " +
        "and (:isActive is null or batch.isActive = :isActive) " +
        "and (:startDateFrom is null or batch.startDate >= :startDateFrom) " +
        "and (:startDateTo is null or batch.startDate <= :startDateTo) " +
        "and (:numberOfBagsMin is null or batch.numberOfBags >= :numberOfBagsMin) " +
        "and (:numberOfBagsMax is null or batch.numberOfBags <= :numberOfBagsMax)",
        countQuery = "select count(distinct batch) from Batch batch " +
        "where (:search is null or lower(batch.batchCode) like lower(concat('%', :search, '%'))) " +
        "and (:phase is null or batch.currentPhase = :phase) " +
        "and (:strainId is null or batch.strain.id = :strainId) " +
        "and (:isActive is null or batch.isActive = :isActive) " +
        "and (:startDateFrom is null or batch.startDate >= :startDateFrom) " +
        "and (:startDateTo is null or batch.startDate <= :startDateTo) " +
        "and (:numberOfBagsMin is null or batch.numberOfBags >= :numberOfBagsMin) " +
        "and (:numberOfBagsMax is null or batch.numberOfBags <= :numberOfBagsMax)"
    )
    Page<Batch> findAllWithFilters(
        @Param("search") String search,
        @Param("phase") String phase,
        @Param("strainId") Long strainId,
        @Param("isActive") Boolean isActive,
        @Param("startDateFrom") java.time.LocalDate startDateFrom,
        @Param("startDateTo") java.time.LocalDate startDateTo,
        @Param("numberOfBagsMin") Integer numberOfBagsMin,
        @Param("numberOfBagsMax") Integer numberOfBagsMax,
        Pageable pageable
    );

    @Query("select batch from Batch batch left join fetch batch.strain left join fetch batch.recipe where batch.recipe.id = :recipeId")
    List<Batch> findByRecipeId(@Param("recipeId") Long recipeId);

    @Query(
        "select batch from Batch batch left join fetch batch.strain left join fetch batch.recipe " +
        "where YEAR(batch.startDate) = :year and MONTH(batch.startDate) = :month"
    )
    List<Batch> findByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    // Performance: Count active batches with a single database query
    @Query("select count(b) from Batch b where b.isActive = true")
    long countActiveBatches();

    // Performance: Get batch count grouped by phase (for active batches only)
    @Query("select b.currentPhase as phase, count(b) as count from Batch b where b.isActive = true group by b.currentPhase")
    List<Object[]> countActiveBatchesByPhase();
}
