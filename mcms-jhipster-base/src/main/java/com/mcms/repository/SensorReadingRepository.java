package com.mcms.repository;

import com.mcms.domain.SensorReading;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SensorReading entity.
 */
@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    default Optional<SensorReading> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SensorReading> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SensorReading> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sensorReading from SensorReading sensorReading left join fetch sensorReading.sensor",
        countQuery = "select count(sensorReading) from SensorReading sensorReading"
    )
    Page<SensorReading> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sensorReading from SensorReading sensorReading left join fetch sensorReading.sensor")
    List<SensorReading> findAllWithToOneRelationships();

    @Query("select sensorReading from SensorReading sensorReading left join fetch sensorReading.sensor where sensorReading.id =:id")
    Optional<SensorReading> findOneWithToOneRelationships(@Param("id") Long id);
}
