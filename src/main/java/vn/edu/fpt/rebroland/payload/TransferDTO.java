package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class TransferDTO {
    @NotEmpty(message = "Số điện thoại không được để trống !")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ!")
    private String phone;

    //    @NotEmpty(message = "OTP không được để trống!")
//    @Min(value = 6, message = "OTP phải có 6 chữ số !")
    private String token;

    @Min(value = 1)
    private long amount;

    @NotEmpty(message = "Nội dung không được để trống!")
    private String content;


}
