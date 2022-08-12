package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class WithdrawDTO {
    private int id;

    private int type;

    private Date startDate;
    @NotEmpty(message = "Nội dung không được để trống!")
    @Size(max = 200)
    private String content;

    private String accountNumber;

    private String accountName;

    private String bankName;

    @NotNull(message = "Số tiền không được để trống!")
    @Min(value = 0)
    private long money;

    private boolean status;

    private UserDTO user;

    private String token;
}
