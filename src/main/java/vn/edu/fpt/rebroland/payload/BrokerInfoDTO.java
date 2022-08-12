package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.Date;

@Data
public class BrokerInfoDTO {
    private int id;

    private Date endDate;

    private int userId;
}
