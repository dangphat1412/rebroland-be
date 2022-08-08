package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Size;
import java.sql.Date;

@Data
public class UserCareDTO {
    private int careId;

    private Integer userCaredId;

    private Date startDate;

    @Size(max = 200)
    private String summarize;


    private boolean status;

    private Integer postId;

    private UserDTO userCared;

//    private UserDTO user;
//
//    private Set<ShortPostDTO> posts;
//
//    private Set<UserCareDetailDTO> userCareDetails;
}
