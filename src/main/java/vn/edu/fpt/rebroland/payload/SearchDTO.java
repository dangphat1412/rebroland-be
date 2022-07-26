package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class SearchDTO {
    private int postId;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 200)
    private String title;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 2000)
    private String description;

    private String startDate;

    @NotNull(message = "Cập nhập thông tin này.")
    @Min(value = 0)
    @Max(value = 10000)
    private float area;

    @Min(value = 0)
    @Max(value = 1000000000000L)
    private long price;

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

    private boolean allowDerivative;

    private StatusDTO status;

    private UnitPriceDTO unitPrice;

    private int directionId;

    private UserDTO user;

    private int numberOfBedroom;

    private int numberOfBathroom;

    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
    private Date endDate;

    private boolean block;

    private int numberOfUserReport;

    private int reportStatus;

    private int reportId;

    private String comment;
}
