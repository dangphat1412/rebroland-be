package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class ReportDTO {
    private int reportId;

    private int userId;
    private Integer postId;
    private int roleId;

    private Integer userReportedId;

    @NotEmpty(message = "Nội dung không được để trống!")
    private String content;

    private Date reportDate;

    private boolean status;
}
