package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.ResidentialHouseHistoryDTO;

public interface ResidentialHouseHistoryService {
    ResidentialHouseHistoryDTO createResidentialHouseHistory
            (ResidentialHouseHistoryDTO residentialHouseHistoryDTO);

    ResidentialHouseHistoryDTO getResidentialHouseHistoryByBarcodeAndPlotNumberAndDate(String barcode, String plotNumber, String date);

    void deleteResidentialHouseHistoryById(int id);

    String updateResidentialHouseHistory(ResidentialHouseHistoryDTO residentialHouseHistoryDTO, int id);
}
