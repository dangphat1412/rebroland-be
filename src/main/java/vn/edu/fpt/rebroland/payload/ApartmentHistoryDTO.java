package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ApartmentHistoryDTO {

    private int id;
    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(min = 13,max = 15,message = "Độ dài từ 13 hoặc 15 ký tự.")
    @Pattern(regexp = "[0-9]+", message = "Mã vạch chỉ nhập số")
    private String barcode;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String owner;
    @NotEmpty(message = "Cập nhập thông tin này.")
    private String startDate;
    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "Số điện thoại không hợp lệ.")
    private String phone;
    @Size(max = 50)
    private String buildingName;
    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 10)
    private String roomNumber;
    @Min(value = 0)
    @Max(value = 10000)
    private int plotNumber;
}
