package vn.edu.fpt.rebroland.service.impl;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.ResidentialHouseHistory;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.ResidentialHouseHistoryDTO;
import vn.edu.fpt.rebroland.repository.ResidentialHouseHistoryRepository;
import vn.edu.fpt.rebroland.service.ResidentialHouseHistoryService;

@Service
public class ResidentialHouseHistoryServiceImpl implements ResidentialHouseHistoryService {
    private ResidentialHouseHistoryRepository residentialHouseHistoryRepository;

    private ModelMapper modelMapper;

    public ResidentialHouseHistoryServiceImpl(ResidentialHouseHistoryRepository residentialHouseHistoryRepository, ModelMapper modelMapper) {
        this.residentialHouseHistoryRepository = residentialHouseHistoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResidentialHouseHistoryDTO createResidentialHouseHistory(ResidentialHouseHistoryDTO residentialHouseHistoryDTO) {
        ResidentialHouseHistory residentialHouseHistory = mapToEntity(residentialHouseHistoryDTO);
        ResidentialHouseHistory newResidentialHouseHistory = residentialHouseHistoryRepository.save(residentialHouseHistory);
        return mapToDTO(newResidentialHouseHistory);
    }

    @Override
    public ResidentialHouseHistoryDTO getResidentialHouseHistoryByBarcodeAndPlotNumberAndDate(String barcode, String plotNumber, String date) {
        ResidentialHouseHistory residentialHouseHistory = residentialHouseHistoryRepository.getResidentialLandHistoryByBarcodeAndPlotNumber(barcode, plotNumber,date);
        return mapToDTO(residentialHouseHistory);
    }

    @Override
    public void deleteResidentialHouseHistoryById(int id) {
        ResidentialHouseHistory residentialHouseHistory = residentialHouseHistoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("HouseHistoryId", "id", id));
        residentialHouseHistoryRepository.delete(residentialHouseHistory);
    }

    @Override
    public String updateResidentialHouseHistory(ResidentialHouseHistoryDTO residentialHouseHistoryDTO, int id) {
        ResidentialHouseHistory residentialHouseHistory = residentialHouseHistoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HouseHistory", "ID", id));
        residentialHouseHistory.setBarcode(residentialHouseHistoryDTO.getBarcode());
        residentialHouseHistory.setOwner(residentialHouseHistoryDTO.getOwner());
        residentialHouseHistory.setStartDate(residentialHouseHistoryDTO.getStartDate());
        residentialHouseHistory.setPhone(residentialHouseHistoryDTO.getPhone());
        residentialHouseHistory.setPlotNumber(residentialHouseHistory.getPlotNumber());
        residentialHouseHistoryRepository.save(residentialHouseHistory);
        return "update success";
    }


    private ResidentialHouseHistoryDTO mapToDTO(ResidentialHouseHistory residentialHouseHistory) {
        return modelMapper.map(residentialHouseHistory, ResidentialHouseHistoryDTO.class);
    }

    private ResidentialHouseHistory mapToEntity(ResidentialHouseHistoryDTO residentialHouseHistoryDTO) {
        return modelMapper.map(residentialHouseHistoryDTO, ResidentialHouseHistory.class);
    }


}