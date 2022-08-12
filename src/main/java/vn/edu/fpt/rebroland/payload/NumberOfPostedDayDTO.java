package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class NumberOfPostedDayDTO {

    @Min(value = 0,message = "Không được âm")
    private int numberOfPostedDay;
}
