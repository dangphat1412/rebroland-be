package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Date;

@Data
public class ContactDTO {
    private int contactId;

    private Date startDate;

    @NotEmpty(message = "cap nhap thong tin nay")
    @Size(max = 200)
    private String content;

    private boolean unread;

    private int userRequestId;

    private UserDTO user;
    private ShortPostDTO shortPost;

}
