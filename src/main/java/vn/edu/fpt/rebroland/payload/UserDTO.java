package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class UserDTO {
    private int id;

//    @NotEmpty(message = "Vui lòng điền đầy đủ họ tên")
    private String fullName;

//    @NotNull(message = "Please provide a date.")
    private String dob;

//    @NotNull(message = "You must choose gender")
    private Boolean gender;

    private String address;

//    @NotEmpty(message = "Ward should not be empty")
    private String ward;

//    @NotEmpty(message = "District should not be empty")
    private String district;

//    @NotEmpty(message = "City should not be empty")
    private String province;

//    @NotEmpty(message = "Vui lòng điền số điện thoại")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "Số điện thoại không hợp lệ.")
    private String phone;

    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email không hợp lệ.")
    private String email;

    private String avatar;

    private int currentRole;

    private String description;

    private boolean block;

    private int unreadNotification;
}
