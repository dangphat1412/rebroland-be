package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.rebroland.entity.ResidentialLandHistory;

import java.util.List;

public interface ResidentialLandHistoryRepository extends JpaRepository<ResidentialLandHistory, Integer> {
    @Query(value = "SELECT * FROM residential_land_history WHERE barcode = :barcode" +
            " AND plot_number =:plotNumber " +
            " ORDER BY start_date ASC ",nativeQuery = true)
    List<ResidentialLandHistory> getLandHistoryByBarcodeAndPlotNumber(String barcode, String plotNumber);

    @Query(value = "SELECT * FROM residential_land_history WHERE barcode = :barcode" +
            " AND plot_number =:plotNumber AND start_date =:startDate",nativeQuery = true)
    ResidentialLandHistory getResidentialLandHistoryByBarcodeAndPlotNumberAndDate(String barcode, String plotNumber, String startDate);

}