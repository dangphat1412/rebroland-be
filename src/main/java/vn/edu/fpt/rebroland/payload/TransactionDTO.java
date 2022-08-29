package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private int id;

    @Min(value = 10000, message = "Số tiền chuyển khoản phải lớn hơn 10000 VNĐ!")
    @Max(value = 50000000, message = "Số tiền chuyển khoản không được vượt quá 50000000 VNĐ!")
//    @Pattern(regexp = "^[0-9]+$", message = "Số tiền chỉ được chứa chữ số !")
    private long amount;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date startDate;
    private String description;
    private int typeId;

    private int discount;

    private UserDTO user;
    @Min(value = 0)
    private int numberOfPostedDay;
}
