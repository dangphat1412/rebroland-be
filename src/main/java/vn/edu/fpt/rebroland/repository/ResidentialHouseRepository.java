package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.ResidentialHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ResidentialHouseRepository extends JpaRepository<ResidentialHouse, Integer> {
    @Query(value = "select * from residential_houses  where post_id = :postId", nativeQuery = true)
    ResidentialHouse findByPostId(int postId);

    @Query(value = "delete from residential_houses  where post_id = :postId", nativeQuery = true)
    @Modifying
    void deleteByPostId(int postId);



}
