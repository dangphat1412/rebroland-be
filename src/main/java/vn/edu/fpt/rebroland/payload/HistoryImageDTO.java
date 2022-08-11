package vn.edu.fpt.rebroland.payload;

import lombok.Data;

@Data
public class HistoryImageDTO {
    private int id;

    private String image;

    private int typeId;

    private int historyId;
}
