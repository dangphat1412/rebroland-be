package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ReportDTO;
import vn.edu.fpt.rebroland.payload.ReportDetailResponse;
import vn.edu.fpt.rebroland.payload.ReportResponse;
import vn.edu.fpt.rebroland.payload.SearchResponse;
import org.springframework.http.HttpStatus;

public interface ReportService {
    HttpStatus createReport(ReportDTO reportPostDTO);

    void deleteReportByPostId(int postId);

    User getUserById(int id);
    User getUserByToken(String token);
    SearchResponse getListReportPost(int pageNumber, int pageSize, String keyword, String sortValue);
    ReportDetailResponse getListDetailReport(int reportId, int pageNumber, int pageSize);
    ReportDetailResponse getListDetailReportUser(int userId, int pageNumber, int pageSize);
    ReportResponse getListReportUser(int pageNumber, int pageSize, String keyword, String sortValue);
    boolean acceptReportPost(int reportId, String comment);
    boolean rejectReportPost(int reportId);
    boolean acceptReportUser(int reportId, String comment);
    boolean rejectReportUser(int reportId);

}
