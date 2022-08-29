package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.ResidentialHouseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResidentialHouseHistoryRepository extends JpaRepository<ResidentialHouseHistory,Integer> {


    @Query(value = "SELECT * FROM residential_house_history WHERE barcode = :barcode" +
            " AND plot_number =:plotNumber AND start_date =:startDate",nativeQuery = true)
    ResidentialHouseHistory getResidentialLandHistoryByBarcodeAndPlotNumber(String barcode, int plotNumber, String startDate);

    @Query(value = "SELECT * FROM residential_house_history WHERE barcode = :barcode", nativeQuery = true)
    ResidentialHouseHistory getResidentialLandHistoryByBarcode(String barcode);


    @Query(value = "SELECT * FROM residential_house_history " +
            " WHERE IF(CHAR_LENGTH(barcode) = 13, barcode LIKE CONCAT(:barcode,'%'), SUBSTRING(barcode, 3, 5) = :barcode) " +
            " AND plot_number =:plotNumber " +
            " ORDER BY start_date DESC ",nativeQuery = true)
    List<ResidentialHouseHistory> getHouseHistoryByBarcodeAndPlotNumber(String barcode, int plotNumber);
}
