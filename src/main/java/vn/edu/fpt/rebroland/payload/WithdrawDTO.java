package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class WithdrawDTO {
    private int id;

    private int type;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date startDate;

    @NotEmpty(message = "Nội dung không được để trống!")
    @Size(max = 200)
    private String content;

//    @NotEmpty(message = "Số tài khoản không được để trống!")
    @Pattern(regexp = "^[0-9]+$", message = "Số tài khoản ngân hàng là dãy các chữ số!")
    private String accountNumber;

    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Tên tài khoản chỉ chứa ký tự!")
    private String accountName;

    private String bankName;

    @NotNull(message = "Số tiền không được để trống!")
    @Min(value = 1)
    private long money;

    private int status;

    private String comment;

    private UserDTO user;

    private String token;
}
