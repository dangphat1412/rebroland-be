package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class ListPrice {
    private List<PriceDTO> lists;
}
