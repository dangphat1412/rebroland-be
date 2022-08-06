package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UserCareDetailDTO {
    private int detailId;

    private Date dateCreate;

    private String dateAppointment;

    private String timeAppointment;

    private Date appointmentTime;


    private Integer alertTime;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String description;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String type;

    @NotNull(message = "Cập nhập thông tin này.")
    private boolean status;

    private UserCareDTO userCare;
}
