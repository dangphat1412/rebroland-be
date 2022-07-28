package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class CareDetailResponse {
    private int detailId;

    private String dateCreate;

    private String appointmentTime;


    private Float alertTime;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String description;

    @NotEmpty(message = "Cập nhập thông tin này.")
    private String type;

    @NotNull(message = "Cập nhập thông tin này.")
    private boolean status;
}
