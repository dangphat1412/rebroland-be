package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
