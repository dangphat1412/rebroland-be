package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private int id;

    private String phone;

    private int userId;

//    @NotEmpty(message = "Nội dung thông báo không được để trống!")
    private String content;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date date;
    private String type;
    private boolean unRead;
}
