package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserRateDTO {
    private int id;

    private int userId;


    private String description;

    private float starRate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Bangkok")
    private Date startDate;

    private int userRated;

    private int userRoleRated;


}
