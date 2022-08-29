package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.HistoryDTO;
import vn.edu.fpt.rebroland.payload.ResidentialHouseHistoryDTO;
import vn.edu.fpt.rebroland.payload.ResidentialLandHistoryDTO;

import java.sql.Date;

public interface ResidentialHouseHistoryService {
    ResidentialHouseHistoryDTO createResidentialHouseHistory
            (ResidentialHouseHistoryDTO residentialHouseHistoryDTO);

    ResidentialHouseHistoryDTO getResidentialHouseHistoryByBarcodeAndPlotNumberAndDate(String barcode, int plotNumber, String date);

    ResidentialHouseHistoryDTO getResidentialHouseHistoryByBarcode(String barcode);


    void deleteResidentialHouseHistoryById(int id);

    String updateResidentialHouseHistory(ResidentialHouseHistoryDTO residentialHouseHistoryDTO, int id);

    void setDataToResidentialHouseHistoryDTO(ResidentialHouseHistoryDTO residentialHouseHistoryDTO, HistoryDTO historyDTO);
}
