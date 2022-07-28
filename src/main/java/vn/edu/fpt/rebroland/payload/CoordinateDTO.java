package vn.edu.fpt.rebroland.payload;

import vn.edu.fpt.rebroland.entity.Coordinate;
import vn.edu.fpt.rebroland.entity.Post;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CoordinateDTO {
    private int id;

    private BigDecimal longitude;

    private BigDecimal latitude;


}
