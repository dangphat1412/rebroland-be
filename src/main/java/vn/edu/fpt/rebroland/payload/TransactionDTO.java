package vn.edu.fpt.rebroland.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private int id;
    private long amount;
    private Date startDate;
    private Date endDate;
    private String description;
    private int typeId;
    private UserDTO user;

    private int numberOfPostedDay;
}
