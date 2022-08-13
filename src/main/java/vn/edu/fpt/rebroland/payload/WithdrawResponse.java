package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class WithdrawResponse {
    private List<WithdrawDTO> lists;
    private int pageNo;
    private int totalPages;
    private Long totalResult;
}
