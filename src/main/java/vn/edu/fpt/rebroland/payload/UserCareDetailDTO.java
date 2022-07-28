package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class UserCareDetailDTO {
    private int detailId;

    private Date dateCreate;

    private String dateAppointment;

    private String timeAppointment;

    private Date appointmentTime;


    private Float alertTime;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String description;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String type;

    @NotNull(message = "Cập nhập thông tin này.")
    private boolean status;

//    private UserCareDTO userCare;
}
