package vn.edu.fpt.rebroland.payload;


import lombok.Data;

import javax.validation.constraints.*;
import java.util.Set;

@Data
public class RealEstatePostDTO {
    private int postId;

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

    @Min(value = 0)
    @Max(value = 1000000000000L)
    private Long price;
    @Size(max = 1000)
    private String additionalDescription;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 50)
    private String contactName;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    private String contactPhone;

    @Size(max = 100)
    private String contactAddress;

    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email không hợp lệ.")
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

    private String thumbnail;

    private Integer originalPost;

    private UserDTO user;
    private PropertyTypeDTO propertyType;
    private DirectionDTO direction;

    private UnitPriceDTO unitPrice;

    private LongevityDTO longevity;

    private StatusDTO status;

    private Set<ImageDTO> images;

    private Set<CoordinateDTO> coordinates;


}

