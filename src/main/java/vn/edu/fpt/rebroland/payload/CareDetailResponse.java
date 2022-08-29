package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class CareDetailResponse {
    private int detailId;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Bangkok")
    private Date dateCreate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Bangkok")
    private Date appointmentTime;

    private Float alertTime;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String description;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String type;

    @NotNull(message = "Cập nhập thông tin này.")
    private boolean status;

    private UserCareDTO userCare;
}
