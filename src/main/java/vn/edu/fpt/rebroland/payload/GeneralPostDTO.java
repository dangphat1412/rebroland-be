package vn.edu.fpt.rebroland.payload;

import vn.edu.fpt.rebroland.entity.Coordinate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class GeneralPostDTO {
    // Post
    private int postId;

    @NotNull(message = "Cập nhập thông tin loại bất động sản này.")
    private int propertyTypeId;

    @NotNull(message = "Cập nhập thông tin loại giá tiền này.")
    private int unitPriceId;

    @NotNull(message = "Cập nhập thông tin tình trạng này.")
    private int statusId;

    private Integer directionId;

    private Integer longevityId;

    @NotEmpty(message = "Cập nhập tiêu đề.")
    @Size(max = 200)
    private String title;

    @NotEmpty(message = "Cập nhập mô tả.")
    @Size(max = 2000, message = "ký tự không quá 2000")
    private String description;

    @NotNull(message = "Cập nhập diện tích.")
    @Min(value = 0)
    @Max(value = 10000)
    private float area;

    @NotNull(message = "Cập nhập chứng chỉ.")
    private boolean certification;


    private String startDate;
    @NotNull(message = "số ngày đăng không trống")
    private int numberOfPostedDay;

    @NotNull(message = "Cập nhập giá cả.")
    @Min(value = 0)
    @Max(value = 1000000000000L)
    private Long price;

    @Size(max = 1000, message = "mô tả bổ sung không quá 1000 ký tự")
    private String additionalDescription;

    @NotEmpty(message = "Tên liên hệ không để trống")
    @Size(max = 50)
    private String contactName;

    @NotEmpty(message = "Số điện thoại liên hệ không để trống")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String contactPhone;

    @Size(max = 100, message = "địa chỉ liên hệ không quá 100 ký tự")
    private String contactAddress;

    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email không hợp lệ!")
    private String contactEmail;

    @NotEmpty(message = "phường xã không để trống")
    @Size(max = 20)
    private String ward;

    @NotEmpty(message = "quận huyện không để trống")
    @Size(max = 20)
    private String district;

    @NotEmpty(message = "tỉnh thành phố không để trống")
    @Size(max = 20)
    private String province;

    @Size(max = 100, message = "địa chỉ ký tự không quá 100")
    private String address;

    // Image DTO
    private List<String> images;

    // Coordinate DTO
    private List<Coordinate> coordinates;

    //Apartment & Residential House

    //    @NotNull(message = "number of bedroom is not empty")
    @Min(value = 0, message = "số phòng ngủ không âm")
    @Max(value = 50, message = "số phòng ngủ không quá 50")
    private int numberOfBedroom;
    //    @NotNull(message = "number of bathroom is not empty")
    @Min(value = 0, message = "số phòng tắm không âm")
    @Max(value = 50, message = "số phòng tắm không quá 50")
    private int numberOfBathroom;
    //    @NotNull(message = "floor number is not empty")
    @Min(value = 0, message = "tầng số không âm")
    @Max(value = 100, message = "tầng số không được quá 100")
    private Integer floorNumber;

    @Size(max = 20, message = "ký tự số phòng không quá 20")
    private String roomNumber;
    @Min(value = 0, message = "số tầng không âm")
    @Max(value = 100, message = "số tầng không quá 100")
    private int numberOfFloor;
    @Size(max = 100, message = "tên tòa nhà không quá 100")
    private String buildingName;

    // Residental Land & Residential House

    @Size(max = 20)
    private String plotNumber;

    @Min(value = 0, message = "diện tích mặt tiền không âm")
    @Max(value = 1000, message = "diện tích mặt tiền không quá 1000")
    private float frontispiece;

    // barcode
    @Pattern(regexp = "(^\\d{13}$)|(^\\d{15}$)", message = "Mã vạch chỉ nhập số 13 hoặc 15 kí tự")
    private String barcode;


    // History
    @Size(max = 50, message = "ký tự không quá 50")
    private String owner;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String ownerPhone;


}
