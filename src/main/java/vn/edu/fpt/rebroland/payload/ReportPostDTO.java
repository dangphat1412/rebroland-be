package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.Date;

@Data
public class ReportPostDTO {
    private int reportId;

    private int userId;
    private int postId;
    private int roleId;
    private String content;
    private Date date;

}
