package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Set;

@Data
public class ReportDetailDTO {
    private int detailId;

    private UserDTO user;

    @NotEmpty(message = "Nội dung không được để trống!")
    private String content;

    private Date startDate;

    private int reportId;

    private Set<EvidenceDTO> evidences;
}
