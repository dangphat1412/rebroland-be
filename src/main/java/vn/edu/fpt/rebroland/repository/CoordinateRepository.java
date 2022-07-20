package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.rebroland.entity.Coordinate;

import java.util.List;

public interface CoordinateRepository extends JpaRepository<Coordinate, Integer> {
    @Query(value = "select * from coordinates  where post_id = :postId", nativeQuery = true)
    List<Coordinate> findByPostId(int postId);

    @Query(value = "delete from coordinates where post_id =:postId", nativeQuery = true)
    @Modifying
    void deleteByPost(int postId);
}
