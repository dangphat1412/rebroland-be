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
    private int floorNumber;



    @Size(max = 10)
    private String roomNumber;

    @Size(min = 13,max = 15,message = "Độ dài từ 13 hoặc 15 ký tự.")
    @Pattern(regexp = "[0-9]+", message = "Mã vạch chỉ nhập số")
    private String barcode;
    @Size(max = 50)
    private String buildingName;

}
