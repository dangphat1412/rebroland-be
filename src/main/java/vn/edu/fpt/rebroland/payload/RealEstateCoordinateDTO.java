package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RealEstateCoordinateDTO {
    private BigDecimal longitude;
    private BigDecimal latitude;
}
