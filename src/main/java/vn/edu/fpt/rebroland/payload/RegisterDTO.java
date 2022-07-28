package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RegisterDTO {
    @NotEmpty(message = "Tên không được để trống !")
    @Pattern(regexp = "^\\p{L}+[\\p{L}\\p{Pd}\\p{Zs}']*\\p{L}+$|^\\p{L}+$", message = "Tên người dùng không hợp lệ!")
    private String fullName;

    @NotEmpty(message = "Số điện thoại không được để trống !")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ!")
    private String phone;

    @NotEmpty(message = "Mật khẩu không được để trống !")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$",
            message = "Mật khẩu chứa ít nhất 8 kí tự, gồm chữ hoa, chữ thường và số ")
    private String password;

//    @NotEmpty(message = "OTP không được để trống!")
//    @Min(value = 6, message = "OTP phải có 6 chữ số !")
    private String token;
}
