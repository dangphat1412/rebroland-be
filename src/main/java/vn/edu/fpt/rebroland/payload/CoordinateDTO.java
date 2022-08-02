package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoordinateDTO {
    private int id;

    private BigDecimal longitude;

    private BigDecimal latitude;


}
