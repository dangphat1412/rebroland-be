package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Size;
import java.sql.Date;

@Data
public class UserCareDTO {
    private int careId;

    private Integer userCaredId;

    @JsonFormat(pattern = "dd-MM-yyyy")
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
