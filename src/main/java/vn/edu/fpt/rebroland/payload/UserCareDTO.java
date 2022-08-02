package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;
import java.sql.Date;

@Data
public class UserCareDTO {
    private int careId;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String fullName;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    private String phone;

    private Date startDate;

    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email không hợp lệ!")
    private String email;

    @Size(max = 200)
    private String summarize;

    @NotNull(message = "Cập nhập thông tin này.")
    private boolean status;

    private Integer postId;

//    private UserDTO user;
//
//    private Set<ShortPostDTO> posts;
//
//    private Set<UserCareDetailDTO> userCareDetails;
}
