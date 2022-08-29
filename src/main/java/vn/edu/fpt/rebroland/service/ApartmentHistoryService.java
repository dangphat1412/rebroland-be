package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.ApartmentHistoryDTO;
import vn.edu.fpt.rebroland.payload.HistoryDTO;
import vn.edu.fpt.rebroland.payload.ResidentialHouseHistoryDTO;

public interface ApartmentHistoryService {
    ApartmentHistoryDTO createApartmentHistory(ApartmentHistoryDTO apartmentHistoryDTO);

    ApartmentHistoryDTO getApartmentHistoryByBarcodeAndBuildingNameAndDate(String barcode, String buildingName, String date);
    ApartmentHistoryDTO getApartmentHistoryByBarcode(String barcode);
    void deleteApartmentHistory(int id);

    String updateApartmentHistory(ApartmentHistoryDTO apartmentHistoryDTO, int id);

    void setDataToApartmentHistoryDTO(ApartmentHistoryDTO apartmentHistoryDTO, HistoryDTO historyDTO);
}
