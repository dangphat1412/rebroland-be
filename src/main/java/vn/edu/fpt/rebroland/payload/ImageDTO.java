package vn.edu.fpt.rebroland.payload;

import vn.edu.fpt.rebroland.entity.Image;
import vn.edu.fpt.rebroland.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private int id;
    private String image;

}
