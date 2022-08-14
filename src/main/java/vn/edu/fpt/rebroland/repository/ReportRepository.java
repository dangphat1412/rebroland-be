package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query(value = "DELETE FROM reports WHERE post_id =:postId", nativeQuery = true)
    @Modifying
    void deleteReportPostByPostId(int postId);

    @Query(value = " SELECT r.* FROM `reports` r " +
            " LEFT JOIN posts p on r.post_id = p.post_id " +
            " WHERE r.post_id IS NOT NULL " +
            " AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%')) OR r.post_id = :keyword) ", nativeQuery = true)
    Page<Report> getListReportPost(Pageable pageable, String keyword);

    @Query(value = " SELECT r.* FROM `reports` r " +
            " LEFT JOIN posts p on r.post_id = p.post_id " +
            " WHERE r.post_id IS NOT NULL AND r.status = 1" +
            " AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%')) OR r.post_id = :keyword) ", nativeQuery = true)
    Page<Report> getListReportPostNotProcess(Pageable pageable, String keyword);

    @Query(value = " SELECT r.* FROM `reports` r " +
            " LEFT JOIN posts p on r.post_id = p.post_id "  +
            " WHERE r.post_id IS NOT NULL AND (r.status = 2 OR r.status = 3) " +
            " AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%')) OR r.post_id = :keyword) ", nativeQuery = true)
    Page<Report> getListReportPostProcessed(Pageable pageable, String keyword);

    @Query(value = " SELECT r.* FROM `reports` r " +
            " LEFT JOIN users u on r.user_id = u.id " +
            " WHERE r.user_id IS NOT NULL " +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR r.user_id = :keyword) ", nativeQuery = true)
    Page<Report> getListReportUser(Pageable pageable, String keyword);

    @Query(value = " SELECT r.* FROM `reports` r " +
            " LEFT JOIN users u on r.user_id = u.id " +
            " WHERE r.user_id IS NOT NULL AND r.status = 1" +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR r.user_id = :keyword) ", nativeQuery = true)
    Page<Report> getListReportUserNotProcess(Pageable pageable, String keyword);

    @Query(value = " SELECT r.* FROM `reports` r " +
            " LEFT JOIN `users` u on r.user_id = u.id " +
            " WHERE r.user_id IS NOT NULL AND (r.status = 2 OR r.status = 3)" +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR r.user_id = :keyword) ", nativeQuery = true)
    Page<Report> getListReportUserProcessed(Pageable pageable, String keyword);

    @Query(value = " SELECT * FROM `reports` r " +
            " WHERE r.post_id = :postId ", nativeQuery = true)
    Report getReportPost(int postId);

    @Query(value = " SELECT * FROM `reports` r " +
            " WHERE r.post_id = :postId " +
            " AND r.status = 1 ", nativeQuery = true)
    Report getReportedPost(int postId);

    @Query(value = " SELECT * FROM `reports` r " +
            " WHERE r.id = :reportId ", nativeQuery = true)
    Report getReportById(int reportId);

    @Query(value = " SELECT * FROM `reports` r " +
            " WHERE r.user_id = :userId " +
            " AND r.status = 1 ", nativeQuery = true)
    Report getReportedUser(int userId);

    @Query(value = " SELECT * FROM `reports` r " +
            " WHERE r.user_reported_id IS NOT NULL ", nativeQuery = true)
    Page<Report> getListReportUser(Pageable pageable);

    @Query(value = " SELECT COUNT(r.report_id) FROM `report_details` r " +
            " WHERE r.report_id IN (SELECT id FROM reports " +
            "                       WHERE post_id = :postId and status = :status and id = :reportId) " +
            " GROUP BY r.report_id ", nativeQuery = true)
    int getNumberOfUserReportPost(int postId, int status, int reportId);

    @Query(value = " SELECT COUNT(r.report_id) FROM `report_details` r " +
            " WHERE r.report_id IN (SELECT id FROM reports " +
            "                       WHERE user_id = :userId and status = :status and id = :reportId) " +
            " GROUP BY r.report_id ", nativeQuery = true)
    int getNumberOfUserReportUser(int userId, int status, int reportId);

}
