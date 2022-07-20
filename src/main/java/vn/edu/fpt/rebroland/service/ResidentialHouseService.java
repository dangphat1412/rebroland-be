package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.ResidentialHouseDTO;

public interface ResidentialHouseService {
    ResidentialHouseDTO createResidentialHouse(ResidentialHouseDTO residentialHouseDTO, int postId);

    ResidentialHouseDTO getResidentialHouseByPostId(int postId);

    void deleteResidentialHouseByPostId(int postId);

    ResidentialHouseDTO updateResidentialHouse(ResidentialHouseDTO residentialHouseDTO, int postId, int id);
}
