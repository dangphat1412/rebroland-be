package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class NotificationDTO {
    private int id;

    private String phone;

    private int userId;

    @NotEmpty(message = "Nội dung thông báo không được để trống!")
    private String content;

    private Date date;
    private String type;
    private boolean unRead;
}
