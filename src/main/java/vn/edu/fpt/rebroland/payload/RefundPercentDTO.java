package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.Date;

@Data
public class RefundPercentDTO {

    private int id;

    private int typeId;

    private int percent;

    private Date startDate;

    private boolean status;
}
