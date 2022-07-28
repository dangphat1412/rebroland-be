package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.ApartmentDTO;
import vn.edu.fpt.rebroland.payload.ResidentialHouseDTO;

public interface ApartmentService {
    ApartmentDTO createApartment(ApartmentDTO apartmentDTO, int postId);

    ApartmentDTO getApartmentByPostId(int postId);

    void deleteApartmentByPostId(int postId);

    ApartmentDTO updateApartment(ApartmentDTO apartmentDTO, int postId, int id);
}
