package vn.edu.fpt.rebroland.service;


import vn.edu.fpt.rebroland.entity.ResidentialLand;
import vn.edu.fpt.rebroland.payload.ResidentialHouseHistoryDTO;
import vn.edu.fpt.rebroland.payload.ResidentialLandHistoryDTO;

public interface ResidentialLandHistoryService {

    ResidentialLandHistoryDTO createResidentialLandHistory
            (ResidentialLandHistoryDTO residentialLandHistoryDTO);

    ResidentialLandHistoryDTO getResidentialLandHistoryByBarcodeAndPlotNumberAndDate(String barcode, String plotNumber, String date);

    void deleteResidentialLandHistoryById(int id);

    String updateResidentialLandHistory(ResidentialLandHistoryDTO residentialLandHistoryDTO, int id);
}
