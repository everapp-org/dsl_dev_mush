package com.mcms.repository;

import com.mcms.domain.CostRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CostRecord entity.
 */
@Repository
public interface CostRecordRepository extends JpaRepository<CostRecord, Long> {
    default Optional<CostRecord> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CostRecord> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CostRecord> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select costRecord from CostRecord costRecord left join fetch costRecord.batch",
        countQuery = "select count(costRecord) from CostRecord costRecord"
    )
    Page<CostRecord> findAllWithToOneRelationships(Pageable pageable);

    @Query("select costRecord from CostRecord costRecord left join fetch costRecord.batch")
    List<CostRecord> findAllWithToOneRelationships();

    @Query("select costRecord from CostRecord costRecord left join fetch costRecord.batch where costRecord.id =:id")
    Optional<CostRecord> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select costRecord from CostRecord costRecord where costRecord.batch.id = :batchId order by costRecord.recordDate desc")
    List<CostRecord> findByBatchIdOrderByRecordDateDesc(@Param("batchId") Long batchId);
}
