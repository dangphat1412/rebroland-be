package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Image;
import vn.edu.fpt.rebroland.payload.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    String createImage(List<String> imageDTO, int postId);


    List<String> getImageByPostId(int postId);

    void deleteImageByPostId(int postId);

    String updateImage(List<String> imageLink, int postId);

//    String upload(String phone, MultipartFile file);
//
//    String uploadAvatar(String phone, MultipartFile file);
}
