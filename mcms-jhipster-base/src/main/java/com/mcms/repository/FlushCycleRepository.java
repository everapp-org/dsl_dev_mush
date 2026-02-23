package com.mcms.repository;

import com.mcms.domain.FlushCycle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FlushCycle entity.
 */
@Repository
public interface FlushCycleRepository extends JpaRepository<FlushCycle, Long> {
    default Optional<FlushCycle> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FlushCycle> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FlushCycle> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select flushCycle from FlushCycle flushCycle left join fetch flushCycle.batch",
        countQuery = "select count(flushCycle) from FlushCycle flushCycle"
    )
    Page<FlushCycle> findAllWithToOneRelationships(Pageable pageable);

    @Query("select flushCycle from FlushCycle flushCycle left join fetch flushCycle.batch")
    List<FlushCycle> findAllWithToOneRelationships();

    @Query("select flushCycle from FlushCycle flushCycle left join fetch flushCycle.batch where flushCycle.id =:id")
    Optional<FlushCycle> findOneWithToOneRelationships(@Param("id") Long id);
}
