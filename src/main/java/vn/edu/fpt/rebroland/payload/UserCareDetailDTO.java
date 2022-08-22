package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UserCareDetailDTO {
    private int detailId;

    private Date dateCreate;

    private String dateAppointment;

    private String timeAppointment;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date appointmentTime;


    private Integer alertTime;

    @NotEmpty(message = "Cập nhập thông tin này.")
    @Size(max = 200,message = "Phần mô tả không quá 200 ký tự!")
    private String description;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String type;

    @NotNull(message = "Cập nhập thông tin này.")
    private boolean status;

    private UserCareDTO userCare;
}
