package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.ReportDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportDetailRepository extends JpaRepository<ReportDetail, Integer> {
    @Query(value = " SELECT * FROM `report_details` r " +
            " WHERE r.report_id IN (SELECT rp.id FROM `reports` rp WHERE rp.id = :reportId)", nativeQuery = true)
    Page<ReportDetail> getListDetailReport(int reportId, Pageable pageable);

    @Query(value = " SELECT DISTINCT r.user_id FROM `report_details` r " +
            " WHERE r.report_id = :reportId ", nativeQuery = true)
    List<Integer> getDetailReportByReportId(int reportId);

}
