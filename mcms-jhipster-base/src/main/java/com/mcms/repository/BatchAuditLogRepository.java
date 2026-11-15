package com.mcms.repository;

import com.mcms.domain.BatchAuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BatchAuditLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BatchAuditLogRepository extends JpaRepository<BatchAuditLog, Long> {
    List<BatchAuditLog> findByBatchIdOrderByTimestampDesc(Long batchId);
}
