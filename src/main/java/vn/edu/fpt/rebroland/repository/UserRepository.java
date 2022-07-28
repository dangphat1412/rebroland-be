package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Post;
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

    @Query(value = " SELECT * FROM users " +
            " WHERE id IN (SELECT user_id FROM user_roles " +
            " WHERE role_id = 3) ", nativeQuery = true)
    Page<User> getAllBrokerPaging(Pageable pageable);

    @Query(value = " SELECT * FROM users " +
            " WHERE id IN (SELECT user_id FROM user_roles " +
            " WHERE role_id = 3) ", nativeQuery = true)
    List<User> getAllBroker();

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

    @Query(value = " SELECT u.*, AVG(star_rate) AS avgRate FROM `users` u " +
            " LEFT JOIN user_rates ur on u.id = ur.user_rated " +
            " WHERE u.id IN (SELECT user_id FROM user_roles GROUP BY user_id " +
            "               HAVING COUNT(user_id) = 2) " +
            "AND (ur.user_role_rated = 3 OR ur.user_role_rated is null) " +
            "AND IF(:fullName IS NULL, 1 = 1, u.full_name LIKE CONCAT('%',:fullName,'%')) " +
            "AND IF(:ward IS NULL, 1 = 1, u.ward LIKE CONCAT('%',:ward,'%')) " +
            "AND IF(:district IS NULL, 1 = 1, u.district LIKE CONCAT('%',:district,'%')) " +
            "AND IF(:province IS NULL, 1 = 1, u.province LIKE CONCAT('%',:province,'%')) " +
            "AND IF(:address IS NULL, 1 = 1, u.address LIKE CONCAT('%',:address,'%')) " +
            "GROUP BY u.id " +
            "ORDER BY avgRate DESC ", nativeQuery = true)
    Page<User> searchBrokerByStarRateDesc(String fullName, String ward, String district, String province,
                            String address, Pageable pageable);

    @Query(value = " SELECT u.*, AVG(star_rate) AS avgRate FROM `users` u " +
            " LEFT JOIN user_rates ur on u.id = ur.user_rated " +
            " WHERE u.id IN (SELECT user_id FROM user_roles GROUP BY user_id " +
            "               HAVING COUNT(user_id) = 2) " +
            "AND (ur.user_role_rated = 3 OR ur.user_role_rated is null) " +
            "AND IF(:fullName IS NULL, 1 = 1, u.full_name LIKE CONCAT('%',:fullName,'%')) " +
            "AND IF(:ward IS NULL, 1 = 1, u.ward LIKE CONCAT('%',:ward,'%')) " +
            "AND IF(:district IS NULL, 1 = 1, u.district LIKE CONCAT('%',:district,'%')) " +
            "AND IF(:province IS NULL, 1 = 1, u.province LIKE CONCAT('%',:province,'%')) " +
            "AND IF(:address IS NULL, 1 = 1, u.address LIKE CONCAT('%',:address,'%')) " +
            "GROUP BY u.id " +
            "ORDER BY avgRate ASC ", nativeQuery = true)
    Page<User> searchBrokerByStarRateAsc(String fullName, String ward, String district, String province,
                                          String address, Pageable pageable);

    @Query(value = " SELECT u.*, AVG(star_rate) AS avgRate FROM `users` u " +
            " LEFT JOIN user_rates ur on u.id = ur.user_rated " +
            " WHERE u.id IN (SELECT user_id FROM user_roles GROUP BY user_id " +
            "               HAVING COUNT(user_id) = 2) " +
            "AND (ur.user_role_rated = 3 OR ur.user_role_rated is null) " +
            "AND IF(:fullName IS NULL, 1 = 1, u.full_name LIKE CONCAT('%',:fullName,'%')) " +
            "AND IF(:ward IS NULL, 1 = 1, u.ward LIKE CONCAT('%',:ward,'%')) " +
            "AND IF(:district IS NULL, 1 = 1, u.district LIKE CONCAT('%',:district,'%')) " +
            "AND IF(:province IS NULL, 1 = 1, u.province LIKE CONCAT('%',:province,'%')) " +
            "AND IF(:address IS NULL, 1 = 1, u.address LIKE CONCAT('%',:address,'%')) " +
            "GROUP BY u.id " +
            "ORDER BY u.full_name ASC ", nativeQuery = true)
    Page<User> searchBrokerByNameAsc(String fullName, String ward, String district, String province,
                                         String address, Pageable pageable);

    @Query(value = " SELECT u.*, AVG(star_rate) AS avgRate FROM `users` u " +
            " LEFT JOIN user_rates ur on u.id = ur.user_rated " +
            " WHERE u.id IN (SELECT user_id FROM user_roles GROUP BY user_id " +
            "               HAVING COUNT(user_id) = 2) " +
            "AND (ur.user_role_rated = 3 OR ur.user_role_rated is null) " +
            "AND IF(:fullName IS NULL, 1 = 1, u.full_name LIKE CONCAT('%',:fullName,'%')) " +
            "AND IF(:ward IS NULL, 1 = 1, u.ward LIKE CONCAT('%',:ward,'%')) " +
            "AND IF(:district IS NULL, 1 = 1, u.district LIKE CONCAT('%',:district,'%')) " +
            "AND IF(:province IS NULL, 1 = 1, u.province LIKE CONCAT('%',:province,'%')) " +
            "AND IF(:address IS NULL, 1 = 1, u.address LIKE CONCAT('%',:address,'%')) " +
            "GROUP BY u.id " +
            "ORDER BY u.full_name ASC ", nativeQuery = true)
    Page<User> searchBrokerByNameDesc(String fullName, String ward, String district, String province,
                                     String address, Pageable pageable);
}
