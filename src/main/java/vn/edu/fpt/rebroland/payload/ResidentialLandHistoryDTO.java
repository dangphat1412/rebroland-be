package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ResidentialLandHistoryDTO {
    private int id;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
    private String barcode;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 20)
    private String plotNumber;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String owner;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String startDate;
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "Điện thoại không hợp lệ")
    private String phone;

}
