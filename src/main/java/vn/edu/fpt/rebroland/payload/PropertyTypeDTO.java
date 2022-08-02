package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PropertyTypeDTO {
    private int id;
    @NotEmpty(message = "Cập nhập thông tin này.")
    private String name;

}
