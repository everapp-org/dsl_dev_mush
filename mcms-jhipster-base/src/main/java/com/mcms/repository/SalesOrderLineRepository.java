package com.mcms.repository;

import com.mcms.domain.SalesOrderLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesOrderLine entity.
 */
@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
    default Optional<SalesOrderLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SalesOrderLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SalesOrderLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select salesOrderLine from SalesOrderLine salesOrderLine left join fetch salesOrderLine.salesOrder left join fetch salesOrderLine.product left join fetch salesOrderLine.batch",
        countQuery = "select count(salesOrderLine) from SalesOrderLine salesOrderLine"
    )
    Page<SalesOrderLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select salesOrderLine from SalesOrderLine salesOrderLine left join fetch salesOrderLine.salesOrder left join fetch salesOrderLine.product left join fetch salesOrderLine.batch"
    )
    List<SalesOrderLine> findAllWithToOneRelationships();

    @Query(
        "select salesOrderLine from SalesOrderLine salesOrderLine left join fetch salesOrderLine.salesOrder left join fetch salesOrderLine.product left join fetch salesOrderLine.batch where salesOrderLine.id =:id"
    )
    Optional<SalesOrderLine> findOneWithToOneRelationships(@Param("id") Long id);
}
