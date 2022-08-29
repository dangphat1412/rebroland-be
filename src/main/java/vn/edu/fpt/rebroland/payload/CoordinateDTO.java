package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class CoordinateDTO {
    private int id;
    @Pattern(regexp = "^(-?\\d+(\\.\\d+)?),\\s*(-?\\d+(\\.\\d+)?)$", message = "Tọa độ phải có dạng 21.******")
    private BigDecimal longitude;
    @Pattern(regexp = "^(-?\\d+(\\.\\d+)?),\\s*(-?\\d+(\\.\\d+)?)$", message = "Tọa độ phải có dạng 21.******")
    private BigDecimal latitude;
}
