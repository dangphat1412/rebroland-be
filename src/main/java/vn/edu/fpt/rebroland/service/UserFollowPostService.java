package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.UserFollowPost;
import vn.edu.fpt.rebroland.payload.DerivativeDTO;
import vn.edu.fpt.rebroland.payload.SearchDTO;
import vn.edu.fpt.rebroland.payload.SearchResponse;
import vn.edu.fpt.rebroland.payload.ShortPostDTO;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface UserFollowPostService {
    HttpStatus createUserFollowPost(String postId, String phone);

    SearchResponse getFollowPostByUserPaging(String phone, String propertyId, int pageNo, int pageSize, String option);

    List<DerivativeDTO> getFollowPostByUser(String phone, String propertyId);

    List<ShortPostDTO> getShortFollowPostByUser(String phone);

    void deleteFollowByPostId(int postId);

    List<SearchDTO> getTop3FollowPost(String phone);
}
