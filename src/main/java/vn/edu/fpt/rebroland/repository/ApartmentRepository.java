package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Apartment;
import vn.edu.fpt.rebroland.entity.ResidentialHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {
    @Query(value = "select * from apartments  where post_id = :postId", nativeQuery = true)
    Apartment findByPostId(int postId);

    @Query(value = "delete from apartments  where post_id = :postId", nativeQuery = true)
    @Modifying
    void deleteByPostId(int postId);
}
