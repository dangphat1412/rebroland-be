package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.ResidentialLandHistory;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.ResidentialLandHistoryDTO;
import vn.edu.fpt.rebroland.repository.ResidentialLandHistoryRepository;
import vn.edu.fpt.rebroland.service.ResidentialLandHistoryService;

@Service
public class ResidentialLandHistoryServiceImpl implements ResidentialLandHistoryService {

    private ResidentialLandHistoryRepository residentialLandHistoryRepository;

    private ModelMapper modelMapper;


    public ResidentialLandHistoryServiceImpl(ResidentialLandHistoryRepository residentialLandHistoryRepository, ModelMapper modelMapper) {
        this.residentialLandHistoryRepository = residentialLandHistoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResidentialLandHistoryDTO createResidentialLandHistory(ResidentialLandHistoryDTO residentialLandHistoryDTO) {
        ResidentialLandHistory residentialLandHistory = mapToEntity(residentialLandHistoryDTO);
        ResidentialLandHistory newResidentialLandHistory = residentialLandHistoryRepository.save(residentialLandHistory);
        return mapToDTO(newResidentialLandHistory);
    }

    @Override
    public ResidentialLandHistoryDTO getResidentialLandHistoryByBarcodeAndPlotNumberAndDate(String barcode, String plotNumber, String date) {
        ResidentialLandHistory residentialLandHistory = residentialLandHistoryRepository.getResidentialLandHistoryByBarcodeAndPlotNumberAndDate(barcode,plotNumber,date);
        return mapToDTO(residentialLandHistory);
    }

    @Override
    public void deleteResidentialLandHistoryById(int id) {
        ResidentialLandHistory residentialHouseHistory = residentialLandHistoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("LandHistoryId", "id", id));
        residentialLandHistoryRepository.delete(residentialHouseHistory);
    }

    @Override
    public String updateResidentialLandHistory(ResidentialLandHistoryDTO residentialLandHistoryDTO, int id) {
        ResidentialLandHistory residentialLandHistory = residentialLandHistoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("LandHistory", "ID", id));
        residentialLandHistory.setBarcode(residentialLandHistoryDTO.getBarcode());
        residentialLandHistory.setOwner(residentialLandHistoryDTO.getOwner());
        residentialLandHistory.setStartDate(residentialLandHistoryDTO.getStartDate());
        residentialLandHistory.setPhone(residentialLandHistoryDTO.getPhone());
        residentialLandHistory.setPlotNumber(residentialLandHistoryDTO.getPlotNumber());
        residentialLandHistoryRepository.save(residentialLandHistory);
        return "update success";
    }

    private ResidentialLandHistoryDTO mapToDTO(ResidentialLandHistory residentialLandHistory) {
        return modelMapper.map(residentialLandHistory, ResidentialLandHistoryDTO.class);
    }

    private ResidentialLandHistory mapToEntity(ResidentialLandHistoryDTO residentialLandHistoryDTO) {
        return modelMapper.map(residentialLandHistoryDTO, ResidentialLandHistory.class);
    }
}
