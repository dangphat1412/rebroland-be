package vn.edu.fpt.rebroland.payload;

import vn.edu.fpt.rebroland.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class UserRateDTO {
    private int id;

    private int roleId;
    private int userId;
    private String description;

    private float starRate;

    private Date startDate;

    private int userRated;

    private int userRoleRated;


}
