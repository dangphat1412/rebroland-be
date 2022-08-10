package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
public class UserDTO {
    private int id;

    //    @NotEmpty(message = "Vui lòng điền đầy đủ họ tên")
    private String fullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    //    @NotNull(message = "You must choose gender")
    private Boolean gender;

    private String address;

    //    @NotEmpty(message = "Ward should not be empty")
    private String ward;

    //    @NotEmpty(message = "District should not be empty")
    private String district;

    //    @NotEmpty(message = "City should not be empty")
    private String province;

    //    @NotEmpty(message = "Vui lòng điền số điện thoại")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String phone;

    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email không hợp lệ.")
    private String email;

    private String avatar;

    private int currentRole;

    private String description;

    private boolean block;

    private int unreadNotification;

    @Pattern(regexp = "(?:(?:http|https):\\/\\/)?(?:www.)?facebook.com\\/(?:(?:\\w)*#!\\/)?(?:pages\\/)?(?:[?\\w\\-]*\\/)?(?:profile.php\\?id=(?=\\d.*))?([\\w\\-]*)?", message = "Link facebook không hợp lệ !")
    private String facebookLink;

    @Pattern(regexp = "^(?:(?:http|https):\\/\\/)?(?:www.)?zalo.me\\/(84|0[3|5|7|8|9])+([0-9]{8})$", message = "Link zalo không hợp lệ!")
    private String zaloLink;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;

    private float avgRate;

    private boolean isBroker;

    @Max(message = "Số tiền vượt quá hạn mức của hệ thống !" ,value = 100000000)
    @Min(message = "Số tiền không được là số âm !" ,value = 0)
    private long accountBalance;

}
