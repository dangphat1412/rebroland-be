package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

@Data
public class ContactDTO {
    private int contactId;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date startDate;

    @NotNull(message = "Role không được trống")
    private int roleId;

//    @NotEmpty(message = "cap nhap thong tin nay")
    @Size(max = 200,message = "Content không vượt quá 200 ký tự")
    private String content;

    private boolean unread;

    private UserDTO userRequest;

    private UserDTO user;
    private PostDTO post;

    private SearchDTO shortPost;

}
