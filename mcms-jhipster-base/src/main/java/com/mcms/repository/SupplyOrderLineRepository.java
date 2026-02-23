package com.mcms.repository;

import com.mcms.domain.SupplyOrderLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SupplyOrderLine entity.
 */
@Repository
public interface SupplyOrderLineRepository extends JpaRepository<SupplyOrderLine, Long> {
    default Optional<SupplyOrderLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SupplyOrderLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SupplyOrderLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select supplyOrderLine from SupplyOrderLine supplyOrderLine left join fetch supplyOrderLine.supplyOrder left join fetch supplyOrderLine.material left join fetch supplyOrderLine.batch",
        countQuery = "select count(supplyOrderLine) from SupplyOrderLine supplyOrderLine"
    )
    Page<SupplyOrderLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select supplyOrderLine from SupplyOrderLine supplyOrderLine left join fetch supplyOrderLine.supplyOrder left join fetch supplyOrderLine.material left join fetch supplyOrderLine.batch"
    )
    List<SupplyOrderLine> findAllWithToOneRelationships();

    @Query(
        "select supplyOrderLine from SupplyOrderLine supplyOrderLine left join fetch supplyOrderLine.supplyOrder left join fetch supplyOrderLine.material left join fetch supplyOrderLine.batch where supplyOrderLine.id =:id"
    )
    Optional<SupplyOrderLine> findOneWithToOneRelationships(@Param("id") Long id);
}
