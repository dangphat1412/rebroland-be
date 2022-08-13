package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Withdraw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WithdrawRepository extends JpaRepository<Withdraw,Integer> {
    @Query(value = " SELECT * FROM `withdraw` " +
            " WHERE status = 1 " +
            " AND type = 1 " +
            " AND user_id IN (SELECT u.id FROM `users` u " +
            "                 WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword))", nativeQuery = true)
    Page<Withdraw> getAllDirectWithdrawNotProcess(String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `withdraw` " +
            " WHERE status != 1 " +
            " AND type = 1 " +
            " AND user_id IN (SELECT u.id FROM `users` u " +
            "                 WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword))", nativeQuery = true)
    Page<Withdraw> getAllDirectWithdrawProcessed(String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `withdraw` " +
            " WHERE type = 1 " +
            " AND user_id IN (SELECT u.id FROM `users` u " +
            "                 WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword))", nativeQuery = true)
    Page<Withdraw> getAllDirectWithdraw(String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `withdraw` " +
            " WHERE type = 2 " +
            " AND user_id IN (SELECT u.id FROM `users` u " +
            "                 WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword))", nativeQuery = true)
    Page<Withdraw> getAllTransferWithdraw(String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `withdraw` " +
            " WHERE status = 1 " +
            " AND type = 2 " +
            " AND user_id IN (SELECT u.id FROM `users` u " +
            "                 WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword))", nativeQuery = true)
    Page<Withdraw> getAllTransferWithdrawNotProcess(String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `withdraw` " +
            " WHERE status != 1 " +
            " AND type = 2 " +
            " AND user_id IN (SELECT u.id FROM `users` u " +
            "                 WHERE ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%')) OR u.id = :keyword))", nativeQuery = true)
    Page<Withdraw> getAllTransferWithdrawProcessed(String keyword, Pageable pageable);

    @Query(value = " SELECT * FROM `withdraw` " +
            " WHERE id = :withdrawId ", nativeQuery = true)
    Withdraw getWithdrawById(int withdrawId);
}
