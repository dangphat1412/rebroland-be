package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Date;

@Data
public class ContactDTO {
    private int contactId;

    private int userRole;

    @NotEmpty(message = "cap nhap thong tin nay")
    @Size(max = 50)
    private String fullName;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    private String phone;

    private Date startDate;
    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email không hợp lệ!")

    private String email;

    @NotEmpty(message = "cap nhap thong tin nay")
    @Size(max = 200)
    private String content;

    private boolean unread;

    private UserDTO user;
    private ShortPostDTO shortPost;

}
