package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    @Query(value = "select * from `contacts` where user_id =:userId", nativeQuery = true)
    Page<Contact> getContactByUserId(Pageable pageable, int userId);

    @Query(value = "select * from `contacts` where user_id =:userId", nativeQuery = true)
    List<Contact> getContactsByUserId(int userId);

    @Query(value = "delete from contacts  where post_id =:postId", nativeQuery = true)
    @Modifying
    void deleteContactByPostId(int postId);
}
