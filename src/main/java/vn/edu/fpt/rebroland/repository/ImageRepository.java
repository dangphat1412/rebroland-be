package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.rebroland.entity.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query(value = "select image_link from images  where post_id = :postId", nativeQuery = true)
    List<String> findImageLinkByPostId(int postId);

    @Query(value = "select * from images  where post_id = :postId", nativeQuery = true)
    List<Image> findImageByPostId(int postId);

    @Query(value = "delete from images where post_id =:postId", nativeQuery = true)
    @Modifying
    void deleteByPost(int postId);
}