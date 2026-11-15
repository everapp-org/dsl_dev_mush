package com.mcms.repository;

import com.mcms.domain.ReportAuditLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReportAuditLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReportAuditLogRepository extends JpaRepository<ReportAuditLog, Long> {}
