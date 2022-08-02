package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query(value = "DELETE FROM reports WHERE post_id =:postId", nativeQuery = true)
    @Modifying
    void deleteReportPostByPostId(int postId);
}
