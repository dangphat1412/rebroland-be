package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Post;

import vn.edu.fpt.rebroland.payload.PostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query(value = " SELECT * FROM `posts` " +
            " WHERE user_id = :userId " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId) ", nativeQuery = true)
    Page<Post> findByUserIdAndPropertyId(int userId, int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT * FROM `posts` " +
            " WHERE original_post is null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId) ", nativeQuery = true)
    Page<Post> findAllPostForBroker(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT * FROM `posts` " +
            " WHERE original_post is not null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId) ", nativeQuery = true)
    Page<Post> findDerivativePostOfBroker(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY Total ASC", nativeQuery = true)
    Page<Post> findAllPostForBrokerOrderByPriceAsc(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is not null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY Total ASC", nativeQuery = true)
    Page<Post> findDerivativePostOfBrokerOrderByPriceAsc(int propertyId, String check, Pageable pageable);


    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY Total DESC", nativeQuery = true)
    Page<Post> findAllPostForBrokerOrderByPriceDesc(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is not null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY Total DESC", nativeQuery = true)
    Page<Post> findDerivativePostOfBrokerOrderByPriceDesc(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY per_m2 ASC", nativeQuery = true)
    Page<Post> findAllPostForBrokerOrderByPricePerSquareAsc(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is not null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY per_m2 ASC", nativeQuery = true)
    Page<Post> findDerivativePostOfBrokerOrderByPricePerSquareAsc(int propertyId, String check, Pageable pageable);


    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY per_m2 DESC", nativeQuery = true)
    Page<Post> findAllPostForBrokerOrderByPricePerSquareDesc(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM `posts` " +
            " WHERE original_post is not null " +
            " AND IF(:check IS NULL, 1 = 1, property_id = :propertyId)" +
            " ORDER BY per_m2 DESC", nativeQuery = true)
    Page<Post> findDerivativePostOfBrokerOrderByPricePerSquareDesc(int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT * FROM `posts` ", nativeQuery = true)
    Page<Post> findAll(Pageable pageable);

    @Query(value = " SELECT * FROM `posts` " +
            " WHERE user_id = :userId " +
            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)", nativeQuery = true)
    Page<Post> getDerivativePostByUserId(int userId, int propertyId, String check, Pageable pageable);

//    @Query(value = " SELECT * FROM `posts` " +
//            " WHERE user_id = :userId " +
//            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)", nativeQuery = true)
//    Page<Post> getAllPostByUserId(int userId, int propertyId, String check, Pageable pageable);


    @Query(value = " SELECT * FROM posts " +
            " WHERE user_id = :userId ", nativeQuery = true)
    List<Post> getAllDerivativePostByUserId(int userId);

    @Query(value = " SELECT * FROM posts " +
            " WHERE user_id = :userId " +
            " AND original_post is null" , nativeQuery = true)
    List<Post> getAllPostByUserIdAndRoleId(int userId);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM posts " +
            " WHERE user_id = :userId " +
            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
            " ORDER BY Total ASC", nativeQuery = true)
    Page<Post> getDerivativePostByUserIdOrderByPriceAsc(int userId, int propertyId, String check, Pageable pageable);

//    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
//            " FROM posts " +
//            " WHERE user_id = :userId " +
//            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
//            " ORDER BY Total ASC", nativeQuery = true)
//    Page<Post> getAllPostByUserIdOrderByPriceAsc(int userId, int propertyId, String check, Pageable pageable);


    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM posts " +
            " WHERE user_id = :userId " +
            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
            " ORDER BY Total DESC", nativeQuery = true)
    Page<Post> getDerivativePostByUserIdOrderByPriceDesc(int userId, int propertyId, String check, Pageable pageable);

//    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
//            " FROM posts " +
//            " WHERE user_id = :userId " +
//            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
//            " ORDER BY Total DESC", nativeQuery = true)
//    Page<Post> getAllPostByUserIdOrderByPriceDesc(int userId, int propertyId, String check, Pageable pageable);


    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM posts " +
            " WHERE user_id = :userId " +
            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
            " ORDER BY per_m2 ASC", nativeQuery = true)
    Page<Post> getDerivativePostByUserIdOrderByPricePerSquareAsc(int userId, int propertyId, String check, Pageable pageable);

//    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
//            " FROM posts " +
//            " WHERE user_id = :userId " +
//            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
//            " ORDER BY per_m2 ASC", nativeQuery = true)
//    Page<Post> getAllPostByUserIdOrderByPricePerSquareAsc(int userId, int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
            " FROM posts " +
            " WHERE user_id = :userId " +
            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
            " ORDER BY per_m2 DESC", nativeQuery = true)
    Page<Post> getDerivativePostByUserIdOrderByPricePerSquareDesc(int userId, int propertyId, String check, Pageable pageable);

//    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2 " +
//            " FROM posts " +
//            " WHERE user_id = :userId " +
//            " AND original_post is null" +
//            " AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, property_id = :propertyId)" +
//            " ORDER BY per_m2 DESC", nativeQuery = true)
//    Page<Post> getAllPostByUserIdOrderByPricePerSquareDesc(int userId, int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2  FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_posts` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId) " +
            "AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, p.property_id = :propertyId) " +
            "ORDER BY Total ASC"
            , nativeQuery = true)
    Page<Post> getFollowPostIdByUserPagingOrderByPriceAsc(int userId, int roleId, int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2  FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_posts` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId) " +
            "AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, p.property_id = :propertyId) " +
            "ORDER BY Total DESC"
            , nativeQuery = true)
    Page<Post> getFollowPostIdByUserPagingOrderByPriceDesc(int userId, int roleId, int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2  FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_posts` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId) " +
            "AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, p.property_id = :propertyId) " +
            "ORDER BY per_m2 ASC"
            , nativeQuery = true)
    Page<Post> getFollowPostIdByUserPagingOrderByPricePerSquareAsc(int userId, int roleId, int propertyId, String check, Pageable pageable);

    @Query(value = " SELECT *, IF(unit_id = 1, price, price * area) as Total, IF(unit_id = 2, price, price / area) as per_m2  FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_posts` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId) " +
            "AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, p.property_id = :propertyId) " +
            "ORDER BY per_m2 DESC"
            , nativeQuery = true)
    Page<Post> getFollowPostIdByUserPagingOrderByPricePerSquareDesc(int userId, int roleId, int propertyId, String check, Pageable pageable);


    @Query(value = " SELECT * FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_posts` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId)" +
            "AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, p.property_id = :propertyId)"
            , nativeQuery = true)
    Page<Post> getFollowPostIdByUserPaging(int userId, int roleId, int propertyId, String check, Pageable pageable);


    @Query(value = " SELECT * FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_posts` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId) " +
            "AND IF(:check IS NULL OR :propertyId = 0, 1 = 1, p.property_id = :propertyId)"
            , nativeQuery = true)
    List<Post> getAllFollowPostIdByUser(int userId, int roleId, int propertyId, String check);

    @Query(value = " (SELECT * FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_posts` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId)) " , nativeQuery = true)
    List<Post> getFollowPostIdByUser(int userId, int roleId);


    @Query(value = " SELECT * FROM `posts` p " +
            "WHERE (p.post_id IN " +
            "      (SELECT post_id FROM `apartments` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_houses` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_lands`)) " +
            "AND p.ward LIKE CONCAT('%',:ward,'%')" +
            "AND p.district LIKE CONCAT('%',:district,'%') " +
            "AND p.province LIKE CONCAT('%',:province,'%') " +
            "AND IF(:minPrice = 0 AND :maxPrice = 0, p.unit_id = 3, p.price BETWEEN :minPrice AND :maxPrice) " +
            "AND (p.area BETWEEN :minArea AND :maxArea) "+
            "AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%'))) "+
            "AND (p.property_id IN :propertyType) " +
            "AND IF(:check IS NULL, 1 = 1, p.direction_id IN :listDirections) ", nativeQuery = true)
    Page<Post> searchPosts(String ward, String district, String province,
                           Long minPrice, Long maxPrice, float minArea, float maxArea,
                           List<Integer> propertyType, String keyword, String check,
                           List<Integer> listDirections, int bedroom, Pageable pageable);


    Post findPostByPostId(int postId);

    @Query(value = " SELECT * FROM `posts` p " +
            "WHERE p.post_id in (SELECT post_id FROM `user_follow_post` u " +
            "WHERE u.user_id = :userId " +
            "AND u.role_id = :roleId) " +
            "LIMIT 3", nativeQuery = true)
    List<Post> getTop3FollowPost(int userId, int roleId);

    @Query(value = "delete from posts where post_id =:postId", nativeQuery = true)
    @Modifying
    void deleteByPostId(int postId);

    @Query(value = " SELECT *, IF(p.unit_id = 1, p.price, p.price * p.area) as Total, IF(p.unit_id = 2, p.price, p.price / p.area) as per_m2 FROM `posts` p " +
            "WHERE (p.post_id IN " +
            "      (SELECT post_id FROM `apartments` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_houses` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_lands`)) " +
            "AND p.ward LIKE CONCAT('%',:ward,'%')" +
            "AND p.district LIKE CONCAT('%',:district,'%') " +
            "AND p.province LIKE CONCAT('%',:province,'%') " +
            "AND IF(:minPrice = 0 AND :maxPrice = 0, p.unit_id = 3, p.price BETWEEN :minPrice AND :maxPrice) " +
            "AND (p.area BETWEEN :minArea AND :maxArea) "+
            "AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%'))) "+
            "AND (p.property_id IN :propertyType) " +
            "AND IF(:check IS NULL, 1 = 1, p.direction_id IN :listDirections) " +
            "ORDER BY Total ASC", nativeQuery = true)
    Page<Post> searchPostOrderByPriceAsc(String ward, String district, String province,
                           Long minPrice, Long maxPrice, float minArea, float maxArea,
                           List<Integer> propertyType, String keyword, String check,
                           List<Integer> listDirections, int bedroom, Pageable pageable);

    @Query(value = " SELECT *, IF(p.unit_id = 1, p.price, p.price * p.area) as Total, IF(p.unit_id = 2, p.price, p.price / p.area) as per_m2 FROM `posts` p " +
            "WHERE (p.post_id IN " +
            "      (SELECT post_id FROM `apartments` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_houses` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_lands`)) " +
            "AND p.ward LIKE CONCAT('%',:ward,'%')" +
            "AND p.district LIKE CONCAT('%',:district,'%') " +
            "AND p.province LIKE CONCAT('%',:province,'%') " +
            "AND IF(:minPrice = 0 AND :maxPrice = 0, p.unit_id = 3, p.price BETWEEN :minPrice AND :maxPrice) " +
            "AND (p.area BETWEEN :minArea AND :maxArea) "+
            "AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%'))) "+
            "AND (p.property_id IN :propertyType) " +
            "AND IF(:check IS NULL, 1 = 1, p.direction_id IN :listDirections) " +
            "ORDER BY Total DESC", nativeQuery = true)
    Page<Post> searchPostOrderByPriceDesc(String ward, String district, String province,
                                         Long minPrice, Long maxPrice, float minArea, float maxArea,
                                         List<Integer> propertyType, String keyword, String check,
                                         List<Integer> listDirections, int bedroom, Pageable pageable);

    @Query(value = " SELECT *, IF(p.unit_id = 1, p.price, p.price * p.area) as Total, IF(p.unit_id = 2, p.price, p.price / p.area) as per_m2 FROM `posts` p " +
            "WHERE (p.post_id IN " +
            "      (SELECT post_id FROM `apartments` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_houses` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_lands`)) " +
            "AND p.ward LIKE CONCAT('%',:ward,'%')" +
            "AND p.district LIKE CONCAT('%',:district,'%') " +
            "AND p.province LIKE CONCAT('%',:province,'%') " +
            "AND IF(:minPrice = 0 AND :maxPrice = 0, p.unit_id = 3, p.price BETWEEN :minPrice AND :maxPrice) " +
            "AND (p.area BETWEEN :minArea AND :maxArea) "+
            "AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%'))) "+
            "AND (p.property_id IN :propertyType) " +
            "AND IF(:check IS NULL, 1 = 1, p.direction_id IN :listDirections) " +
            "ORDER BY per_m2 ASC", nativeQuery = true)
    Page<Post> searchPostOrderByPricePerSquareAsc(String ward, String district, String province,
                                         Long minPrice, Long maxPrice, float minArea, float maxArea,
                                         List<Integer> propertyType, String keyword, String check,
                                         List<Integer> listDirections, int bedroom, Pageable pageable);

    @Query(value = " SELECT *, IF(p.unit_id = 1, p.price, p.price * p.area) as Total, IF(p.unit_id = 2, p.price, p.price / p.area) as per_m2 FROM `posts` p " +
            "WHERE (p.post_id IN " +
            "      (SELECT post_id FROM `apartments` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_houses` " +
            "       WHERE number_of_bedroom >= :bedroom) " +
            "OR p.post_id IN " +
            "      (SELECT post_id FROM `residential_lands`)) " +
            "AND p.ward LIKE CONCAT('%',:ward,'%')" +
            "AND p.district LIKE CONCAT('%',:district,'%') " +
            "AND p.province LIKE CONCAT('%',:province,'%') " +
            "AND IF(:minPrice = 0 AND :maxPrice = 0, p.unit_id = 3, p.price BETWEEN :minPrice AND :maxPrice) " +
            "AND (p.area BETWEEN :minArea AND :maxArea) "+
            "AND ((p.title LIKE CONCAT('%',:keyword,'%')) OR (p.description LIKE CONCAT('%',:keyword,'%'))) "+
            "AND (p.property_id IN :propertyType) " +
            "AND IF(:check IS NULL, 1 = 1, p.direction_id IN :listDirections) " +
            "ORDER BY per_m2 DESC", nativeQuery = true)
    Page<Post> searchPostOrderByPricePerSquareDesc(String ward, String district, String province,
                                                  Long minPrice, Long maxPrice, float minArea, float maxArea,
                                                  List<Integer> propertyType, String keyword, String check,
                                                  List<Integer> listDirections, int bedroom, Pageable pageable);

    @Query(value = " SELECT * FROM posts " +
            " WHERE post_id IN (SELECT post_id FROM post_cares WHERE care_id = :careId) ", nativeQuery = true)
    List<Post> getListPostCare(int careId);
}
