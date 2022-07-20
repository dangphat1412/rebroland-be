package vn.edu.fpt.rebroland.service;

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
