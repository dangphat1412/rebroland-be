package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class ReportDetailResponse {
    private List<ReportDetailDTO> list;
    private int pageNo;
    private int totalPages;
    private Long totalResult;

}
