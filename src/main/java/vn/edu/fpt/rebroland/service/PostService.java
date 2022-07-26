package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.payload.PostDTO;
import vn.edu.fpt.rebroland.payload.SearchResponse;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface PostService {
    PostDTO createPost(PostDTO postDTO, int userId, Integer directionId, int propertyTypeId,
                       int unitId, int statusId, Integer longevityId);

    SearchResponse getPostByUserId(int pageNo, int pageSize, int userId, String propertyId, String sortValue, String status);

    SearchResponse getAllPostByUserId(int pageNo, int pageSize, int userId, String propertyId, String sortValue);

    SearchResponse getPostByUserId(int pageNo, int pageSize, int userId, String propertyId);

    SearchResponse getAllPostForBroker(int pageNo, int pageSize, String option);

    SearchResponse getAllPostByUserId(int pageNo, int pageSize, int userId);

    void blockAllPostByUserId(int userId);

    List<BrokerInfoOfPostDTO> getDerivativePostOfOriginalPost(int originalPostId);

    SearchResponse searchPosts(String ward, String district, String province, String minPrice, String maxPrice,
                               String minArea, String maxArea, List<String> propertyType, String keyword,
                               List<String> direction, int bedroom, int pageNo, int pageSize, String sortValue);

    SearchResponse searchOriginalPosts(String ward, String district, String province, String minPrice, String maxPrice,
                                       String minArea, String maxArea, List<String> propertyType, String keyword,
                                       List<String> direction, int bedroom, int pageNo, int pageSize, String sortValue, int userId);


    List<PostDTO> getPostByPropertyTypeId(int propertyTypeId);

    PostDTO getPostByPostId(int postId);

    PostDTO getActiveOrFinishPostById(int postId);

    PostDTO findPostByPostId(int postId);

    PostDTO getAllPostByPostId(int postId);

    PostDTO getDerivativePostByPostId(int postId);

    PostDTO getDerivativePostOfUser(int userId, int postId);

    void deletePost(int postId);

    SearchResponse getAllPost(int pageNo, int pageSize);

    SearchResponse getAllPost(int pageNo, int pageSize, String keyword, String sortValue);

    SearchResponse getExpiredPostByUserId(int userId, int pageNo, int pageSize);

    Map<String, List> getRealEstateHistory(int postId);

    List<DerivativeDTO> getAllDerivativePostPaging(int pageNo, int pageSize, String sortValue);

    List<DerivativeDTO> getAllDerivativePost();

    List<DerivativeDTO> getDerivativePostByUserIdPaging(int userId, String propertyTypeId, int pageNumber, int pageSize, String sortValue);

    SearchResponse getDerivativePostOfBrokerPaging(int userId, String propertyTypeId, int pageNumber, int pageSize, String sortValue, String status);


    List<DerivativeDTO> getDerivativePostByUserId(int userId);

    PostDTO setDataToPostDTO(GeneralPostDTO generalPostDTO, int userId, Date date, boolean check);

    PostDTO updatePost(GeneralPostDTO generalPostDTO, Post post, int userId,
                          List<String> imageLink);

    ResidentialHouseDTO setDataToResidentialHouse(GeneralPostDTO generalPostDTO);

    ResidentialHouseHistoryDTO setDataToResidentialHouseHistory(GeneralPostDTO generalPostDTO, ResidentialHouseDTO residentialHouse, String date);

    ApartmentDTO setDataToApartment(GeneralPostDTO generalPostDTO);

    ApartmentHistoryDTO setDataToApartmentHistory(GeneralPostDTO generalPostDTO, ApartmentDTO apartmentDTO, String date);

    ResidentialLandDTO setDataToResidentialLand(GeneralPostDTO generalPostDTO);

    ResidentialLandHistoryDTO setDataToResidentialLandHistory(GeneralPostDTO generalPostDTO, ResidentialLandDTO residentialLandDTO, String date);

    void setDataToRealEstateDTO(RealEstatePostDTO realEstatePostDTO, PostDTO postDTO, int postId);

    int changeStatusOfPost(int postId);

    Map<String, Integer> getNumberOfPropertyType();

    Map<String, Integer> getNumberOfPropertyTypeForBroker(int userId);

    void extendPost(int postId, int numberOfPostedDay, Long totalPayment);

    void changeStatus(int postId, int statusId);

    void changeStatusOfDerivativePostOfPost(int postId);

    SearchResponse getAllOriginalPostByUserId(int userId, int pageNo, int pageSize, String sortValue, String propertyType);

    SearchResponse getOriginalPostByUserId(int userId, int pageNo, int pageSize, String sortValue, String propertyType);

    List<SearchDTO> getOutstandingPost();
}
