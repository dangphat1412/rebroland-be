package vn.edu.fpt.rebroland.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class ResidentialHouseHistoryDTO {
    private int id;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(min = 13,max = 15,message = "Độ dài từ 13 hoặc 15 ký tự.")
    @Pattern(regexp = "[0-9]+", message = "Mã vạch chỉ nhập số")
    private String barcode;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 20)
    private String plotNumber;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String owner;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String startDate;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "số điện thoại không hợp lệ")
    private String phone;
}
