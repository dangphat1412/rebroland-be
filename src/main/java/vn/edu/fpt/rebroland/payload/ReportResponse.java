package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class ReportResponse {
    private List<ReportDTO> users;
    private int pageNo;
    private int totalPages;
    private Long totalResult;
}
