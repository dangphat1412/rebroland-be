package vn.edu.fpt.rebroland.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.rebroland.entity.Image;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.ImageDTO;
import vn.edu.fpt.rebroland.repository.ImageRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ImageService;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private ModelMapper mapper;

    private ImageRepository imageRepository;

    private PostRepository postRepository;

    private UserRepository userRepository;


    public ImageServiceImpl(ModelMapper mapper, ImageRepository imageRepository, PostRepository postRepository,
                            UserRepository userRepository) {
        this.mapper = mapper;
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Cloudinary cloudinaryConfig() {
        Map config = new HashMap();
        config.put("cloud_name", "widdel2000");
        config.put("api_key", "675259543629689");
        config.put("api_secret", "jPoBhnoBvZKmYx3YwNDjXlUcUG0");
        Cloudinary cloudinary = new Cloudinary(config);
        return cloudinary;
    }

    //create images for post
    @Override
//    @Transactional
    public String createImage(List<String> links, int postId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
            for (String link : links) {
                Image image = new Image();
                try {
                    image.setImage(link);
                    image.setPost(post);
                    imageRepository.save(image);
                } catch (Exception ex) {
                    return null;
                }
            }
            return "Insert successfully Image !";
        } catch (Exception e) {
            return "Insert failed Image !";
        }
    }


    //upload image to cloudinary
    public String upload(String phone, MultipartFile file) {
        Cloudinary cloudinary = cloudinaryConfig();
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        if (user != null) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String publicId = uploadResult.get("public_id").toString() + ".jpg";

                return publicId;
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    //upload avatar for user
    public String uploadAvatar(String phone, MultipartFile file) {
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        if (user != null) {
            String publicId = upload(phone, file);
            user.setAvatar(publicId);
            userRepository.save(user);
            return publicId;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getImageByPostId(int postId) {
        List<String> imageList = imageRepository.findImageLinkByPostId(postId);
        return imageList;
    }

    @Override
    public void deleteImageByPostId(int postId) {
        try {
            imageRepository.deleteByPost(postId);
        } catch (Exception e) {

        }
//        imageRepository.deleteByPostId(postId);

    }

    @Override
    public String updateImage(List<String> imageLink, int postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
//        List<Image> images = imageRepository.findImageByPostId(postId);
//        for (Image image : images) {
//            imageRepository.delete(image);
//        }
        imageRepository.deleteByPost(postId);
        try {

            for (String link : imageLink) {
                Image image = new Image();
                image.setImage(link);
                image.setPost(post);
                imageRepository.save(image);
            }
            return "update image success!";
        } catch (Exception e) {
            return "update fail";
        }

    }

    private ImageDTO mapToDTO(Image image) {
        ImageDTO imageDTO = mapper.map(image, ImageDTO.class);
        return imageDTO;
    }

    private Image mapToEntity(ImageDTO imageDTO) throws ParseException {
        Image image = mapper.map(imageDTO, Image.class);
        return image;
    }
}
