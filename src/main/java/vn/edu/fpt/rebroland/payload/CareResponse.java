package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class CareResponse {
    private List<UserCareDTO> cares;
    private int pageNo;
    private int totalPages;
    private int totalResult;
}
