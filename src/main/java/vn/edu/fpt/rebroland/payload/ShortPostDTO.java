package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class ShortPostDTO {
    private int postId;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 200)
    private String title;

    private Date startDate;

    private Integer originalPost;

    private UserDTO user;

    private String thumbnail;

}
