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
            "where u.user_cared_id = :userCaredId and " +
            "p.post_id = :postId", nativeQuery = true)
    UserCare findUserCareByUserCaredIdAndPostId(Integer userCaredId, int postId);

    @Query(value = "select * from user_cares where user_cared_id = :userCaredId", nativeQuery = true)
    UserCare findUserCareByUserCaredId(Integer userCaredId);

    @Query(value = "select * from `user_cares` where user_id =:userId" +
            " AND user_cared_id IN (SELECT u.id FROM `users` u " +
            "                       WHERE (u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%'))) ", nativeQuery = true)
    Page<UserCare> getUserCareByUserId(Pageable pageable, int userId, String keyword);

    @Query(value = "select * from `user_cares` where user_id =:userId", nativeQuery = true)
    List<UserCare> getListUserCareByUserId(int userId);

    @Query(value = "delete from `post_cares`  where post_id =:postId", nativeQuery = true)
    @Modifying
    void deletePostCareByPostId(int postId);

}
