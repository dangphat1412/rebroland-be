package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.UserCareDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCareDetailRepository extends JpaRepository<UserCareDetail,Integer> {
    @Query(value = "select * from user_care_details where care_id = :careId order by date_create desc",nativeQuery = true)
    List<UserCareDetail> getUserCareDetailsByCareId(int careId);
}
