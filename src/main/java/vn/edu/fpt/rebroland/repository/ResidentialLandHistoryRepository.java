package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.ResidentialLandHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResidentialLandHistoryRepository extends JpaRepository<ResidentialLandHistory, Integer> {
    @Query(value = "SELECT * FROM residential_land_history " +
            " WHERE IF(CHAR_LENGTH(barcode) = 13, barcode LIKE CONCAT(:barcode,'%'), SUBSTRING(barcode, 3, 5) = :barcode) " +
            " AND plot_number =:plotNumber " +
            " ORDER BY start_date DESC ",nativeQuery = true)
    List<ResidentialLandHistory> getLandHistoryByBarcodeAndPlotNumber(String barcode, int plotNumber);

    @Query(value = "SELECT * FROM residential_land_history WHERE barcode = :barcode" +
            " AND plot_number =:plotNumber AND start_date =:startDate",nativeQuery = true)
    ResidentialLandHistory getResidentialLandHistoryByBarcodeAndPlotNumberAndDate(String barcode, int plotNumber, String startDate);

    @Query(value = "SELECT * FROM residential_land_history WHERE barcode = :barcode", nativeQuery = true)
    ResidentialLandHistory getResidentialLandHistoryByBarcode(String barcode);

}
