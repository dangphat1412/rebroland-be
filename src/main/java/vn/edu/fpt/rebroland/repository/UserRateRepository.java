package vn.edu.fpt.rebroland.repository;


import vn.edu.fpt.rebroland.entity.UserRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRateRepository extends JpaRepository<UserRate, Integer> {

    @Query(value = " SELECT * FROM `user_rates` " +
            " WHERE user_rated = :userRatedId " +
            " AND user_role_rated = :roleRatedId " +
            " AND start_date IN (SELECT MAX(start_date) FROM `user_rates` " +
            "                    WHERE user_rated = 11 " +
            "                    AND user_role_rated = 3 " +
            "                    GROUP BY user_id) ", nativeQuery = true)
    List<UserRate> getStarRateOfUserRated(int userRatedId, int roleRatedId);
}
