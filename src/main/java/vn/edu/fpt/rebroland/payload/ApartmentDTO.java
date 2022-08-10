package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ApartmentDTO extends  RealEstatePostDTO{
    private int id;


    @Min(value = 0)
    @Max(value = 10)
    private int numberOfBedroom;

    @Min(value = 0)
    @Max(value = 10)
    private int numberOfBathroom;



    @Min(value = 0)
    @Max(value = 100)
    private Integer floorNumber;



    @Size(max = 10)
    private String roomNumber;

    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
    private String barcode;
    @Size(max = 50)
    private String buildingName;

    @Size(max = 50)
    private String owner;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String ownerPhone;

    @Min(value = 0)
    @Max(value = 10000)
    private int plotNumber;

}
