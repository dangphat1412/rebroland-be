package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class ReportDTO {
    private int reportId;

    private Integer postId;

    private Integer userReportedId;

    private int status;

    private int userReportId;

//    @NotEmpty(message = "Nội dung không được để trống!")
    @Size(max = 100,message = "Nội dung không vượt quá 100 ký tự")
    private String content;

    private List<String> images;

    private PostDTO post;

    private UserDTO user;

    private String comment;

    private int numberOfUserReport;
}
