package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.ResidentialLandHistory;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.HistoryDTO;
import vn.edu.fpt.rebroland.payload.ResidentialLandHistoryDTO;
import vn.edu.fpt.rebroland.repository.ResidentialLandHistoryRepository;
import vn.edu.fpt.rebroland.service.ResidentialLandHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
        ResidentialLandHistory history = residentialLandHistoryRepository.getResidentialLandHistoryByBarcode(residentialLandHistoryDTO.getBarcode());
        if(history != null){
            return null;
        }

        ResidentialLandHistory residentialLandHistory = mapToEntity(residentialLandHistoryDTO);
        ResidentialLandHistory newResidentialLandHistory = residentialLandHistoryRepository.save(residentialLandHistory);
        return mapToDTO(newResidentialLandHistory);
    }

    @Override
    public ResidentialLandHistoryDTO getResidentialLandHistoryByBarcodeAndPlotNumberAndDate(String barcode, int plotNumber, String date) {
        ResidentialLandHistory residentialLandHistory = residentialLandHistoryRepository.getResidentialLandHistoryByBarcodeAndPlotNumberAndDate(barcode,plotNumber,date);
        return mapToDTO(residentialLandHistory);
    }

    @Override
    public ResidentialLandHistoryDTO getResidentialLandHistoryByBarcode(String barcode) {
        ResidentialLandHistory residentialLandHistory = residentialLandHistoryRepository.getResidentialLandHistoryByBarcode(barcode);
        if(residentialLandHistory != null){
            return mapToDTO(residentialLandHistory);
        }else {
            return null;
        }
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

    @Override
    public void setDataToResidentialLandHistoryDTO(ResidentialLandHistoryDTO residentialLandHistoryDTO, HistoryDTO historyDTO) {
        residentialLandHistoryDTO.setBarcode(historyDTO.getBarcode());
        residentialLandHistoryDTO.setOwner(historyDTO.getOwner());
        residentialLandHistoryDTO.setPhone(historyDTO.getPhone());
        residentialLandHistoryDTO.setPlotNumber(historyDTO.getPlotNumber());


        String startDate = "";
        if (historyDTO.getBarcode().length() == 13) {
            startDate = "20" + historyDTO.getBarcode().substring(5, 7);
        } else {
            startDate = "20" + historyDTO.getBarcode().substring(7, 9);
        }
        residentialLandHistoryDTO.setStartDate(startDate);
    }

    private ResidentialLandHistoryDTO mapToDTO(ResidentialLandHistory residentialLandHistory) {
        return modelMapper.map(residentialLandHistory, ResidentialLandHistoryDTO.class);
    }

    private ResidentialLandHistory mapToEntity(ResidentialLandHistoryDTO residentialLandHistoryDTO) {
        return modelMapper.map(residentialLandHistoryDTO, ResidentialLandHistory.class);
    }
}
