package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.ResidentialLand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ResidentialLandRepository extends JpaRepository<ResidentialLand, Integer> {
    @Query(value = "select * from residential_lands  where post_id = :postId", nativeQuery = true)
    ResidentialLand findByPostId(int postId);

    @Query(value = "delete from residential_lands  where post_id = :postId", nativeQuery = true)
    @Modifying
    void deleteByPostId(int postId);


}
