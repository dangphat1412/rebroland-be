package vn.edu.fpt.rebroland.repository;


import vn.edu.fpt.rebroland.entity.UserCare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCareRepository extends JpaRepository<UserCare, Integer> {
    @Query(value = "delete from user_cares where care_id =:careId", nativeQuery = true)
    @Modifying
    void deleteUserCareById(int careId);

    @Query(value = "delete from post_cares where care_id =:careId", nativeQuery = true)
    @Modifying
    void deletePostCareById(int careId);

    @Query(value = "select * from user_cares u join post_cares p on u.care_id = p.care_id " +
            "where phone = :phone and " +
            "post_id = :postId", nativeQuery = true)
    UserCare findUserCareByPhoneAndPostId(String phone, int postId);

    @Query(value = "select * from user_cares where phone =:phone", nativeQuery = true)
    UserCare findUserCareByPhone(String phone);

    @Query(value = "select * from `user_cares` where user_id =:userId", nativeQuery = true)
    Page<UserCare> getUserCareByUserId(Pageable pageable, int userId);

    @Query(value = "select * from `user_cares` where user_id =:userId", nativeQuery = true)
    List<UserCare> getListUserCareByUserId(int userId);

}
