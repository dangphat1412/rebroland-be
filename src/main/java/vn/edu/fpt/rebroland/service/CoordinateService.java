package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.CoordinateDTO;
import vn.edu.fpt.rebroland.payload.RealEstateCoordinateDTO;

import java.util.List;

public interface CoordinateService {
    String createCoordinate(List<CoordinateDTO> listCoordinate, int postId);

     List<RealEstateCoordinateDTO> getCoordinateByPostId(int postId);

    void deleteCoordinateByPostId(int postId);

    String updateCoordinate(List<CoordinateDTO> coordinateList,int postId);
}
