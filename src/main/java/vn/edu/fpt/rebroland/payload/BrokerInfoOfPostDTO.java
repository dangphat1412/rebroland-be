package vn.edu.fpt.rebroland.payload;

import lombok.Data;

@Data
public class BrokerInfoOfPostDTO {
    private UserDTO user;

    private int postId;
}
