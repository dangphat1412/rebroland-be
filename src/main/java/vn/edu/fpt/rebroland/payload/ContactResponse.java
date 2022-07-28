package vn.edu.fpt.rebroland.payload;

import lombok.Data;

import java.util.List;

@Data
public class ContactResponse {
    private List<ContactDTO> contacts;
    private int pageNo;
    private int totalPages;
    private int totalResult;

}
