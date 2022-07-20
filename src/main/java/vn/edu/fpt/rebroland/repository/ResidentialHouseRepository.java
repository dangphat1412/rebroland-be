package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.rebroland.entity.ResidentialHouse;

public interface ResidentialHouseRepository extends JpaRepository<ResidentialHouse, Integer> {
    @Query(value = "select * from residential_houses  where post_id = :postId", nativeQuery = true)
    ResidentialHouse findByPostId(int postId);



}
