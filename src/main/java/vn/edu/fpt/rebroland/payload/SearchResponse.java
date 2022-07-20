package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private List<SearchDTO> posts;
    private int pageNo;
    private int totalPages;

}
