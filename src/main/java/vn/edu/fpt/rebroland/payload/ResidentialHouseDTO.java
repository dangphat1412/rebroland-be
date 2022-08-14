package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ResidentialHouseDTO extends RealEstatePostDTO{
    private int id;

    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
    private String barcode;

    @Min(value = 1,message = "Số thửa tối thiểu là 1")
    @Max(value = 99999, message = "Số thửa tối đa là 99999")
    private Integer plotNumber;


    @Min(value = 0)
    @Max(value = 10)
    private int numberOfBedroom;


    @Min(value = 0)
    @Max(value = 10)
    private int numberOfBathroom;


    @Min(value = 0)
    @Max(value = 100)
    private int numberOfFloor;


    @Min(value = 0)
    @Max(value = 1000)
    private float frontispiece;

    @Size(max = 50)
    private String owner;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String ownerPhone;
}
