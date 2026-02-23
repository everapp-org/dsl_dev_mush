package com.mcms.repository;

import com.mcms.domain.MonthlyReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MonthlyReport entity.
 */
@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {
    default Optional<MonthlyReport> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MonthlyReport> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MonthlyReport> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select monthlyReport from MonthlyReport monthlyReport left join fetch monthlyReport.batch",
        countQuery = "select count(monthlyReport) from MonthlyReport monthlyReport"
    )
    Page<MonthlyReport> findAllWithToOneRelationships(Pageable pageable);

    @Query("select monthlyReport from MonthlyReport monthlyReport left join fetch monthlyReport.batch")
    List<MonthlyReport> findAllWithToOneRelationships();

    @Query("select monthlyReport from MonthlyReport monthlyReport left join fetch monthlyReport.batch where monthlyReport.id =:id")
    Optional<MonthlyReport> findOneWithToOneRelationships(@Param("id") Long id);
}
