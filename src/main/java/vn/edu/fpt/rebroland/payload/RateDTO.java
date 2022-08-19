package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RateDTO {
    private int id;

    private UserDTO user;


    private String description;

    private float starRate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date startDate;

    private int userRated;

    private int userRoleRated;

}
