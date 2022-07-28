package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Report;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ReportDTO;

public interface ReportService {
    ReportDTO createReport(ReportDTO reportPostDTO);

    void deleteReportByPostId(int postId);

    User getUserById(int id);
    User getUserByToken(String token);
}
