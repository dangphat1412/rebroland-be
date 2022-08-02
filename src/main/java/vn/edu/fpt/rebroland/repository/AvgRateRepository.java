package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.AvgRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AvgRateRepository extends JpaRepository<AvgRate, Integer> {
    @Query(value = " SELECT * FROM average_rates " +
            " WHERE user_id = :userId " +
            " AND role_id = :roleId ", nativeQuery = true)
    AvgRate getAvgRateByUserIdAndRoleId(int userId, int roleId);

}
