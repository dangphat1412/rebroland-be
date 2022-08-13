package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query(value = " SELECT * FROM `notifications` " +
            " WHERE user_id = :userId" +
            " AND un_read = true ", nativeQuery = true)
    List<Notification> getUnreadNotification(int userId);

    @Query(value = " SELECT * FROM `notifications` " +
            " WHERE user_id = :userId" +
            " AND un_read = true " +
            " ORDER BY date DESC ", nativeQuery = true)
    Page<Notification> getUnreadNotificationPaging(int userId, Pageable pageable);
}
