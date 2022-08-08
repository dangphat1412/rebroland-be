package vn.edu.fpt.rebroland.repository;

import vn.edu.fpt.rebroland.entity.ApartmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApartmentHistoryRepository extends JpaRepository<ApartmentHistory,Integer> {

    @Query(value = "SELECT * FROM apartment_history WHERE barcode = :barcode" +
            " AND building_name =:buildingName AND start_date =:startDate",nativeQuery = true)
    ApartmentHistory getApartmentHistoryByBarcodeAndBuildingName(String barcode, String buildingName, String startDate);

    @Query(value = "SELECT * FROM apartment_history WHERE barcode LIKE CONCAT(:barcode,'%') " +
            " AND building_name = :buildingName " +
            " AND room_number = :roomNumber " +
            " AND plot_number = :plotNumber " +
            " ORDER BY start_date DESC ",nativeQuery = true)
    List<ApartmentHistory> getApartmentHistoryByBarcode(String barcode, int plotNumber, String buildingName, String roomNumber);

}
