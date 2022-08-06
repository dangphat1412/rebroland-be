package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ReportDTO {
    private int reportId;

    private Integer postId;

    private Integer userReportedId;

    private int status;

    private int userReportId;

    @NotEmpty(message = "Nội dung không được để trống!")
    private String content;

    private List<String> images;

    private PostDTO post;

    private UserDTO user;

    private int numberOfUserReport;
}
