package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.rebroland.entity.ResidentialLand;

public interface ResidentialLandRepository extends JpaRepository<ResidentialLand, Integer> {
    @Query(value = "select * from residential_lands  where post_id = :postId", nativeQuery = true)
    ResidentialLand findByPostId(int postId);




}
