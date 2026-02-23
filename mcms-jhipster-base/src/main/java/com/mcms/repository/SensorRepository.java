package com.mcms.repository;

import com.mcms.domain.Sensor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Sensor entity.
 */
@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    default Optional<Sensor> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Sensor> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Sensor> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select sensor from Sensor sensor left join fetch sensor.room", countQuery = "select count(sensor) from Sensor sensor")
    Page<Sensor> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sensor from Sensor sensor left join fetch sensor.room")
    List<Sensor> findAllWithToOneRelationships();

    @Query("select sensor from Sensor sensor left join fetch sensor.room where sensor.id =:id")
    Optional<Sensor> findOneWithToOneRelationships(@Param("id") Long id);
}
