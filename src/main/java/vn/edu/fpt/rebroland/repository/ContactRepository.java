package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
//    @Query(value = "select * from `contacts` where user_id =:userId" +
//            " AND user_request_id IN (SELECT user_id FROM users " +
//            "                         WHERE ((phone LIKE CONCAT('%',:keyword,'%')) OR (full_name LIKE CONCAT('%',:keyword,'%')) OR user_id = :keyword)) ", nativeQuery = true)
//    Page<Contact> getContactByUserId(Pageable pageable, int userId, String keyword);

    @Query(value = "select c.* from `contacts` c " +
            "JOIN `users` u ON c.user_request_id = u.id " +
            "where c.user_id = :userId" +
            " AND ((u.phone LIKE CONCAT('%',:keyword,'%')) OR (u.full_name LIKE CONCAT('%',:keyword,'%'))) " +
            " ORDER BY c.start_date DESC ", nativeQuery = true)
    Page<Contact> getContactByUserId(Pageable pageable, int userId, String keyword);


    @Query(value = "select * from `contacts` where user_id =:userId", nativeQuery = true)
    List<Contact> getContactsByUserId(int userId);

    @Query(value = "delete from contacts  where post_id =:postId", nativeQuery = true)
    @Modifying
    void deleteContactByPostId(int postId);
}
