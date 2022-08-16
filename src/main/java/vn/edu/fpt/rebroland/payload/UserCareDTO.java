package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Size;
import java.sql.Date;

@Data
public class UserCareDTO {
    private int careId;

    private Integer userCaredId;

    private Date startDate;

    @Size(max = 200,message = "Phần mô tả không quá 200 ký tự")
    private String summarize;


    private boolean status;

    private Integer postId;

    private UserDTO user;

//    private UserDTO user;
//
//    private Set<ShortPostDTO> posts;
//
//    private Set<UserCareDetailDTO> userCareDetails;
}
