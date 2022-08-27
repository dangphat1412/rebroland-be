package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Data
public class PriceDTO {
    private int id;

    private int typeId;

    @Min(value = 0, message = "Giá phải lớn hơn 0")
    private long price;

    private Date startDate;

    private boolean status;

    @Min(value = 0, message = "Giảm giá lớn hơn 0")
    @Max(value = 100, message = "Giảm giá không vượt quá 100")
    private int discount;

    private int unitDate;
}
