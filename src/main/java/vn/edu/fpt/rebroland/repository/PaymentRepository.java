package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Query(value = " SELECT SUM(amount) FROM payments ", nativeQuery = true)
    Long getTotalRevenue();

    @Query(value = " SELECT SUM(amount) FROM payments " +
            "WHERE type = 'Post' ", nativeQuery = true)
    Long getTotalMoneyFromPost();

    @Query(value = " SELECT SUM(amount) FROM payments " +
            "WHERE type = 'Broker' ", nativeQuery = true)
    Long getTotalMoneyFromBroker();
}
