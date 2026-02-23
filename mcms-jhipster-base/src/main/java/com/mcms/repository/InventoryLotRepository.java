package com.mcms.repository;

import com.mcms.domain.InventoryLot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventoryLot entity.
 */
@Repository
public interface InventoryLotRepository extends JpaRepository<InventoryLot, Long> {
    default Optional<InventoryLot> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InventoryLot> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InventoryLot> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select inventoryLot from InventoryLot inventoryLot left join fetch inventoryLot.material",
        countQuery = "select count(inventoryLot) from InventoryLot inventoryLot"
    )
    Page<InventoryLot> findAllWithToOneRelationships(Pageable pageable);

    @Query("select inventoryLot from InventoryLot inventoryLot left join fetch inventoryLot.material")
    List<InventoryLot> findAllWithToOneRelationships();

    @Query("select inventoryLot from InventoryLot inventoryLot left join fetch inventoryLot.material where inventoryLot.id =:id")
    Optional<InventoryLot> findOneWithToOneRelationships(@Param("id") Long id);
}
