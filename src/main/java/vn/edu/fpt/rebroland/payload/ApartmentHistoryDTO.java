package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ApartmentHistoryDTO {

    private int id;
    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
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
    @Min(value = 1,message = "Số thửa tối thiểu là 1")
    @Max(value = 99999, message = "Số thửa tối đa là 99999")
    private int plotNumber;
}
