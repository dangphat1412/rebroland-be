package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class ListUserRate {
    private List<UserRateDTO> lists;
}
