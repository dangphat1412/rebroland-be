package vn.edu.fpt.rebroland.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
public class ReportDetailDTO {
    private int detailId;

    private UserDTO user;

    @Size(max = 100,message = "Nội dung không vượt quá 100 ký tự")
    private String content;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date startDate;

    private int reportId;

    private Set<EvidenceDTO> evidences;
}
