package vn.edu.fpt.rebroland.service;

import org.springframework.http.HttpStatus;
import vn.edu.fpt.rebroland.payload.DerivativeDTO;
import vn.edu.fpt.rebroland.payload.SearchDTO;
import vn.edu.fpt.rebroland.payload.ShortPostDTO;

import java.util.List;

public interface UserFollowPostService {
    HttpStatus createUserFollowPost(String postId, String phone);

    List<DerivativeDTO> getFollowPostByUserPaging(String phone, String propertyId, int pageNo, int pageSize);

    List<DerivativeDTO> getFollowPostByUser(String phone);

    List<ShortPostDTO> getShortFollowPostByUser(String phone);

    List<SearchDTO> getTop3FollowPost(String phone);
}
