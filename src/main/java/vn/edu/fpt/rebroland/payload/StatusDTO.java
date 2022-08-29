package vn.edu.fpt.rebroland.payload;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class StatusDTO {
    private int id;

    private String name;


}
