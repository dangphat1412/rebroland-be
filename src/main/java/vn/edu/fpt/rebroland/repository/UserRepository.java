package vn.edu.fpt.rebroland.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.rebroland.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhone(String phone);
    Boolean existsByPhone(String phone);

    @Query(value = " SELECT * FROM users " +
            " WHERE id IN (SELECT user_id FROM user_roles " +
            " WHERE role_id = 3) ", nativeQuery = true)
    Page<User> getAllBroker(Pageable pageable);

    @Query(value = " SELECT * FROM users " +
            " WHERE ", nativeQuery = true)
    Page<User> searchBroker(String fullName, String ward, String district, String province,
                            String address, Pageable pageable);



}
