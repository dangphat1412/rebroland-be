package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.rebroland.entity.Apartment;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {
    @Query(value = "select * from apartments  where post_id = :postId", nativeQuery = true)
    Apartment findByPostId(int postId);

}
