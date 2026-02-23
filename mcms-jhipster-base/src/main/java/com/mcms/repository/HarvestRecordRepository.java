package com.mcms.repository;

import com.mcms.domain.HarvestRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HarvestRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HarvestRecordRepository extends JpaRepository<HarvestRecord, Long> {}
