package com.mcms.repository;

import com.mcms.domain.SalesOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesOrder entity.
 */
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    default Optional<SalesOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SalesOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SalesOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select salesOrder from SalesOrder salesOrder left join fetch salesOrder.customer",
        countQuery = "select count(salesOrder) from SalesOrder salesOrder"
    )
    Page<SalesOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select salesOrder from SalesOrder salesOrder left join fetch salesOrder.customer")
    List<SalesOrder> findAllWithToOneRelationships();

    @Query("select salesOrder from SalesOrder salesOrder left join fetch salesOrder.customer where salesOrder.id =:id")
    Optional<SalesOrder> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select count(salesOrder) > 0 from SalesOrder salesOrder where salesOrder.customer.id = :customerId")
    boolean existsByCustomerId(@Param("customerId") Long customerId);

    @Query("select salesOrder from SalesOrder salesOrder left join fetch salesOrder.customer where salesOrder.customer.id = :customerId order by salesOrder.orderDate desc")
    List<SalesOrder> findByCustomerId(@Param("customerId") Long customerId);
}
