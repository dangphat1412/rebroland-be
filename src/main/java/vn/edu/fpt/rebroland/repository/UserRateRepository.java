package vn.edu.fpt.rebroland.repository;


import vn.edu.fpt.rebroland.entity.UserRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRateRepository extends JpaRepository<UserRate, Integer> {

//    @Query(value = " SELECT * FROM `user_rates` " +
//            " WHERE user_rated = :userRatedId " +
//            " AND user_role_rated = :roleRatedId " +
//            " AND start_date IN (SELECT MAX(start_date) FROM `user_rates` " +
//            "                    WHERE user_rated = :userRatedId " +
//            "                    AND user_role_rated = :roleRatedId " +
//            "                    GROUP BY user_id) ", nativeQuery = true)
//    List<UserRate> getStarRateOfUserRated(int userRatedId, int roleRatedId);

    @Query(value = " SELECT * FROM `user_rates` " +
            " WHERE user_rated = :userRatedId " +
            " AND user_role_rated = :roleRatedId ", nativeQuery = true)
    List<UserRate> getStarRateOfUserRated(int userRatedId, int roleRatedId);

    @Query(value = " SELECT * FROM `user_rates` " +
            " WHERE user_rated = :userRatedId " +
            " AND user_role_rated = :roleRatedId " +
            " ORDER BY start_date DESC ", nativeQuery = true)
    Page<UserRate> getListUserRate(int userRatedId, int roleRatedId, Pageable pageable);

    @Query(value = " SELECT * FROM `user_rates` " +
            " WHERE user_id = :userId " +
            " AND user_rated = :userRatedId " +
            " AND user_role_rated = :roleId " +
            " ORDER BY start_date DESC " +
            " LIMIT 1 ", nativeQuery = true)
    UserRate getUserRateStartDateMax(int userId, int userRatedId, int roleId);
}
