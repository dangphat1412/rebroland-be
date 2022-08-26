package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class HistoryDTO {
    private int typeId;

    private boolean provideInfo ;

//    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String owner;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b",message = "Số điện thoại không hợp lệ.")
    private String phone;

//    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
    private String barcode;

    @Min(value = 1,message = "Số thửa tối thiểu là 1")
    @Max(value = 99999, message = "Số thửa tối đa là 99999")
    private Integer plotNumber;

    @Size(max = 50)
    private String buildingName;

//    @NotEmpty(message = "Cập nhập thông tin này.")
//    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Số phòng không chứa kí tự đặc biệt!")
    @Size(max = 10)
    private String roomNumber;

    private List<String> images;

    private String token;

}
