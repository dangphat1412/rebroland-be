package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ResidentialLandDTO extends RealEstatePostDTO {
    private int id;


    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
    private String barcode;
    @Size(max = 20)
    private String plotNumber;


    @Min(value = 0)
    @Max(value = 1000)
    private float frontispiece;

    @Size(max = 50)
    private String owner;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String ownerPhone;

}
