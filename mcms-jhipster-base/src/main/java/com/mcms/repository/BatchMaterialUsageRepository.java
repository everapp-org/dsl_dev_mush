package com.mcms.repository;

import com.mcms.domain.BatchMaterialUsage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BatchMaterialUsage entity.
 */
@Repository
public interface BatchMaterialUsageRepository extends JpaRepository<BatchMaterialUsage, Long> {
    default Optional<BatchMaterialUsage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BatchMaterialUsage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BatchMaterialUsage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select batchMaterialUsage from BatchMaterialUsage batchMaterialUsage left join fetch batchMaterialUsage.batch left join fetch batchMaterialUsage.inventoryLot left join fetch batchMaterialUsage.material",
        countQuery = "select count(batchMaterialUsage) from BatchMaterialUsage batchMaterialUsage"
    )
    Page<BatchMaterialUsage> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select batchMaterialUsage from BatchMaterialUsage batchMaterialUsage left join fetch batchMaterialUsage.batch left join fetch batchMaterialUsage.inventoryLot left join fetch batchMaterialUsage.material"
    )
    List<BatchMaterialUsage> findAllWithToOneRelationships();

    @Query(
        "select batchMaterialUsage from BatchMaterialUsage batchMaterialUsage left join fetch batchMaterialUsage.batch left join fetch batchMaterialUsage.inventoryLot left join fetch batchMaterialUsage.material where batchMaterialUsage.id =:id"
    )
    Optional<BatchMaterialUsage> findOneWithToOneRelationships(@Param("id") Long id);
}
