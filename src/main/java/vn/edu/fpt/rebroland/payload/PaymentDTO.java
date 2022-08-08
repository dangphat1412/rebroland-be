package vn.edu.fpt.rebroland.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private int id;
    private int amount;
    private Date date;
    private String description;
    private int typeId;
    private UserDTO user;
}
