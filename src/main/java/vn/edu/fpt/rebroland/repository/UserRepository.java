package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhone(String phone);
    Boolean existsByPhone(String phone);

    @Query(value = " SELECT * FROM `users` " +
            " WHERE phone = :phone AND block = false ", nativeQuery = true)
    User getUserByPhone(String phone);

    @Query(value = " SELECT * FROM `users` " +
            " WHERE id = :userId", nativeQuery = true)
    User getUserById(int userId);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE u.id IN (SELECT user_id FROM user_roles " +
            "               WHERE role_id = 3) " +
            " AND (u.id != :userId) ", nativeQuery = true)
    Page<User> getAllBrokerPaging(int userId, Pageable pageable);

    @Query(value = " SELECT * FROM users " +
            " WHERE id IN (SELECT user_id FROM user_roles " +
            " WHERE role_id = 3) " +
            " AND id != :userId ", nativeQuery = true)
    List<User> getAllBroker(int userId);

//    @Query(value = " SELECT * FROM `users` u " +
//            " WHERE u.id IN (SELECT user_id FROM user_roles GROUP BY user_id " +
//            "               HAVING COUNT(user_id) = 2) " +
//            "AND IF(:fullName IS NULL, 1 = 1, u.full_name LIKE CONCAT('%',:fullName,'%')) " +
//            "AND IF(:ward IS NULL, 1 = 1, u.ward LIKE CONCAT('%',:ward,'%')) " +
//            "AND IF(:district IS NULL, 1 = 1, u.district LIKE CONCAT('%',:district,'%')) " +
//            "AND IF(:province IS NULL, 1 = 1, u.province LIKE CONCAT('%',:province,'%')) " +
//            "AND IF(:address IS NULL, 1 = 1, u.address LIKE CONCAT('%',:address,'%')) ", nativeQuery = true)
//    Page<User> searchBroker(String fullName, String ward, String district, String province,
//                            String address, Pageable pageable);

    @Query(value = " SELECT u.* FROM `users` u " +
            " LEFT JOIN average_rates r on u.id = r.user_id " +
            " WHERE u.id IN (SELECT user_id FROM `user_roles` " +
            "                WHERE role_id = 3) " +
            "AND IF(:fullName IS NULL, 1 = 1, u.full_name LIKE CONCAT('%',:fullName,'%')) " +
            "AND IF(:ward IS NULL, 1 = 1, u.ward LIKE CONCAT('%',:ward,'%')) " +
            "AND IF(:district IS NULL, 1 = 1, u.district LIKE CONCAT('%',:district,'%')) " +
            "AND IF(:province IS NULL, 1 = 1, u.province LIKE CONCAT('%',:province,'%')) " +
            "AND IF(:check IS NULL, 1 = 1, u.id IN (SELECT user_id FROM `posts` " +
            "                                       WHERE property_id IN :propertyType)) " +
            "AND (u.id != :userId) " +
            "AND u.block = false "  +
            "ORDER BY r.avg_rate DESC ", nativeQuery = true)
    Page<User> searchBrokerByStarRateDesc(String fullName, String ward, String district, String province,
                                          String check, List<Integer> propertyType, Pageable pageable, int userId);

    @Query(value = " SELECT u.* FROM `users` u " +
            " LEFT JOIN average_rates r on u.id = r.user_id " +
            " WHERE u.id IN (SELECT user_id FROM `user_roles` " +
            "                WHERE role_id = 3) " +
            "AND IF(:fullName IS NULL, 1 = 1, u.full_name LIKE CONCAT('%',:fullName,'%')) " +
            "AND IF(:ward IS NULL, 1 = 1, u.ward LIKE CONCAT('%',:ward,'%')) " +
            "AND IF(:district IS NULL, 1 = 1, u.district LIKE CONCAT('%',:district,'%')) " +
            "AND IF(:province IS NULL, 1 = 1, u.province LIKE CONCAT('%',:province,'%')) " +
            "AND IF(:check IS NULL, 1 = 1, u.id IN (SELECT user_id FROM `posts` " +
            "                                       WHERE property_id IN :propertyType)) " +
            "AND (u.id != :userId) " +
            "AND u.block = false ", nativeQuery = true)
    List<User> searchBroker(String fullName, String ward, String district, String province,
                                          String check, List<Integer> propertyType, int userId);


    @Query(value = " SELECT * FROM `users` u " +
            " WHERE (u.id != :userId) " +
            " AND ((u.full_name LIKE CONCAT('%',:keyword,'%')) OR (u.phone LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword) ", nativeQuery = true)
    Page<User> getAllUserForAdminPaging(int userId, String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE (u.id != :userId) " +
            " AND ((u.full_name LIKE CONCAT('%',:keyword,'%')) OR (u.phone LIKE CONCAT('%',:keyword,'%')) " +
            "      OR u.id = :keyword) " +
            " AND u.block = false ", nativeQuery = true)
    Page<User> getAllActiveUserForAdminPaging(int userId, String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE (u.id != :userId) " +
            " AND ((u.full_name LIKE CONCAT('%',:keyword,'%')) OR (u.phone LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword) " +
            " AND u.block = true ", nativeQuery = true)
    Page<User> getAllBlockUserForAdminPaging(int userId, String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `users` u" +
            " WHERE (u.id != :userId) " +
            " AND ((u.full_name LIKE CONCAT('%',:keyword,'%')) OR (u.phone LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword) ", nativeQuery = true)
    List<User> getAllUserForAdmin(int userId, String keyword);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE (u.id != :userId) " +
            " AND ((u.full_name LIKE CONCAT('%',:keyword,'%')) OR (u.phone LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword) " +
            " AND u.block = false ", nativeQuery = true)
    List<User> getAllActiveUserForAdmin(int userId, String keyword);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE (u.id != :userId) " +
            " AND ((u.full_name LIKE CONCAT('%',:keyword,'%')) OR (u.phone LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword) " +
            " AND u.block = true ", nativeQuery = true)
    List<User> getAllBlockUserForAdmin(int userId, String keyword);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE u.id IN (SELECT user_id FROM reports " +
            "                     WHERE user_id IS NOT NULL)" +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%'))) ", nativeQuery = true)
    Page<User> getListReportedUser(Pageable pageable, String keyword);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE u.id IN (SELECT user_id FROM reports " +
            "                     WHERE user_id IS NOT NULL AND status = 1)" +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%'))) ", nativeQuery = true)
    Page<User> getListReportedUserNotProcess(Pageable pageable, String keyword);

    @Query(value = " SELECT * FROM `users` u " +
            " WHERE u.id IN (SELECT user_id FROM reports " +
            "                     WHERE user_id IS NOT NULL AND status = 2)" +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%'))) ", nativeQuery = true)
    Page<User> getListReportedUserProcessed(Pageable pageable, String keyword);
}
