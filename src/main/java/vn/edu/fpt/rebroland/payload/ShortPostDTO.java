package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.Set;

@Data
public class ShortPostDTO {
    private int postId;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 200)
    private String title;

    private Date startDate;

    private Integer originalPost;

//    private UserDTO user;

    private String thumbnail;

}
