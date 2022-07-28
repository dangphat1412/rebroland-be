package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.ResidentialHouseHistory;
import vn.edu.fpt.rebroland.payload.PostDTO;
import vn.edu.fpt.rebroland.payload.SearchResponse;
import vn.edu.fpt.rebroland.payload.*;
import org.springframework.data.domain.Pageable;


import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface PostService {
    PostDTO createPost(PostDTO postDTO, int userId, Integer directionId, int propertyTypeId,
                       int unitId, int statusId, Integer longevityId);

    SearchResponse getPostByUserId(int pageNo, int pageSize, int userId, String propertyId, String sortValue);

    SearchResponse getPostByUserId(int pageNo, int pageSize, int userId, String propertyId);

    SearchResponse getAllPostForBroker(int pageNo, int pageSize, String propertyId, String option);


    SearchResponse searchPosts(String ward, String district, String province, String minPrice, String maxPrice,
                           String minArea, String maxArea, List<String> propertyType, String keyword,
                           List<String> direction, int bedroom, int pageNo, int pageSize, String sortValue);

    List<PostDTO> getPostByPropertyTypeId(int propertyTypeId);

    PostDTO getPostByPostId(int postId);

    PostDTO updatePost(PostDTO postDTO, int postId, int userId,
                       Integer directionId, int propertyTypeId,
                       int unitId, int statusId, Integer longevityId);

    void deletePost(int postId);

    SearchResponse getAllPost(int pageNo, int pageSize);

    Map<String, List> getRealEstateHistory(int postId);

    List<DerivativeDTO> getAllDerivativePostPaging(int pageNo, int pageSize);

    List<DerivativeDTO> getAllDerivativePost();

    List<DerivativeDTO> getDerivativePostByUserIdPaging(int userId, String propertyTypeId, int pageNumber, int pageSize, String sortValue);

    SearchResponse getDerivativePostOfBrokerPaging(int userId, String propertyTypeId, int pageNumber, int pageSize, String sortValue);


    List<DerivativeDTO> getDerivativePostByUserId(int userId);

    PostDTO setDataToPostDTO(GeneralPostDTO generalPostDTO, int userId, Date date);

    ResidentialHouseDTO setDataToResidentialHouse(GeneralPostDTO generalPostDTO);

    ResidentialHouseHistoryDTO setDataToResidentialHouseHistory(GeneralPostDTO generalPostDTO, ResidentialHouseDTO residentialHouse, String date);

    ApartmentDTO setDataToApartment(GeneralPostDTO generalPostDTO);

    ApartmentHistoryDTO setDataToApartmentHistory(GeneralPostDTO generalPostDTO, ApartmentDTO apartmentDTO, String date);

    ResidentialLandDTO setDataToResidentialLand(GeneralPostDTO generalPostDTO);

    ResidentialLandHistoryDTO setDataToResidentialLandHistory(GeneralPostDTO generalPostDTO, ResidentialLandDTO residentialLandDTO, String date);

    void setDataToRealEstateDTO(RealEstatePostDTO realEstatePostDTO, PostDTO postDTO, int postId);

}
