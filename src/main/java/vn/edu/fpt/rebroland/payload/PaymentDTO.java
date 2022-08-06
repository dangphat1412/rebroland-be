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
    private String description;
    private Date date;
    private String type;
    private UserDTO user;
//    private String bankCode;
}
