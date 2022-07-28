package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.Set;

@Data
public class DerivativeDTO {
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

    private Date startDate;

    @NotNull(message = "Cập nhập thông tin này.")
    @Min(value = 0)
    @Max(value = 1000000000000L)
    private Long price;

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

    private PropertyTypeDTO propertyType;

    private UserDTO user;

    private UnitPriceDTO unitPrice;

    private StatusDTO status;

}
