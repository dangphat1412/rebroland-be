package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;

@Data
public class UserCareDetailDTO {
    private int detailId;


    @NotEmpty(message = "Cập nhập thông tin này.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b")
    private String phone;

    private Date dateCreate;

    private Date appointmentTime;

    @NotNull(message = "Cập nhập thông tin này.")
    private float alertTime;

    private String description;
    @NotNull(message = "Cập nhập thông tin này.")
    private boolean status;

    private UserCareDTO userCare;
}
