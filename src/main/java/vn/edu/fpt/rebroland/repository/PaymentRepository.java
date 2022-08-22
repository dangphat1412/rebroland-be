package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Transactions, Integer> {
    @Query(value = " SELECT SUM(amount) FROM transactions ", nativeQuery = true)
    Long getTotalRevenue();

    @Query(value = " SELECT SUM(amount) FROM transactions " +
            "WHERE type_id = 1 ", nativeQuery = true)
    Long getTotalMoneyFromPost();

    @Query(value = " SELECT SUM(amount) FROM transactions " +
            "WHERE type_id = 2 ", nativeQuery = true)
    Long getTotalMoneyFromBroker();

    @Query(value = " SELECT SUM(amount) FROM transactions " +
            "WHERE type_id = 3 ", nativeQuery = true)
    Long getTotalDepositMoney();

    @Query(value = " SELECT p.* FROM `transactions` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE p.type_id = 1 " +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) ", nativeQuery = true)
    Page<Transactions> findAllPostPayment(Pageable pageable, String keyword);

    @Query(value = " SELECT p.* FROM `transactions` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE p.type_id = 2 " +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) ", nativeQuery = true)
    Page<Transactions> findAllBrokerPayment(Pageable pageable, String keyword);

    @Query(value = " SELECT p.* FROM `transactions` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE p.type_id = 3 " +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) ", nativeQuery = true)
    Page<Transactions> findAllDepositMoneyIntoWallet(Pageable pageable, String keyword);

    @Query(value = " SELECT p.* FROM `transactions` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE p.type_id = 6 " +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) ", nativeQuery = true)
    Page<Transactions> findAllWithdrawMoney(Pageable pageable, String keyword);

    @Query(value = " SELECT p.* FROM `transactions` p " +
            " JOIN `users` u on p.user_id = u.id" +
            " WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR p.user_id = :keyword) " +
            " AND (p.type_id != 4) " +
            " AND (p.type_id != 5) ", nativeQuery = true)
    Page<Transactions> findAll(Pageable pageable, String keyword);
}
