package vn.edu.fpt.rebroland.payload;

import vn.edu.fpt.rebroland.entity.Coordinate;
import vn.edu.fpt.rebroland.entity.Post;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CoordinateDTO {
    private int id;
    @Pattern(regexp = "^(-?\\d+(\\.\\d+)?),\\s*(-?\\d+(\\.\\d+)?)$", message = "Tọa độ phải có dạng 21.******")
    private BigDecimal longitude;
    @Pattern(regexp = "^(-?\\d+(\\.\\d+)?),\\s*(-?\\d+(\\.\\d+)?)$", message = "Tọa độ phải có dạng 21.******")
    private BigDecimal latitude;
}
