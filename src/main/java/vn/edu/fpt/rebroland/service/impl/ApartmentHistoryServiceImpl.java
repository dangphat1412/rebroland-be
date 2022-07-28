package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Apartment;

import vn.edu.fpt.rebroland.entity.ApartmentHistory;
import vn.edu.fpt.rebroland.entity.ResidentialHouse;
import vn.edu.fpt.rebroland.entity.ResidentialHouseHistory;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.ApartmentHistoryDTO;
import vn.edu.fpt.rebroland.repository.ApartmentHistoryRepository;
import vn.edu.fpt.rebroland.repository.ApartmentRepository;
import vn.edu.fpt.rebroland.service.ApartmentHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ApartmentHistoryServiceImpl implements ApartmentHistoryService {
    private ApartmentHistoryRepository apartmentHistoryRepository;
    private ModelMapper modelMapper;


    public ApartmentHistoryServiceImpl(ApartmentHistoryRepository apartmentHistoryRepository, ModelMapper modelMapper) {
        this.apartmentHistoryRepository = apartmentHistoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ApartmentHistoryDTO createApartmentHistory(ApartmentHistoryDTO apartmentHistoryDTO) {
        ApartmentHistory apartmentHistory = mapToEntity(apartmentHistoryDTO);
        ApartmentHistory newApartmentHistory = apartmentHistoryRepository.save(apartmentHistory);
        return mapToDTO(newApartmentHistory);
    }

    @Override
    public ApartmentHistoryDTO getApartmentHistoryByBarcodeAndBuildingNameAndDate(String barcode, String buildingName, String date) {
        ApartmentHistory apartmentHistory = apartmentHistoryRepository.getApartmentHistoryByBarcodeAndBuildingName(barcode, buildingName, date);
        return mapToDTO(apartmentHistory);
    }

    @Override
    public void deleteApartmentHistory(int id) {
        ApartmentHistory apartmentHistory = apartmentHistoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("AparmentHistory", "id", id));
        apartmentHistoryRepository.delete(apartmentHistory);
    }

    @Override
    public String updateApartmentHistory(ApartmentHistoryDTO apartmentHistoryDTO, int id) {
        ApartmentHistory apartmentHistory = apartmentHistoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AparmentHistory", "ID", id));
        apartmentHistory.setBarcode(apartmentHistoryDTO.getBarcode());
        apartmentHistory.setOwner(apartmentHistoryDTO.getOwner());
        apartmentHistory.setStartDate(apartmentHistoryDTO.getStartDate());
        apartmentHistory.setPhone(apartmentHistoryDTO.getPhone());
        apartmentHistoryRepository.save(apartmentHistory);
        return "update success";

    }

    private ApartmentHistoryDTO mapToDTO(ApartmentHistory apartmentHistory) {
        return modelMapper.map(apartmentHistory, ApartmentHistoryDTO.class);
    }

    private ApartmentHistory mapToEntity(ApartmentHistoryDTO apartmentHistoryDTO) {
        return modelMapper.map(apartmentHistoryDTO, ApartmentHistory.class);
    }


}
