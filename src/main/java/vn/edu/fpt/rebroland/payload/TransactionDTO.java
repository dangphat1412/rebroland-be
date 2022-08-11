package vn.edu.fpt.rebroland.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private int id;
    private long amount;
    private Date startDate;
    private String description;
    private int typeId;

    private int discount;

    private UserDTO user;
    @Min(value = 0)
    private int numberOfPostedDay;
}
