package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class ResetPasswordDTO {
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$",
            message = "Mật khẩu chứa ít nhất 8 kí tự, gồm chữ hoa, chữ thường và số ")
    private String password;
//    @NotNull
    private int token;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "Số điện thoại không hợp lệ.")
    private String phone;
}
