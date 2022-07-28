package vn.edu.fpt.rebroland.payload;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class LoginDTO {
    @NotEmpty(message = "Vui lòng điền số điện thoại.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "Số điện thoại không hợp lệ.")
    private String phone;

    @NotEmpty(message = "Vui lòng điền mật khẩu.")
    private String password;

}
