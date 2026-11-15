package com.mcms.repository;

import com.mcms.domain.SupplyOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SupplyOrder entity.
 */
@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {
    default Optional<SupplyOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SupplyOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SupplyOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select supplyOrder from SupplyOrder supplyOrder left join fetch supplyOrder.supplier",
        countQuery = "select count(supplyOrder) from SupplyOrder supplyOrder"
    )
    Page<SupplyOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select supplyOrder from SupplyOrder supplyOrder left join fetch supplyOrder.supplier")
    List<SupplyOrder> findAllWithToOneRelationships();

    @Query("select supplyOrder from SupplyOrder supplyOrder left join fetch supplyOrder.supplier where supplyOrder.id =:id")
    Optional<SupplyOrder> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select count(supplyOrder) > 0 from SupplyOrder supplyOrder where supplyOrder.supplier.id = :supplierId")
    boolean existsBySupplierId(@Param("supplierId") Long supplierId);

    @Query("select supplyOrder from SupplyOrder supplyOrder left join fetch supplyOrder.supplier where supplyOrder.supplier.id = :supplierId")
    List<SupplyOrder> findBySupplierId(@Param("supplierId") Long supplierId);
}
