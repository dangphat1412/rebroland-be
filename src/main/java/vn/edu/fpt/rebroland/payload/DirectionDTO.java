package vn.edu.fpt.rebroland.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class DirectionDTO {
    private Integer id;

    private String name;
}


