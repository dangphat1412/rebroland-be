package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.ResidentialHouseDTO;
import vn.edu.fpt.rebroland.payload.ResidentialLandDTO;

public interface ResidentialLandService {
    ResidentialLandDTO createResidentialLand(ResidentialLandDTO residentialLandDTO, int postId);

    ResidentialLandDTO getResidentialLandByPostId(int postId);


    void deleteResidentialLandByPostId(int postId);

    ResidentialLandDTO updateResidentialLand(ResidentialLandDTO residentialLandDTO, int postId, int id);

}
