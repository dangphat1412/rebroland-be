package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class HistoryDTO {
    private int typeId;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String owner;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "Số điện thoại không hợp lệ.")
    private String phone;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
    private String barcode;

    @Min(value = 0)
    @Max(value = 10000)
    private int plotNumber;

    @Size(max = 50)
    private String buildingName;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 10)
    private String roomNumber;

    private List<String> images;

}
