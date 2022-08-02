package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.UserFollowPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserFollowPostRepository extends JpaRepository<UserFollowPost, Integer> {

    @Query(value = " SELECT * FROM user_follow_posts " +
            "WHERE user_id = :userId " +
            "AND post_id = :postId " +
            "AND role_id = :roleId ", nativeQuery = true)
    UserFollowPost getUserFollowPost(int userId, int postId, int roleId);

    @Query(value = "DELETE FROM user_follow_posts WHERE post_id = :postId", nativeQuery = true)
    @Modifying
    void deleteFollowByPostId(int postId);


}
