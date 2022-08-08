package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Query(value = " SELECT SUM(amount) FROM payments ", nativeQuery = true)
    Long getTotalRevenue();

    @Query(value = " SELECT SUM(amount) FROM payments " +
            "WHERE type_id = 1 ", nativeQuery = true)
    Long getTotalMoneyFromPost();

    @Query(value = " SELECT SUM(amount) FROM payments " +
            "WHERE type_id = 2 ", nativeQuery = true)
    Long getTotalMoneyFromBroker();

    @Query(value = " SELECT p.* FROM `payments` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE p.type_id = 1 " +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) ", nativeQuery = true)
    Page<Payment> findAllPostPayment(Pageable pageable, String keyword);

    @Query(value = " SELECT p.* FROM `payments` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE p.type_id = 2 " +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) ", nativeQuery = true)
    Page<Payment> findAllBrokerPayment(Pageable pageable, String keyword);

    @Query(value = " SELECT p.* FROM `payments` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) ", nativeQuery = true)
    Page<Payment> findAll(Pageable pageable, String keyword);
}
