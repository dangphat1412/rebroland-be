package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Coordinate;
import vn.edu.fpt.rebroland.payload.CoordinateDTO;
import vn.edu.fpt.rebroland.payload.RealEstateCoordinateDTO;

import java.util.List;

public interface CoordinateService {
    String createCoordinate(List<Coordinate> listCoordinate, int postId);

     List<RealEstateCoordinateDTO> getCoordinateByPostId(int postId);

    void deleteCoordinateByPostId(int postId);

    String updateCoordinate(List<Coordinate> coordinateList,int postId);
}
