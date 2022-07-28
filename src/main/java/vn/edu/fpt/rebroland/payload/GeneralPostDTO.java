package vn.edu.fpt.rebroland.payload;

import vn.edu.fpt.rebroland.entity.Coordinate;
import vn.edu.fpt.rebroland.entity.Image;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class GeneralPostDTO {
    // Post
    private int postId;

    @NotNull(message = "Cập nhập thông tin này.")
    private int propertyTypeId;

    @NotNull(message = "Cập nhập thông tin này.")
    private int unitPriceId;

    @NotNull(message = "Cập nhập thông tin này.")
    private int statusId;

    private Integer directionId;

    private Integer longevityId;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 200)
    private String title;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 2000)
    private String description;

    @NotNull(message = "Cập nhập thông tin này.")
    @Min(value = 0)
    @Max(value = 10000)
    private float area;

    @NotNull(message = "Cập nhập thông tin này.")
    private boolean certification;


    private String startDate;

    @NotNull(message = "Cập nhập thông tin này.")
    @Min(value = 0)
    @Max(value = 1000000000000L)
    private Long price;

    @Size(max = 1000)
    private String additionalDescription;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String contactName;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String contactPhone;

    @Size(max = 100)
    private String contactAddress;

    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email không hợp lệ!")
    private String contactEmail;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 20)
    private String ward;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 20)
    private String district;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 20)
    private String province;

    @Size(max = 100)
    private String address;

    // Image DTO
    private List<String> images;

    // Coordinate DTO
    private List<Coordinate> coordinates;

    //Apartment & Residential House

    //    @NotNull(message = "number of bedroom is not empty")
    @Min(value = 0)
    @Max(value = 10)
    private int numberOfBedroom;
    //    @NotNull(message = "number of bathroom is not empty")
    @Min(value = 0)
    @Max(value = 10)
    private int numberOfBathroom;
    //    @NotNull(message = "floor number is not empty")
    @Min(value = 0)
    @Max(value = 100)
    private Integer floorNumber;

    @Size(max = 10)
    private String roomNumber;
    @Min(value = 0)
    @Max(value = 100)
    private int numberOfFloor;
    @Size(max = 20)
    private String buildingName;

    // Residental Land & Residential House

    @Size(max = 20)
    private String plotNumber;

    @Min(value = 0)
    @Max(value = 1000)
    private float frontispiece;

    // barcode

    @Size(min = 13,max = 15,message = "Độ dài từ 13 hoặc 15 ký tự.")
    @Pattern(regexp = "[0-9]+", message = "Mã vạch chỉ nhập số")
    private String barcode;


    // History
    @Size(max = 50)
    private String owner;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String ownerPhone;


}
