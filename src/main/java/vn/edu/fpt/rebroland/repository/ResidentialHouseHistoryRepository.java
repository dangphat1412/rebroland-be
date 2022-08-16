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


    @Query(value = "SELECT * FROM residential_house_history WHERE barcode LIKE CONCAT(:barcode,'%') " +
            " AND plot_number =:plotNumber " +
            " ORDER BY start_date ASC ",nativeQuery = true)
    List<ResidentialHouseHistory> getHouseHistoryByBarcodeAndPlotNumber(String barcode, int plotNumber);
}
