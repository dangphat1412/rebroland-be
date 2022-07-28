package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class ChangePasswordDTO {
    @NotEmpty(message = "Mật khẩu cũ không được để trống!")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$",
            message = "Mật khẩu chứa ít nhất 8 kí tự, gồm chữ hoa, chữ thường và số ")
    private String oldPassword;

    @NotEmpty(message = "Mật khẩu mới không được để trống!")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$",
            message = "Mật khẩu chứa ít nhất 8 kí tự, gồm chữ hoa, chữ thường và số ")
    private String newPassword;
}
